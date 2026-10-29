package dev.dogetime.ui.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import dev.dogetime.core.CapturedStream
import dev.dogetime.core.PlayerLog
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Pixmap
import java.awt.EventQueue
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.min

/** Frames larger than this are scaled down by mpv, to bound the CPU cost of software rendering. */
private const val MAX_RENDER_WIDTH = 1920.0
private const val MAX_RENDER_HEIGHT = 1080.0

// User data numbers for the observed properties.
private const val PROP_PAUSE = 1L
private const val PROP_TIME_POS = 2L
private const val PROP_DURATION = 3L
private const val PROP_PAUSED_FOR_CACHE = 4L
private const val PROP_SEEKING = 5L
private const val PROP_CACHE_TIME = 6L
private const val PROP_SPEED = 7L
private const val PROP_MUTE = 8L
private const val PROP_EOF = 9L
private const val PROP_DWIDTH = 10L
private const val PROP_DHEIGHT = 11L
private const val PROP_HEIGHT = 12L
private const val PROP_TRACK_LIST = 13L

/**
 * Plays a stream with libmpv. mpv decodes (with hardware decoding when available) and renders
 * each frame in software into a Skia bitmap, which [VideoSurface] draws, so Compose controls can
 * be drawn over the video. Frames are double-buffered: mpv renders into the back bitmap while
 * the front one may be on screen, then they are swapped under [frameLock].
 */
internal class MpvVideoPlayerController(stream: CapturedStream, title: String) : VideoPlayerController() {

    /** Bumped for each new frame, so the surface redraws. */
    var frameVersion by mutableIntStateOf(0)
        private set

    /** Width / height of the video as displayed, or 0 before it is known. */
    var videoAspect by mutableFloatStateOf(0f)
        private set

    private var mpv: MpvLibrary? = null
    private var handle: Pointer? = null
    private var renderContext: Pointer? = null
    @Volatile private var running = true
    private var eventThread: Thread? = null
    private var renderThread: Thread? = null

    private val frameRequested = Semaphore(0)
    // Kept in a field: JNA only holds a weak reference to callbacks passed to native code.
    private val updateCallback = MpvLibrary.UpdateCallback { frameRequested.release() }
    // Byte order R, G, B, unused, matching Skia's RGB_888X, which ignores the unused byte
    // (mpv leaves it at 0, which an alpha channel would read as fully transparent).
    private val swFormat = cString("rgb0")

    private val frameLock = Any()
    private var front: Frame? = null
    private var back: Frame? = null
    @Volatile private var displayWidth = 0
    @Volatile private var displayHeight = 0

    // Raw mpv state, combined into isBuffering on the UI thread.
    private var started = false
    private var pausedForCache = false
    private var seeking = false

    /** The stream as captured, played when the quality is automatic. */
    private var masterUrl = ""

    init {
        try {
            start(stream, title)
        } catch (e: Exception) {
            PlayerLog.log("mpv failed to start: ${e.message}")
            error = e.message ?: "mpv failed to start."
            isBuffering = false
        }
    }

