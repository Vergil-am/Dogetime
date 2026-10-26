package dev.dogetime.core

import kotlinx.coroutines.flow.StateFlow

/** How a torrent is doing, for the loading screen. */
data class TorrentProgress(
    /** False until peers have sent the torrent's file list. */
    val hasMetadata: Boolean = false,
    val peers: Int = 0,
    /** Bytes per second. */
    val downloadRate: Long = 0,
    /** Share of the video file downloaded, 0 to 1. */
    val progress: Float = 0f,
)

/** A video file from a torrent, streamed over HTTP from inside the app while it downloads. */
interface TorrentStream {
    /** The file's name in the torrent. */
    val name: String

    /** A local `http://127.0.0.1` address that the player can read and seek in. */
    val stream: CapturedStream

    val progress: StateFlow<TorrentProgress>

    /** Stops the download, deletes what was downloaded and stops serving [stream]. */
    fun close()
}

/** Plays magnet links: downloads one of the torrent's video files in order and serves it to the player. */
expect object Torrents {
    /** False on platforms without a torrent engine (iOS). */
    val isSupported: Boolean

    /**
     * Adds [magnet] and waits until its video file can be streamed. [onProgress] is called while
     * the torrent's metadata is fetched. Throws with a message for the user if that fails.
     *
     * The file named [fileName] is played, or else the one at [fileIndex], for torrents of a whole
     * season; without either, or if neither is a video, the largest video file is played.
     */
    suspend fun open(
        magnet: String,
        fileName: String? = null,
        fileIndex: Int? = null,
        onProgress: (TorrentProgress) -> Unit,
    ): TorrentStream
}
