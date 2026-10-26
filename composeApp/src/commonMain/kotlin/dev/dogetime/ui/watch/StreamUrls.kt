package dev.dogetime.ui.watch

/** Headers that stream servers commonly check before serving playlists and segments. */
internal val FORWARDED_HEADERS = listOf("Referer", "Origin", "User-Agent", "Cookie")

internal fun isHlsPlaylist(url: String): Boolean =
    url.substringBefore('#').substringBefore('?').endsWith(".m3u8", ignoreCase = true)

/** Keeps the [FORWARDED_HEADERS] from [raw], with their names spelled as in that list. */
internal fun forwardedHeaders(raw: Map<String, String>): LinkedHashMap<String, String> {
    val headers = linkedMapOf<String, String>()
    raw.forEach { (name, value) ->
        FORWARDED_HEADERS.firstOrNull { it.equals(name, ignoreCase = true) }?.let { headers[it] = value }
    }
    return headers
}
