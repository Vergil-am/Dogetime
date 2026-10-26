package dev.dogetime.core

/** A video stream the player page requested, with the headers its server expects. */
data class CapturedStream(
    val url: String,
    val headers: Map<String, String>,
)

/**
 * Plays captured streams in another app, as an alternative to the app's own player: in mpv on
 * desktop and in a video player app on Android. iOS plays the stream in the WebView itself.
 */
expect object ExternalPlayer {
    /** True on platforms that capture streams from the player page (and can hand them to another app). */
    val isSupported: Boolean

    /** Name of the installed player, or null if none was found. */
    val name: String?

    /**
     * Starts playback of [stream], replacing any playback this app started before.
     * Returns an error message, or null on success.
     */
    fun play(stream: CapturedStream, title: String): String?
}