    private fun start(stream: CapturedStream, title: String) {
        val lib = Mpv.library.getOrElse {
            throw IllegalStateException("Couldn't load libmpv. Install mpv, which includes it, then restart DogeTime.", it)
        }
        mpv = lib
        val h = lib.mpv_create() ?: throw IllegalStateException("mpv couldn't be created.")
        handle = h

        fun option(name: String, value: String) {
            val result = lib.mpv_set_option_string(h, name, value)
            if (result < 0) PlayerLog.log("mpv option $name: ${lib.mpv_error_string(result)}")
        }
        option("vo", "libmpv")
        option("hwdec", "auto-copy")
        option("keep-open", "yes")
        option("ytdl", "no")
        option("force-media-title", title)
        option("log-file", PlayerLog.mpvLogFile.absolutePath)
        option("sub-visibility", "yes")
        option("sub-auto", "fuzzy")
        stream.headers["Referer"]?.let { option("referrer", it) }
        stream.headers["User-Agent"]?.let { option("user-agent", it) }
        check(lib, lib.mpv_initialize(h), "start mpv")

        // Header values may contain commas, so each one is appended as a whole list item.
        stream.headers
            .filterKeys { it != "Referer" && it != "User-Agent" }
            .forEach { (name, value) -> command("change-list", "http-header-fields", "append", "$name: $value") }

        val apiType = cString("sw")
        val result = PointerByReference()
        check(lib, lib.mpv_render_context_create(result, h, RenderParams(Mpv.RENDER_PARAM_API_TYPE to apiType).memory), "set up video output")
        val context = result.value
        renderContext = context
        lib.mpv_render_context_set_update_callback(context, updateCallback, null)

        listOf(
            PROP_PAUSE to ("pause" to Mpv.FORMAT_FLAG),
            PROP_TIME_POS to ("time-pos" to Mpv.FORMAT_DOUBLE),
            PROP_DURATION to ("duration" to Mpv.FORMAT_DOUBLE),
            PROP_PAUSED_FOR_CACHE to ("paused-for-cache" to Mpv.FORMAT_FLAG),
            PROP_SEEKING to ("seeking" to Mpv.FORMAT_FLAG),
            PROP_CACHE_TIME to ("demuxer-cache-time" to Mpv.FORMAT_DOUBLE),
            PROP_SPEED to ("speed" to Mpv.FORMAT_DOUBLE),
            PROP_MUTE to ("mute" to Mpv.FORMAT_FLAG),
            PROP_EOF to ("eof-reached" to Mpv.FORMAT_FLAG),
            PROP_DWIDTH to ("dwidth" to Mpv.FORMAT_INT64),
            PROP_DHEIGHT to ("dheight" to Mpv.FORMAT_INT64),
            PROP_HEIGHT to ("height" to Mpv.FORMAT_INT64),
            PROP_TRACK_LIST to ("track-list" to Mpv.FORMAT_NONE),
        ).forEach { (id, property) -> lib.mpv_observe_property(h, id, property.first, property.second) }

        eventThread = thread(name = "mpv-events", isDaemon = true) { eventLoop(lib, h) }
        renderThread = thread(name = "mpv-render", isDaemon = true) { renderLoop(lib, context) }

        PlayerLog.log("mpv (embedded) loading: ${stream.url}")
        masterUrl = stream.url
        command("loadfile", stream.url)
        isPlaying = true
    }

    private fun check(lib: MpvLibrary, result: Int, action: String) {
        if (result < 0) throw IllegalStateException("mpv couldn't $action: ${lib.mpv_error_string(result)}")
    }

    private fun command(vararg args: String) {
        val lib = mpv ?: return
        val h = handle ?: return
        if (!running) return
        val result = lib.mpv_command(h, arrayOf(*args))
        if (result < 0) PlayerLog.log("mpv ${args.first()}: ${lib.mpv_error_string(result)}")
    }

    private fun setProperty(name: String, value: String) {
        val lib = mpv ?: return
        val h = handle ?: return
        if (running) lib.mpv_set_property_string(h, name, value)
    }

    override fun play() {
        if (isEnded) command("seek", "0", "absolute")
        setProperty("pause", "no")
    }

    override fun pause() = setProperty("pause", "yes")

    override fun seekTo(positionMs: Long) {
        this.positionMs = positionMs
        command("seek", (positionMs / 1000.0).toString(), "absolute")
    }

    override fun setPlaybackSpeed(speed: Float) = setProperty("speed", speed.toString())

    override fun setVolumeMuted(muted: Boolean) = setProperty("mute", if (muted) "yes" else "no")

    /**
     * mpv plays the best variant of a master playlist and never switches, so a quality is picked
     * by loading its variant playlist instead, from the current position.
     */
    override fun selectQuality(quality: VideoQuality?) {
        if (quality == selectedQuality || masterUrl.isEmpty()) return
        selectedQuality = quality
        started = false
        updateBuffering()
        val url = quality?.key ?: masterUrl
        PlayerLog.log("mpv quality ${quality?.label ?: "auto"}: $url")
        // Arguments: URL, flags, playlist index (-1: unused), per-file options.
        command("loadfile", url, "replace", "-1", "start=${positionMs / 1000.0}")
    }

    override fun selectSubtitleTrack(track: SubtitleTrack?) {
        if (track == null) {
            command("set", "sid", "no")
            command("set", "sub-visibility", "no")
        } else {
            command("set", "sid", track.id.toString())
            command("set", "sub-visibility", "yes")
        }
        selectedSubtitleTrack = track
    }

