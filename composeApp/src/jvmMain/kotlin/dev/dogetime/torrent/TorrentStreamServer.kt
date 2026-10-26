package dev.dogetime.torrent

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.ranges
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.writeFully

private const val CHUNK_BYTES = 256 * 1024

/**
 * Serves [file] at a local address, answering the Range requests players send to start, seek
 * and read ahead. Each request reads the torrent from where it asks, so seeking just works.
 */
internal class TorrentStreamServer(private val file: TorrentFile) {
    private val path = "/video." + file.name.substringAfterLast('.', "mp4").lowercase()

    private val server = embeddedServer(CIO, host = "127.0.0.1", port = 0) {
        routing {
            get(path) {
                val size = file.size
                val requested = call.request.ranges()
                val range = requested?.merge(size)?.firstOrNull()
                if (requested != null && range == null) {
                    call.response.header(HttpHeaders.ContentRange, "bytes */$size")
                    call.respond(HttpStatusCode.RequestedRangeNotSatisfiable)
                    return@get
                }
                val from = range?.first ?: 0L
                val to = range?.last ?: (size - 1)

                call.response.header(HttpHeaders.AcceptRanges, "bytes")
                if (range != null) call.response.header(HttpHeaders.ContentRange, "bytes $from-$to/$size")
                // A player that seeks closes this connection, which ends the loop with an exception.
                call.respondBytesWriter(
                    contentType = ContentType.defaultForFileExtension(path.substringAfterLast('.')),
                    status = if (range != null) HttpStatusCode.PartialContent else HttpStatusCode.OK,
                    contentLength = to - from + 1,
                ) {
                    val buffer = ByteArray(CHUNK_BYTES)
                    var position = from
                    while (position <= to) {
                        val read = file.read(position, buffer)
                        val count = minOf(read.toLong(), to - position + 1).toInt()
                        writeFully(buffer, 0, count)
                        position += count
                    }
                }
            }
        }
    }

    /** Starts the server and returns the address to hand to the player. */
    suspend fun start(): String {
        server.startSuspend(wait = false)
        val port = server.engine.resolvedConnectors().first().port
        return "http://127.0.0.1:$port$path"
    }

    fun stop() = server.stop(gracePeriodMillis = 0, timeoutMillis = 1_000)
}
