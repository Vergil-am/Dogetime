package dev.dogetime.torrent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.libtorrent4j.TorrentHandle
import org.libtorrent4j.TorrentInfo
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

private const val PIECE_POLL_MS = 100L
private const val READ_AHEAD_BYTES = 8L * 1024 * 1024
private const val DEADLINE_STEP_MS = 250

/**
 * One file of a torrent that is being downloaded. Reads wait until the bytes they need have
 * arrived, and move the download to wherever is being read.
 */
internal class TorrentFile(
    private val handle: TorrentHandle,
    info: TorrentInfo,
    private val index: Int,
    saveDir: File,
) {
    private val files = info.files()
    private val pieceLength = info.pieceLength().toLong()
    /** Where this file starts in the torrent's byte stream: pieces span file boundaries. */
    private val start = files.fileOffset(index)
    val size: Long = files.fileSize(index)
    val name: String = files.fileName(index)
    private val path = File(files.filePath(index, saveDir.absolutePath))
    private val lastPiece = pieceAt(size - 1)
    private val readAhead = ((READ_AHEAD_BYTES + pieceLength - 1) / pieceLength).toInt().coerceIn(2, 32)

    @Volatile
    private var channel: FileChannel? = null

    init {
        // MP4 often keeps its index at the end of the file, and MKV keeps its seek cues there,
        // so the end is fetched early along with the start.
        prioritize(0)
        handle.setPieceDeadline(lastPiece, 0)
        if (lastPiece > 0) handle.setPieceDeadline(lastPiece - 1, DEADLINE_STEP_MS)
    }

    private fun pieceAt(position: Long) = ((start + position) / pieceLength).toInt()

    /** Asks for the pieces from [position] onward to arrive first, soonest first. */
    private fun prioritize(position: Long) {
        val first = pieceAt(position)
        for (piece in first..minOf(first + readAhead - 1, lastPiece)) {
            if (!handle.havePiece(piece)) handle.setPieceDeadline(piece, (piece - first) * DEADLINE_STEP_MS)
        }
    }

    /**
     * Reads bytes at [position] into [buffer], waiting for their piece to arrive.
     * Stops at the end of that piece, since the next one may not have arrived yet.
     * Returns how many bytes were read.
     */
    suspend fun read(position: Long, buffer: ByteArray): Int = withContext(Dispatchers.IO) {
        val piece = pieceAt(position)
        prioritize(position)
        while (!handle.havePiece(piece)) delay(PIECE_POLL_MS)
        val pieceEnd = (piece + 1) * pieceLength - start
        val count = minOf(buffer.size.toLong(), pieceEnd - position, size - position).toInt()
        val target = ByteBuffer.wrap(buffer, 0, count)
        val file = openChannel()
        while (target.hasRemaining()) {
            val read = file.read(target, position + target.position())
            check(read >= 0) { "The downloaded file ended early." }
        }
        count
    }

    /** Opened on the first read: libtorrent only creates the file once data arrives. */
    private fun openChannel(): FileChannel =
        channel ?: synchronized(this) {
            channel ?: FileChannel.open(path.toPath(), StandardOpenOption.READ).also { channel = it }
        }

    fun close() {
        runCatching { channel?.close() }
    }
}
