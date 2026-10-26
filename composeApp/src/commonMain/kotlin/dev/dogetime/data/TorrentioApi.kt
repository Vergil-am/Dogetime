package dev.dogetime.data

import dev.dogetime.domain.TorrentOption
import dev.dogetime.domain.WatchTarget
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.Serializable

private const val BASE_URL = "https://torrentio.strem.fun/stream"

// Torrentio results carry no trackers; these well-known public ones find peers faster than DHT alone.
private val TRACKERS = listOf(
    "udp://tracker.opentrackr.org:1337/announce",
    "udp://open.demonii.com:1337/announce",
    "udp://open.stealth.si:80/announce",
    "udp://tracker.torrent.eu.org:451/announce",
    "udp://exodus.desync.com:6969/announce",
    "udp://tracker.openbittorrent.com:6969/announce",
)

/** Torrentio (a Stremio add-on) lists torrents for a movie or episode by its IMDb id. */
class TorrentioApi(private val client: HttpClient) {

    /** Torrents for [target], best quality first. Throws if [target] has no IMDb id. */
    suspend fun streams(target: WatchTarget): List<TorrentOption> {
        val imdbId = requireNotNull(target.imdbId) { "This title has no IMDb id, which torrents are looked up by." }
        val path = when (target) {
            is WatchTarget.Movie -> "movie/$imdbId.json"
            is WatchTarget.Episode -> "series/$imdbId:${target.season}:${target.episode}.json"
        }
        val response: TorrentioResponse = client.get("$BASE_URL/$path").body()
        return response.streams.mapNotNull { it.toDomain() }
    }
}

@Serializable
private data class TorrentioResponse(val streams: List<TorrentioStream> = emptyList())

@Serializable
private data class TorrentioStream(
    /** "Torrentio\n1080p" */
    val name: String = "",
    /** Release name, then (for packs) the file's name, then "👤 12 💾 1.4 GB ⚙️ Site". */
    val title: String = "",
    val infoHash: String? = null,
    val fileIdx: Int? = null,
    val behaviorHints: TorrentioHints? = null,
)

@Serializable
private data class TorrentioHints(val filename: String? = null)

private val SEEDERS = Regex("""👤\s*(\d+)""")
private val SIZE = Regex("""💾\s*([\d.,]+\s*[KMGT]?B)""")
private val SITE = Regex("""⚙️\s*(.+)""")

private fun TorrentioStream.toDomain(): TorrentOption? {
    val hash = infoHash?.takeIf { it.isNotBlank() } ?: return null
    val lines = title.lines().map { it.trim() }.filter { it.isNotEmpty() }
    val stats = lines.lastOrNull { "👤" in it || "💾" in it }.orEmpty()
    val fileName = behaviorHints?.filename
    val release = lines.firstOrNull { it != stats } ?: fileName ?: hash
    val magnet = buildString {
        append("magnet:?xt=urn:btih:").append(hash)
        append("&dn=").append((fileName ?: release).encodeURLParameter())
        TRACKERS.forEach { append("&tr=").append(it.encodeURLParameter()) }
    }
    return TorrentOption(
        // Some results are named just "Torrentio", with no quality line.
        quality = name.substringAfter('\n', missingDelimiterValue = "").trim().ifEmpty { "Unknown" },
        release = release,
        seeders = SEEDERS.find(stats)?.groupValues?.get(1)?.toIntOrNull(),
        size = SIZE.find(stats)?.groupValues?.get(1),
        site = SITE.find(stats)?.groupValues?.get(1)?.trim(),
        magnet = magnet,
        fileIndex = fileIdx,
        fileName = fileName,
    )
}
