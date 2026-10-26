package dev.dogetime.core

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import dev.dogetime.AndroidApp

/** Opens captured streams in a video player app (MX Player, VLC, mpv-android, …). */
actual object ExternalPlayer {
    actual val isSupported: Boolean = true

    actual val name: String? = "your video player"

    actual fun play(stream: CapturedStream, title: String): String? {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(stream.url), HLS_MIME_TYPE)
            putExtra("title", title)
            // MX Player's format, also read by other players: names and values, alternating.
            putExtra("headers", stream.headers.flatMap { (name, value) -> listOf(name, value) }.toTypedArray())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            AndroidApp.context.startActivity(intent)
            null
        } catch (e: ActivityNotFoundException) {
            "No video player found. Install MX Player or VLC to watch videos."
        }
    }
}

// Players disagree on the HLS type they accept (and Android matches it case-sensitively), but
// they all accept any video and detect HLS from the stream itself.
private const val HLS_MIME_TYPE = "video/*"
