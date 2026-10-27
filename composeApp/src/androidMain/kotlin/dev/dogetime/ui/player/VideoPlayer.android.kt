package dev.dogetime.ui.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.dogetime.core.CapturedStream
import dev.dogetime.ui.watch.isHlsPlaylist
import `is`.xyz.mpv.MPV
import `is`.xyz.mpv.MPVNode

actual val playerUsesMouse: Boolean = false

private val mainHandler = Handler(Looper.getMainLooper())
private fun onUi(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        block()
    } else {
        mainHandler.post(block)
    }
}

/**
 * Plays streams with libmpv on Android, matching the desktop player's format support.
 */
internal class MpvAndroidPlayerController(
    private val context: Context,
    private val stream: CapturedStream,
    title: String,
) : VideoPlayerController(), MPV.EventObserver {

    val mpv = MPV()
    private var surfaceAttached = false
    private var fileLoaded = false

    private var started = false
    private var pausedForCache = false
    private var seeking = false

    init {
        try {
            `is`.xyz.mpv.Utils.copyAssets(context.applicationContext)

            val fontsConf = java.io.File(context.filesDir, "fonts.conf")
            if (!fontsConf.exists()) {
                fontsConf.writeText(
                    """
                    <?xml version="1.0"?>
                    <!DOCTYPE fontconfig SYSTEM "fonts.dtd">
                    <fontconfig>
                        <dir>/system/fonts</dir>
                        <dir>/system/font</dir>
                        <dir>/data/fonts</dir>
                        <dir>${context.filesDir.path}</dir>
                    </fontconfig>
                    """.trimIndent()
                )
            }

            mpv.create(context.applicationContext)
            mpv.setOptionString("config", "yes")
            mpv.setOptionString("config-dir", context.filesDir.path)
            mpv.setOptionString("gpu-shader-cache-dir", context.cacheDir.path)
            mpv.setOptionString("icc-cache-dir", context.cacheDir.path)

            mpv.setOptionString("vo", "gpu")
            mpv.setOptionString("gpu-context", "android")
            mpv.setOptionString("hwdec", "mediacodec-copy")
            mpv.setOptionString("keep-open", "yes")
            mpv.setOptionString("ytdl", "no")
            mpv.setOptionString("force-media-title", title)

            // Subtitle font & rendering configuration
            mpv.setOptionString("sub-fonts-dir", context.filesDir.path)
            mpv.setOptionString("sub-font", "Droid Sans Fallback")
            mpv.setOptionString("sub-visibility", "yes")
            mpv.setOptionString("sub-ass", "yes")
            mpv.setOptionString("sub-auto", "fuzzy")

            stream.headers["User-Agent"]?.let { mpv.setOptionString("user-agent", it) }
            stream.headers["Referer"]?.let { mpv.setOptionString("referrer", it) }
            val extraHeaders = stream.headers
                .filterKeys { k -> k != "User-Agent" && k != "Referer" }
                .entries.joinToString(",") { (k, v) -> "$k: $v" }
            if (extraHeaders.isNotEmpty()) {
                mpv.setOptionString("http-header-fields", extraHeaders)
            }

            mpv.init()
            mpv.setOptionString("force-window", "no")

            mpv.addObserver(this)
            mpv.observeProperty("pause", MPV.mpvFormat.MPV_FORMAT_FLAG)
            mpv.observeProperty("time-pos", MPV.mpvFormat.MPV_FORMAT_DOUBLE)
            mpv.observeProperty("duration", MPV.mpvFormat.MPV_FORMAT_DOUBLE)
            mpv.observeProperty("paused-for-cache", MPV.mpvFormat.MPV_FORMAT_FLAG)
            mpv.observeProperty("seeking", MPV.mpvFormat.MPV_FORMAT_FLAG)
            mpv.observeProperty("demuxer-cache-time", MPV.mpvFormat.MPV_FORMAT_DOUBLE)
            mpv.observeProperty("speed", MPV.mpvFormat.MPV_FORMAT_DOUBLE)
            mpv.observeProperty("mute", MPV.mpvFormat.MPV_FORMAT_FLAG)
            mpv.observeProperty("eof-reached", MPV.mpvFormat.MPV_FORMAT_FLAG)
            mpv.observeProperty("height", MPV.mpvFormat.MPV_FORMAT_INT64)
            mpv.observeProperty("sid", MPV.mpvFormat.MPV_FORMAT_STRING)
            mpv.observeProperty("track-list", MPV.mpvFormat.MPV_FORMAT_NONE)

            isPlaying = true
        } catch (e: Exception) {
            error = e.message ?: "mpv failed to start."
            isBuffering = false
        }
    }

    private var destroyed = false

    fun attachSurface(surface: Surface) {
        if (destroyed) return
        try {
            mpv.attachSurface(surface)
            mpv.setOptionString("force-window", "yes")
            mpv.setPropertyString("vo", "gpu")
            surfaceAttached = true
            if (!fileLoaded) {
                mpv.command("loadfile", stream.url)
                fileLoaded = true
            }
        } catch (e: Exception) {
            error = e.message ?: "Failed to attach surface."
        }
    }

    fun detachSurface() {
        if (destroyed || !surfaceAttached) return
        surfaceAttached = false
        try {
            mpv.detachSurface()
        } catch (_: Exception) {}
    }

    fun setFit(fit: VideoFit) {
        try {
            when (fit) {
                VideoFit.Fit -> {
                    mpv.setPropertyDouble("panscan", 0.0)
                    mpv.setPropertyString("video-aspect-override", "-1")
                }
                VideoFit.Fill -> {
                    mpv.setPropertyDouble("panscan", 1.0)
                    mpv.setPropertyString("video-aspect-override", "-1")
                }
                VideoFit.Stretch -> {
                    mpv.setPropertyDouble("panscan", 0.0)
                }
            }
        } catch (_: Exception) {}
    }

    fun release() {
        if (destroyed) return
        try {
            mpv.command("stop")
            detachSurface()
            destroyed = true
            mpv.removeObserver(this)
            
            // Destroy asynchronously to avoid libmpv mutex races with network threads
            val instance = mpv
            Thread {
                try {
                    Thread.sleep(300)
                    instance.destroy()
                } catch (_: Exception) {}
            }.start()
        } catch (_: Exception) {}
    }

    // ── VideoPlayerController ─────────────────────────────────────────────

    override fun play() {
        if (isEnded) mpv.command("seek", "0", "absolute")
        mpv.setPropertyBoolean("pause", false)
    }

    override fun pause() {
        mpv.setPropertyBoolean("pause", true)
    }

    override fun seekTo(positionMs: Long) {
        mpv.command("seek", (positionMs / 1000.0).toString(), "absolute")
        this.positionMs = positionMs
    }

    override fun setPlaybackSpeed(speed: Float) {
        mpv.setPropertyDouble("speed", speed.toDouble())
    }

    override fun setVolumeMuted(muted: Boolean) {
        mpv.setPropertyBoolean("mute", muted)
        isMuted = muted
    }

    override fun selectQuality(quality: VideoQuality?) {
        val target = quality?.key ?: return
        mpv.command("loadfile", target, "replace", "-1", "start=${positionMs / 1000.0}")
        selectedQuality = quality
    }

    override fun selectSubtitleTrack(track: SubtitleTrack?) {
        try {
            if (track == null) {
                mpv.command("set", "sid", "no")
            } else {
                mpv.command("set", "sid", track.id.toString())
                mpv.command("set", "sub-visibility", "yes")
            }
            selectedSubtitleTrack = track
        } catch (_: Exception) {}
    }

    private fun updateTracks() {
        try {
            val count = mpv.getPropertyInt("track-list/count") ?: 0
            val tracks = mutableListOf<SubtitleTrack>()
            var currentSid: Int? = mpv.getPropertyInt("sid")
            for (i in 0 until count) {
                val type = mpv.getPropertyString("track-list/$i/type")
                if (type == "sub") {
                    val id = mpv.getPropertyInt("track-list/$i/id") ?: (i + 1)
                    val title = mpv.getPropertyString("track-list/$i/title").orEmpty()
                    val lang = mpv.getPropertyString("track-list/$i/lang")
                    val selected = mpv.getPropertyBoolean("track-list/$i/selected") ?: false
                    if (selected) {
                        currentSid = id
                    }
                    tracks += SubtitleTrack(id = id, title = title, language = lang)
                }
            }
            subtitleTracks = tracks
            selectedSubtitleTrack = tracks.find { it.id == currentSid }
        } catch (_: Exception) {}
    }

    // ── MPV.EventObserver ─────────────────────────────────────────────────

    override fun eventProperty(property: String) {
        if (property == "track-list") {
            onUi { updateTracks() }
        }
    }

    override fun eventProperty(property: String, value: Boolean) {
        onUi {
            when (property) {
                "pause" -> isPlaying = !value
                "paused-for-cache" -> { pausedForCache = value; updateBuffering() }
                "seeking" -> { seeking = value; updateBuffering() }
                "mute" -> isMuted = value
                "eof-reached" -> isEnded = value
            }
        }
    }

    override fun eventProperty(property: String, value: Long) {
        onUi {
            when (property) {
                "height" -> videoHeight = value.toInt()
            }
        }
    }

    override fun eventProperty(property: String, value: Double) {
        onUi {
            when (property) {
                "time-pos" -> positionMs = (value * 1000).toLong()
                "duration" -> durationMs = (value * 1000).toLong()
                "demuxer-cache-time" -> bufferedMs = (value * 1000).toLong()
                "speed" -> speed = value.toFloat()
            }
        }
    }

    override fun eventProperty(property: String, value: String) {
        onUi {
            if (property == "sid") {
                val currentSid = value.toIntOrNull()
                selectedSubtitleTrack = subtitleTracks.find { it.id == currentSid }
            }
        }
    }

    override fun eventProperty(property: String, value: MPVNode) {}

    override fun event(eventId: Int, data: MPVNode) {
        onUi {
            when (eventId) {
                MPV.mpvEvent.MPV_EVENT_FILE_LOADED -> {
                    started = true
                    updateTracks()
                    updateBuffering()
                }
                MPV.mpvEvent.MPV_EVENT_PLAYBACK_RESTART -> {
                    updateTracks()
                    updateBuffering()
                }
                MPV.mpvEvent.MPV_EVENT_END_FILE -> isEnded = true
            }
        }
    }

    private fun updateBuffering() {
        if (error == null) isBuffering = !started || pausedForCache || seeking
    }
}

