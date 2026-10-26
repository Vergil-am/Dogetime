package dev.dogetime.ui.player

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference

/**
 * The parts of libmpv's C API ([client.h](https://github.com/mpv-player/mpv/blob/master/include/mpv/client.h)
 * and [render.h](https://github.com/mpv-player/mpv/blob/master/include/mpv/render.h)) the player uses.
 * String arrays are passed as NULL-terminated `char**`, as `mpv_command` expects.
 */
@Suppress("FunctionName")
internal interface MpvLibrary : Library {
    fun mpv_create(): Pointer?
    fun mpv_initialize(handle: Pointer): Int
    fun mpv_terminate_destroy(handle: Pointer)
    fun mpv_error_string(error: Int): String
    fun mpv_set_option_string(handle: Pointer, name: String, value: String): Int
    fun mpv_set_property_string(handle: Pointer, name: String, value: String): Int
    fun mpv_command(handle: Pointer, args: Array<String>): Int
    fun mpv_observe_property(handle: Pointer, userData: Long, name: String, format: Int): Int
    fun mpv_wait_event(handle: Pointer, timeout: Double): Pointer
    fun mpv_wakeup(handle: Pointer)

    fun mpv_render_context_create(result: PointerByReference, handle: Pointer, params: Pointer): Int
    fun mpv_render_context_set_update_callback(context: Pointer, callback: UpdateCallback?, data: Pointer?)
    fun mpv_render_context_update(context: Pointer): Long
    fun mpv_render_context_render(context: Pointer, params: Pointer): Int
    fun mpv_render_context_free(context: Pointer)

    /** Called by mpv on its own thread when a new frame can be rendered. Must not call mpv. */
    fun interface UpdateCallback : Callback {
        fun invoke(data: Pointer?)
    }
}

internal object Mpv {
    // mpv_format
    const val FORMAT_NONE = 0
    const val FORMAT_FLAG = 3
    const val FORMAT_INT64 = 4
    const val FORMAT_DOUBLE = 5

    // mpv_event_id
    const val EVENT_SHUTDOWN = 1
    const val EVENT_END_FILE = 7
    const val EVENT_FILE_LOADED = 8
    const val EVENT_PLAYBACK_RESTART = 21
    const val EVENT_PROPERTY_CHANGE = 22

    // mpv_end_file_reason
    const val END_FILE_REASON_ERROR = 4

    // mpv_render_param_type
    const val RENDER_PARAM_INVALID = 0
    const val RENDER_PARAM_API_TYPE = 1
    const val RENDER_PARAM_SW_SIZE = 17
    const val RENDER_PARAM_SW_FORMAT = 18
    const val RENDER_PARAM_SW_STRIDE = 19
    const val RENDER_PARAM_SW_POINTER = 20

    const val RENDER_UPDATE_FRAME = 1L

    // dlopen() mode on Linux: lazy binding, and without RTLD_GLOBAL (0x100) the library is local.
    private const val RTLD_LAZY = 1

    /** libmpv, or the reason it couldn't be loaded. */
    val library: Result<MpvLibrary> by lazy {
        runCatching {
            useCNumericLocale()
            val names = listOf("mpv", "mpv-2", "libmpv-2", "libmpv.so.2", "libmpv.so.1")
            var lastError: Throwable? = null
            for (name in names) {
                try {
                    return@runCatching Native.load(name, MpvLibrary::class.java, loadOptions())
                } catch (e: UnsatisfiedLinkError) {
                    lastError = e
                }
            }
            throw lastError ?: UnsatisfiedLinkError("libmpv not found")
        }
    }

    /**
     * On Linux, JNA loads libraries with RTLD_GLOBAL, which would make the system OpenSSL that
     * libmpv links (through ffmpeg) take precedence for every library loaded after it. libtorrent
     * bundles its own OpenSSL and exports its functions, so its calls would then be split between
     * the two copies, crashing it. RTLD_LAZY alone keeps libmpv's dependencies to itself.
     * macOS keeps each library's symbols apart already, and Windows has no such flags.
     */
    private fun loadOptions(): Map<String, Any> =
        if (System.getProperty("os.name").startsWith("Linux")) {
            mapOf(Library.OPTION_OPEN_FLAGS to RTLD_LAZY)
        } else {
            emptyMap()
        }

    /**
     * mpv refuses to start unless numbers are formatted the C way, and the JVM adopts the
     * system locale at startup (e.g. "0,5" in French), so the numeric locale is reset first.
     */
    private fun useCNumericLocale() {
        val os = System.getProperty("os.name")
        val lcNumeric = when {
            os.startsWith("Linux") -> 1
            os.startsWith("Mac") -> 4
            else -> return
        }
        runCatching { Native.load("c", CLibrary::class.java).setlocale(lcNumeric, "C") }
    }

    @Suppress("FunctionName")
    private interface CLibrary : Library {
        fun setlocale(category: Int, locale: String): String?
    }
}

/** A NULL-terminated `mpv_render_param[]`: `{ int type; void *data; }`, 16 bytes per entry on 64-bit. */
internal class RenderParams(vararg params: Pair<Int, Pointer?>) {
    val memory = Memory(16L * (params.size + 1)).apply { clear() }

    init {
        params.forEachIndexed { index, (type, data) ->
            memory.setInt(16L * index, type)
            memory.setPointer(16L * index + 8, data)
        }
        memory.setInt(16L * params.size, Mpv.RENDER_PARAM_INVALID)
    }
}

/** A C string kept alive as long as this object. */
internal fun cString(value: String): Memory =
    Memory(value.toByteArray().size + 1L).apply { setString(0, value) }
