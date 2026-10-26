package dev.dogetime.core

import dev.dogetime.torrent.TorrentFile
import dev.dogetime.torrent.TorrentStreamServer
import dev.dogetime.torrent.prepareTorrentLibrary
import dev.dogetime.torrent.torrentDataDir
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.libtorrent4j.AddTorrentParams
import org.libtorrent4j.Priority
import org.libtorrent4j.SessionHandle
import org.libtorrent4j.SessionManager
import org.libtorrent4j.SessionParams
import org.libtorrent4j.TorrentFlags
import org.libtorrent4j.TorrentHandle
import org.libtorrent4j.TorrentInfo
import java.io.File

private const val METADATA_TIMEOUT_MS = 90_000L
private const val STATUS_POLL_MS = 1_000L
private const val METADATA_POLL_MS = 500L
private val VIDEO_EXTENSIONS = setOf("mp4", "mkv", "webm", "avi", "mov", "m4v", "ts", "wmv", "flv")

/** One libtorrent session for the whole app, started on the first magnet link. */
actual object Torrents {
    actual val isSupported: Boolean = true

    private val startLock = Mutex()
    private var session: SessionManager? = null

    /** Stopping and cleaning up blocks, so it runs here rather than on the caller's thread. */
    private val background = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private suspend fun session(): SessionManager = startLock.withLock {
        session ?: withContext(Dispatchers.IO) {
            prepareTorrentLibrary()
            // Nothing is kept between runs: torrents are only stored while they're watched.
            torrentDataDir().deleteRecursively()
            // libtorrent's default disk back-end memory-maps files and installs SIGSEGV/SIGBUS
            // handlers that kill the process on any fault they didn't cause. The JVM raises those
            // signals on purpose (null checks, safepoints), so the plain file back-end is used.
            val params = SessionParams().apply { setPosixDiskIO() }
            SessionManager().apply { start(params) }.also { session = it }
        }
    }

    actual suspend fun open(
        magnet: String,
        fileName: String?,
        fileIndex: Int?,
        onProgress: (TorrentProgress) -> Unit,
    ): TorrentStream {
        // Started first: parsing the link already needs the native library.
        val session = session()
        val hash = try {
            AddTorrentParams.parseMagnetUri(magnet.trim()).infoHashes.best
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("That isn't a valid magnet link.", e)
        }
        val saveDir = File(torrentDataDir(), hash.toHex()).apply { mkdirs() }

        return withContext(Dispatchers.IO) {
            session.download(magnet.trim(), saveDir, TorrentFlags.SEQUENTIAL_DOWNLOAD)
            var handle: TorrentHandle? = null
            try {
                val info = withTimeoutOrNull(METADATA_TIMEOUT_MS) {
                    var found: TorrentInfo? = null
                    while (found == null) {
                        // The torrent is added asynchronously, so it may not be there yet.
                        val current = handle ?: session.find(hash)
                        handle = current
                        if (current != null) {
                            val status = current.status()
                            onProgress(TorrentProgress(peers = status.numPeers()))
                            if (status.hasMetadata()) found = current.torrentFile()?.takeIf { it.isValid }
                        }
                        if (found == null) delay(METADATA_POLL_MS)
                    }
                    found
                } ?: throw IllegalStateException(
                    "No peers sent this torrent's file list. The magnet link may have no seeders left.",
                )
                val added = handle!!
                val index = videoFileIndex(info, fileName, fileIndex)
                    ?: throw IllegalStateException("This torrent has no video file.")

                // Only the video file is downloaded.
                added.prioritizeFiles(
                    Array(info.numFiles()) { if (it == index) Priority.DEFAULT else Priority.IGNORE },
                )
                val file = TorrentFile(added, info, index, saveDir)
                val server = TorrentStreamServer(file)
                val url = server.start()
                OpenTorrent(added, file, server, url)
            } catch (e: Throwable) {
                // Also reached when the user leaves before the video starts.
                (handle ?: session.find(hash))?.let { remove(it) }
                throw e
            }
        }
    }

    /** The file to play: [fileName], else [fileIndex], else the largest video file. */
    private fun videoFileIndex(info: TorrentInfo, fileName: String?, fileIndex: Int?): Int? {
        val files = info.files()
        val videos = (0 until files.numFiles())
            .filter { files.fileName(it).substringAfterLast('.').lowercase() in VIDEO_EXTENSIONS }
        return videos.firstOrNull { fileName != null && files.fileName(it) == fileName }
            ?: fileIndex?.takeIf { it in videos }
            ?: videos.maxByOrNull { files.fileSize(it) }
    }

    private fun remove(handle: TorrentHandle) {
        background.launch {
            session?.let { if (handle.isValid) it.remove(handle, SessionHandle.DELETE_FILES) }
        }
    }

    private class OpenTorrent(
        private val handle: TorrentHandle,
        private val file: TorrentFile,
        private val server: TorrentStreamServer,
        url: String,
    ) : TorrentStream {
        override val name: String = file.name
        override val stream = CapturedStream(url, emptyMap())

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val _progress = MutableStateFlow(TorrentProgress(hasMetadata = true))
        override val progress: StateFlow<TorrentProgress> = _progress

        init {
            scope.launch {
                while (isActive && handle.isValid) {
                    val status = handle.status()
                    _progress.value = TorrentProgress(
                        hasMetadata = true,
                        peers = status.numPeers(),
                        downloadRate = status.downloadRate().toLong(),
                        progress = status.progress(),
                    )
                    delay(STATUS_POLL_MS)
                }
            }
        }

        override fun close() {
            scope.cancel()
            background.launch {
                server.stop()
                file.close()
                remove(handle)
            }
        }
    }
}