@Composable
actual fun rememberVideoPlayerController(stream: CapturedStream, title: String): VideoPlayerController {
    val context = LocalContext.current
    val controller = remember(stream) {
        MpvAndroidPlayerController(context.applicationContext, stream, title)
    }
    DisposableEffect(controller) {
        onDispose { controller.release() }
    }
    LaunchedEffect(controller) {
        if (isHlsPlaylist(stream.url)) {
            val list = fetchHlsQualities(stream)
            controller.setAvailableQualities(list)
        }
    }
    return controller
}

@Composable
actual fun VideoSurface(controller: VideoPlayerController, fit: VideoFit, modifier: Modifier) {
    val mpvController = controller as MpvAndroidPlayerController
    LaunchedEffect(fit) {
        mpvController.setFit(fit)
    }
    AndroidView(
        factory = { context ->
            SurfaceView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        mpvController.attachSurface(holder.surface)
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        mpvController.detachSurface()
                    }
                })
            }
        },
        modifier = modifier,
    )
}

/** The player always fills the screen on phones. */
@Composable
actual fun rememberFullscreenController(): FullscreenController? = null

@Composable
actual fun PlayerSystemUiEffect() {
    val view = LocalView.current
    val activity = LocalContext.current.findActivity() ?: return
    DisposableEffect(activity) {
        val insets = WindowCompat.getInsetsController(activity.window, view)
        val previousOrientation = activity.requestedOrientation
        insets.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insets.hide(WindowInsetsCompat.Type.systemBars())
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        view.keepScreenOn = true
        onDispose {
            insets.show(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = previousOrientation
            view.keepScreenOn = false
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