    override fun addExternalSubtitle(path: String, title: String, language: String?) {
        // sub-add <url> [<flags> [<title> [<lang>]]]
        val args = mutableListOf("sub-add", path, "select", title)
        language?.let { args += it }
        command(*args.toTypedArray())
        command("set", "sub-visibility", "yes")
        val lib = mpv
        val h = handle
        if (lib != null && h != null) {
            updateTracks(lib, h)
        }
    }

    private fun getPropertyString(lib: MpvLibrary, h: Pointer, name: String): String? {
        val ptr = lib.mpv_get_property_string(h, name) ?: return null
        return try {
            ptr.getString(0)
        } finally {
            lib.mpv_free(ptr)
        }
    }

    private fun updateTracks(lib: MpvLibrary, h: Pointer) {
        val count = getPropertyString(lib, h, "track-list/count")?.toIntOrNull() ?: 0
        val tracks = mutableListOf<SubtitleTrack>()
        val currentSid = getPropertyString(lib, h, "sid")?.toIntOrNull()
        for (i in 0 until count) {
            val type = getPropertyString(lib, h, "track-list/$i/type")
            if (type == "sub") {
                val id = getPropertyString(lib, h, "track-list/$i/id")?.toIntOrNull() ?: (i + 1)
                val title = getPropertyString(lib, h, "track-list/$i/title").orEmpty()
                val lang = getPropertyString(lib, h, "track-list/$i/lang")
                tracks += SubtitleTrack(id = id, title = title, language = lang)
            }
        }
        onUi {
            subtitleTracks = tracks
            selectedSubtitleTrack = tracks.find { it.id == currentSid }
        }
    }

    /** Runs [block] with the newest frame, if any, while mpv can't replace it. */
    fun withFrame(block: (image: ImageBitmap, width: Int, height: Int) -> Unit) {
        synchronized(frameLock) {
            front?.let { block(it.image, it.width, it.height) }
        }
    }

    private fun eventLoop(lib: MpvLibrary, h: Pointer) {
        while (running) {
            val event = lib.mpv_wait_event(h, 1.0)
            when (event.getInt(0)) {
                Mpv.EVENT_SHUTDOWN -> return
                Mpv.EVENT_FILE_LOADED -> updateTracks(lib, h)
                Mpv.EVENT_PLAYBACK_RESTART -> {
                    updateTracks(lib, h)
                    onUi {
                        started = true
                        updateBuffering()
                    }
                }
                Mpv.EVENT_END_FILE -> {
                    val data = event.getPointer(16) ?: continue
                    if (data.getInt(0) == Mpv.END_FILE_REASON_ERROR) {
                        val message = lib.mpv_error_string(data.getInt(4))
                        PlayerLog.log("mpv playback failed: $message")
                        onUi {
                            error = "The stream couldn't be played ($message)."
                            isBuffering = false
                        }
                    }
                }
                Mpv.EVENT_PROPERTY_CHANGE -> {
                    val property = event.getPointer(16) ?: continue
                    onPropertyChange(event.getLong(8), property.getInt(8), property.getPointer(16))
                }
            }
        }
    }

    /** Reads an `mpv_event_property` value and applies it on the UI thread. */
    private fun onPropertyChange(id: Long, format: Int, data: Pointer?) {
        if (id == PROP_TRACK_LIST) {
            val lib = mpv
            val h = handle
            if (lib != null && h != null) updateTracks(lib, h)
            return
        }
        val flag = data != null && format == Mpv.FORMAT_FLAG && data.getInt(0) != 0
        val double = if (data != null && format == Mpv.FORMAT_DOUBLE) data.getDouble(0) else null
        val long = if (data != null && format == Mpv.FORMAT_INT64) data.getLong(0) else null
        when (id) {
            PROP_DWIDTH -> displayWidth = long?.toInt() ?: 0
            PROP_DHEIGHT -> displayHeight = long?.toInt() ?: 0
        }
        onUi {
            when (id) {
                PROP_PAUSE -> isPlaying = !flag
                PROP_TIME_POS -> double?.let { positionMs = (it * 1000).toLong() }
                PROP_DURATION -> durationMs = ((double ?: 0.0) * 1000).toLong()
                PROP_PAUSED_FOR_CACHE -> pausedForCache = flag
                PROP_SEEKING -> seeking = flag
                PROP_CACHE_TIME -> bufferedMs = ((double ?: 0.0) * 1000).toLong()
                PROP_SPEED -> double?.let { speed = it.toFloat() }
                PROP_MUTE -> isMuted = flag
                PROP_HEIGHT -> videoHeight = long?.toInt() ?: 0
                PROP_EOF -> isEnded = flag
                PROP_DWIDTH, PROP_DHEIGHT -> {
                    val w = displayWidth
                    val h = displayHeight
                    videoAspect = if (w > 0 && h > 0) w.toFloat() / h else 0f
                }
            }
            updateBuffering()
        }
    }

