package dev.dogetime.core

import java.io.File
import java.time.LocalTime

/**
 * Plain-text log of what the desktop player captures and hands to mpv, for troubleshooting.
 * Written to ~/.dogetime/player.log and cleared each time the app starts.
 */
internal object PlayerLog {
    private val dir = File(System.getProperty("user.home"), ".dogetime").apply { mkdirs() }
    private val file = File(dir, "player.log").apply { writeText("") }

    /** mpv writes its own log here (see ExternalPlayer). */
    val mpvLogFile: File = File(dir, "mpv.log")

    @Synchronized
    fun log(message: String) {
        runCatching { file.appendText("${LocalTime.now()} $message\n") }
    }
}
