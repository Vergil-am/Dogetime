package dev.dogetime.ui.player

import dev.dogetime.core.CapturedStream
import dev.dogetime.data.createHttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText

/**
 * Reads the qualities listed in [stream]'s HLS master playlist, with the stream's headers.
 * Each quality's [VideoQuality.key] is its variant playlist URL. Returns an empty list when the
 * playlist has a single quality or can't be read.
 */
internal suspend fun fetchHlsQualities(stream: CapturedStream): List<VideoQuality> {
    val client = createHttpClient()
    return try {
        val text = client.get(stream.url) {
            stream.headers.forEach { (name, value) -> header(name, value) }
        }.bodyAsText()
        parseHlsQualities(text, stream.url)
    } catch (e: Exception) {
        emptyList()
    } finally {
        client.close()
    }
}

/** Parses the `#EXT-X-STREAM-INF` entries of a master playlist; one entry per height, best bitrate first. */
internal fun parseHlsQualities(playlist: String, playlistUrl: String): List<VideoQuality> {
    val lines = playlist.lines().map { it.trim() }
    val variants = mutableListOf<VideoQuality>()
    for ((index, line) in lines.withIndex()) {
        if (!line.startsWith("#EXT-X-STREAM-INF:")) continue
        val uri = lines.drop(index + 1).firstOrNull { it.isNotEmpty() && !it.startsWith("#") } ?: continue
        val attributes = line.substringAfter(':')
        val height = RESOLUTION.find(attributes)?.groupValues?.get(2)?.toIntOrNull() ?: 0
        val bitrate = BANDWIDTH.find(attributes)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        variants += VideoQuality(height = height, bitrate = bitrate, key = resolveUrl(playlistUrl, uri))
    }
    return variants.bestPerHeight()
}

/** One quality per height (the best bitrate), best first; empty when there is no choice to make. */
internal fun List<VideoQuality>.bestPerHeight(): List<VideoQuality> =
    groupBy { it.height }
        .map { (_, sameHeight) -> sameHeight.maxBy { it.bitrate } }
        .sortedWith(compareByDescending<VideoQuality> { it.height }.thenByDescending { it.bitrate })
        .takeIf { it.size > 1 }
        .orEmpty()

private val RESOLUTION = Regex("""(?:^|,)RESOLUTION=(\d+)x(\d+)""")
private val BANDWIDTH = Regex("""(?:^|,)BANDWIDTH=(\d+)""")

/** Resolves a playlist entry against the playlist's own URL, as browsers resolve links. */
private fun resolveUrl(base: String, reference: String): String {
    if (reference.startsWith("http://") || reference.startsWith("https://")) return reference
    val scheme = base.substringBefore("://")
    if (reference.startsWith("//")) return "$scheme:$reference"
    val origin = scheme + "://" + base.substringAfter("://").substringBefore('/')
    if (reference.startsWith("/")) return origin + reference
    val directory = base.substringBefore('?').substringBefore('#').substringBeforeLast('/')
    return "$directory/$reference"
}