    private fun updateBuffering() {
        if (error == null) isBuffering = !started || pausedForCache || seeking
    }

    private fun renderLoop(lib: MpvLibrary, context: Pointer) {
        while (running) {
            if (!frameRequested.tryAcquire(200, TimeUnit.MILLISECONDS)) continue
            frameRequested.drainPermits()
            if (!running) return
            val flags = lib.mpv_render_context_update(context)
            if (flags and Mpv.RENDER_UPDATE_FRAME != 0L) renderFrame(lib, context)
        }
    }

    private fun renderFrame(lib: MpvLibrary, context: Pointer) {
        val sourceWidth = displayWidth
        val sourceHeight = displayHeight
        if (sourceWidth <= 0 || sourceHeight <= 0) return
        val scale = min(1.0, min(MAX_RENDER_WIDTH / sourceWidth, MAX_RENDER_HEIGHT / sourceHeight))
        val width = (sourceWidth * scale).toInt().coerceAtLeast(2)
        val height = (sourceHeight * scale).toInt().coerceAtLeast(2)

        // The back frame is never on screen, so it can be replaced or drawn into without the lock.
        val target = back?.takeIf { it.width == width && it.height == height }
            ?: Frame(width, height).also {
                back?.close()
                back = it
            }
        lib.mpv_render_context_render(context, target.renderParams.memory)
        target.bitmap.notifyPixelsChanged()
        synchronized(frameLock) {
            back = front
            front = target
        }
        onUi { frameVersion++ }
    }

    fun release() {
        if (!running) return
        running = false
        frameRequested.release()
        val lib = mpv ?: return
        val h = handle
        h?.let(lib::mpv_wakeup)
        // mpv can take a moment to shut down, so it isn't done on the UI thread.
        thread(name = "mpv-release", isDaemon = true) {
            eventThread?.join()
            renderThread?.join()
            renderContext?.let {
                lib.mpv_render_context_set_update_callback(it, null, null)
                lib.mpv_render_context_free(it)
            }
            h?.let(lib::mpv_terminate_destroy)
            synchronized(frameLock) {
                front?.close()
                back?.close()
                front = null
                back = null
            }
            PlayerLog.log("mpv (embedded) released")
        }
    }

    private inline fun onUi(crossinline block: () -> Unit) = EventQueue.invokeLater { block() }

    /** A bitmap mpv renders into directly, with the render parameters pointing at its pixels. */
    private inner class Frame(val width: Int, val height: Int) {
        val bitmap = Bitmap().also {
            check(it.allocPixels(ImageInfo(width, height, ColorType.RGB_888X, ColorAlphaType.OPAQUE))) {
                "Couldn't allocate a ${width}x$height frame"
            }
        }
        val image: ImageBitmap = bitmap.asComposeImageBitmap()
        private val pixels: Pixmap = checkNotNull(bitmap.peekPixels()) { "Bitmap has no pixels" }
        private val size = Memory(8).apply {
            setInt(0, width)
            setInt(4, height)
        }
        private val stride = Memory(8).apply { setLong(0, pixels.rowBytes.toLong()) }
        val renderParams = RenderParams(
            Mpv.RENDER_PARAM_SW_SIZE to size,
            Mpv.RENDER_PARAM_SW_FORMAT to swFormat,
            Mpv.RENDER_PARAM_SW_STRIDE to stride,
            Mpv.RENDER_PARAM_SW_POINTER to Pointer(pixels.addr),
        )

        fun close() {
            pixels.close()
            bitmap.close()
        }
    }
}
