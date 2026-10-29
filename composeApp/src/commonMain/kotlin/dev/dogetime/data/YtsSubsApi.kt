package dev.dogetime.data

import dev.dogetime.domain.SubtitleGroup
import dev.dogetime.domain.WatchTarget
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes

private const val BASE_URL = "https://yts-subs.com"
private const val USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

/** Scrapes yts-subs.com for subtitles matching a movie's IMDb id using Jsoup. */
class YtsSubsApi(private val client: HttpClient) {

    /**
     * Searches YTS Subs for subtitles matching [target] and returns them grouped by language.
     *
     * @param languages optional comma-separated language names (e.g. `"English,Spanish"`).
     *                  When `null`, subtitles in all available languages are returned.
     */
    suspend fun search(
        target: WatchTarget,
        languages: String? = null,
    ): List<SubtitleGroup> {
        if (target !is WatchTarget.Movie) return emptyList()
        val rawImdbId = target.imdbId ?: return emptyList()
        val imdbId = if (rawImdbId.startsWith("tt", ignoreCase = true)) rawImdbId else "tt$rawImdbId"
        val scrapeUrl = "$BASE_URL/movie-imdb/$imdbId"

        val html = try {
            client.get(scrapeUrl) {
                header("User-Agent", USER_AGENT)
            }.bodyAsText()
        } catch (_: Exception) {
            return emptyList()
        }

        val allSubtitles = parseYtsSubsMoviePage(html, BASE_URL)
        val targetLanguages = languages?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotEmpty() }

        val filtered = if (targetLanguages != null) {
            allSubtitles.filter { sub -> targetLanguages.any { it.equals(sub.language, ignoreCase = true) } }
        } else {
            allSubtitles
        }

        return filtered
            .groupBy { it.language }
            .map { (lang, subs) -> SubtitleGroup(language = lang, subtitles = subs) }
            .sortedBy { it.language }
    }

    /**
     * Resolves the direct subtitle download URL by fetching the subtitle page,
     * extracting the `btn-download-subtitle` tag's `data-link` attribute, and base64-decoding it.
     */
    suspend fun getDownloadUrl(subtitlePageUrl: String): String {
        val html = client.get(subtitlePageUrl) {
            header("User-Agent", USER_AGENT)
        }.bodyAsText()

        return parseYtsSubsDownloadUrl(html)
            ?: throw IllegalStateException("Could not extract download URL from $subtitlePageUrl")
    }

    /**
     * Downloads subtitle bytes from [url]. If [url] is a yts-subs subtitle page,
     * it resolves the direct link from `data-link` first.
     */
    suspend fun download(url: String): ByteArray {
        val directUrl = if (url.contains("yts-subs.com/subtitles/")) {
            getDownloadUrl(url)
        } else {
            url
        }

        return client.get(directUrl) {
            header("User-Agent", USER_AGENT)
        }.readRawBytes()
    }
}
