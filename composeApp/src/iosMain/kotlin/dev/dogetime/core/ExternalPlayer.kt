package dev.dogetime.core

// Videos play inside the app's WebView on this platform.
actual object ExternalPlayer {
    actual val isSupported: Boolean = false

    actual val name: String? = null

    actual fun play(stream: CapturedStream, title: String): String? =
        "External playback isn't used on this platform."
}
