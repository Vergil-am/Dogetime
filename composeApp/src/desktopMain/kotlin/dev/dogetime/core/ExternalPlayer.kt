package dev.dogetime.core

import java.io.File
import java.lang.ProcessBuilder.Redirect

/** Plays captured streams in mpv, which decodes H.264/AAC that the bundled Chromium can't. */
actual object ExternalPlayer {
    actual val isSupported: Boolean = true

    private val mpv: File? by lazy { findExecutable("mpv") }

    actual val name: String?
        get() = if (mpv != null) "mpv" else null

    /** The mpv started by this app, so "play again" replaces it. */
    private var process: Process? = null

    actual fun play(stream: CapturedStream, title: String): String? {
        val executable = mpv ?: return "Install mpv to watch videos on desktop."
        val command = buildList {
            add(executable.absolutePath)
            add("--force-media-title=$title")
            add("--log-file=${PlayerLog.mpvLogFile.absolutePath}")
            stream.headers["Referer"]?.let { add("--referrer=$it") }
            stream.headers["User-Agent"]?.let { add("--user-agent=$it") }
            stream.headers
                .filterKeys { it != "Referer" && it != "User-Agent" }
                .forEach { (name, value) -> add("--http-header-fields-append=$name: $value") }
            add("--")
            add(stream.url)
        }
        process?.takeIf { it.isAlive }?.destroy()
        PlayerLog.log("mpv start: ${stream.url}")
        return runCatching {
            process = ProcessBuilder(command)
                .redirectOutput(Redirect.DISCARD)
                .redirectError(Redirect.DISCARD)
                .start()
        }.exceptionOrNull()?.let { "Couldn't start mpv: ${it.message}" }
    }
}

private fun findExecutable(name: String): File? {
    val fileName = if (System.getProperty("os.name").startsWith("Windows")) "$name.exe" else name
    return System.getenv("PATH").orEmpty()
        .split(File.pathSeparator)
        .map { File(it, fileName) }
        .firstOrNull { it.isFile && it.canExecute() }
}
