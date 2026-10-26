package dev.dogetime.core

// libtorrent4j has no iOS build.
actual object Torrents {
    actual val isSupported: Boolean = false

    actual suspend fun open(
        magnet: String,
        fileName: String?,
        fileIndex: Int?,
        onProgress: (TorrentProgress) -> Unit,
    ): TorrentStream =
        throw UnsupportedOperationException("Magnet links can't be played on this platform.")
}
