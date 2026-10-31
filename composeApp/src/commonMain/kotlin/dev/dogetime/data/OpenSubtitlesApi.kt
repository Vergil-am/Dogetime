package dev.dogetime.data

import dev.dogetime.Secrets
import dev.dogetime.domain.Subtitle
import dev.dogetime.domain.SubtitleGroup
import dev.dogetime.domain.WatchTarget
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val API_BASE_URL = "https://api.opensubtitles.com/api/v1"
private const val SEARCH_URL = "$API_BASE_URL/subtitles"
private const val DOWNLOAD_URL = "$API_BASE_URL/download"
private const val USER_AGENT = "dogetime v1.0.0"

/** Thin wrapper over the OpenSubtitles.com REST API. Returns domain models grouped by language. */
class OpenSubtitlesApi(
    private val client: HttpClient,
    private val kitsuMapping: KitsuMappingRepository? = null,
) {

    /**
     * Searches OpenSubtitles for subtitles matching [target] and returns them grouped by language.
     * Automatically resolves Kitsu anime ids to IMDb ids and season/episode numbers via [kitsuMapping].
     *
     * @param languages optional comma-separated ISO 639-1 codes (e.g. `"en,fr,ar"`).
     *                  When `null`, subtitles in every available language are returned.
     */
    suspend fun search(
        target: WatchTarget,
        languages: String? = null,
    ): List<SubtitleGroup> {
        val resolved = if (target.imdbId == null && target.kitsuId != null && kitsuMapping != null) {
            val kitsuEp = (target as? WatchTarget.Episode)?.episode
            kitsuMapping.resolve(target.kitsuId!!, kitsuEp)
        } else null

        val effectiveImdbId = resolved?.imdbId ?: target.imdbId
        val effectiveSeason = resolved?.season ?: (target as? WatchTarget.Episode)?.season
        val effectiveEpisode = resolved?.episode ?: (target as? WatchTarget.Episode)?.episode
        val isMovie = target is WatchTarget.Movie && effectiveEpisode == null

        val response: OpenSubtitlesSearchResponse = client.get(SEARCH_URL) {
            header("Api-Key", Secrets.OPEN_SUBTITLES_API_KEY)
            header("User-Agent", USER_AGENT)

            // CRITICAL: Parameters MUST be in strict alphabetical order to satisfy
            // OpenSubtitles Cloudflare canonical url rule (avoids 301 redirect which drops auth headers).
            // Alphabetical order:
            // 1. episode_number
            // 2. imdb_id
            // 3. languages
            // 4. season_number
            // 5. tmdb_id
            // 6. type

            if (!isMovie && effectiveEpisode != null) {
                parameter("episode_number", effectiveEpisode)
            }

            if (effectiveImdbId != null) {
                // OpenSubtitles requires leading zeros removed (e.g. "0111161" -> "111161")
                val cleanImdbNumber = effectiveImdbId.removePrefix("tt").trimStart('0').ifEmpty { "0" }
                parameter("imdb_id", cleanImdbNumber)
            }

            languages?.let { parameter("languages", it) }

            if (!isMovie && effectiveSeason != null) {
                parameter("season_number", effectiveSeason)
            }

            if (effectiveImdbId == null) {
                val tmdbId = when (target) {
                    is WatchTarget.Movie -> target.id
                    is WatchTarget.Episode -> target.showId
                }
                parameter("tmdb_id", tmdbId)
            }

            parameter("type", if (isMovie) "movie" else "episode")
        }.body()

        return response.data
            .flatMap { it.toDomainList() }
            .groupBy { it.language }
            .map { (language, subs) -> SubtitleGroup(language = language, subtitles = subs) }
            .sortedBy { it.language }
    }

    /**
     * Downloads a subtitle file by its file ID or download link.
     *
     * Unlike SubDL, OpenSubtitles requires the API key on download requests:
     * 1. A POST to `/download` with the `file_id` and the `Api-Key` header to request the download link.
     * 2. A GET to the returned link with the `Api-Key` and `User-Agent` headers to retrieve the file bytes.
     */
    suspend fun download(url: String): ByteArray {
        val fileId = url.toLongOrNull()
        val directUrl = if (fileId != null) {
            requestDownloadLink(fileId)
        } else {
            url
        }
        return client.get(directUrl) {
            header("Api-Key", Secrets.OPEN_SUBTITLES_API_KEY)
            header("User-Agent", USER_AGENT)
        }.readRawBytes()
    }

    /** Downloads a subtitle file given its OpenSubtitles [fileId]. */
    suspend fun download(fileId: Long): ByteArray {
        val directUrl = requestDownloadLink(fileId)
        return client.get(directUrl) {
            header("Api-Key", Secrets.OPEN_SUBTITLES_API_KEY)
            header("User-Agent", USER_AGENT)
        }.readRawBytes()
    }

    private suspend fun requestDownloadLink(fileId: Long): String {
        val response: OpenSubtitlesDownloadResponse = client.post(DOWNLOAD_URL) {
            header("Api-Key", Secrets.OPEN_SUBTITLES_API_KEY)
            header("User-Agent", USER_AGENT)
            contentType(ContentType.Application.Json)
            setBody(OpenSubtitlesDownloadRequest(fileId = fileId))
        }.body()
        return response.link
    }
}

// ── DTO → Domain mapping ──────────────────────────────────────────────

private fun OpenSubtitlesData.toDomainList(): List<Subtitle> {
    if (attributes.files.isEmpty()) return emptyList()
    val langCode = attributes.language
    val langName = LANGUAGE_MAP[langCode.lowercase()] ?: langCode.uppercase()
    val baseRelease = attributes.release.ifBlank { attributes.featureDetails?.title.orEmpty() }

    return attributes.files.map { file ->
        val fileRelease = if (attributes.files.size > 1 && file.cdNumber != null) {
            "${baseRelease.ifBlank { file.fileName }} (CD ${file.cdNumber})"
        } else {
            baseRelease.ifBlank { file.fileName.substringBeforeLast('.').ifBlank { "Subtitle ${file.fileId}" } }
        }

        Subtitle(
            releaseName = fileRelease,
            language = langName,
            author = attributes.uploader?.name.orEmpty(),
            downloadUrl = file.fileId.toString(),
            hearingImpaired = attributes.hearingImpaired,
            season = attributes.featureDetails?.seasonNumber,
            episode = attributes.featureDetails?.episodeNumber,
        )
    }
}

private val LANGUAGE_MAP = mapOf(
    "en" to "English",
    "fr" to "French",
    "es" to "Spanish",
    "de" to "German",
    "it" to "Italian",
    "pt" to "Portuguese",
    "pt-br" to "Portuguese (BR)",
    "pt-pt" to "Portuguese (PT)",
    "ar" to "Arabic",
    "ru" to "Russian",
    "tr" to "Turkish",
    "el" to "Greek",
    "nl" to "Dutch",
    "pl" to "Polish",
    "sv" to "Swedish",
    "da" to "Danish",
    "fi" to "Finnish",
    "no" to "Norwegian",
    "cs" to "Czech",
    "hu" to "Hungarian",
    "ro" to "Romanian",
    "bg" to "Bulgarian",
    "uk" to "Ukrainian",
    "he" to "Hebrew",
    "fa" to "Persian",
    "hi" to "Hindi",
    "bn" to "Bengali",
    "id" to "Indonesian",
    "ms" to "Malay",
    "th" to "Thai",
    "vi" to "Vietnamese",
    "ko" to "Korean",
    "ja" to "Japanese",
    "zh" to "Chinese",
    "zh-cn" to "Chinese (Simplified)",
    "zh-tw" to "Chinese (Traditional)",
    "sr" to "Serbian",
    "hr" to "Croatian",
    "sl" to "Slovenian",
    "sk" to "Slovak",
    "lt" to "Lithuanian",
    "lv" to "Latvian",
    "et" to "Estonian",
    "sq" to "Albanian",
    "bs" to "Bosnian",
    "mk" to "Macedonian",
    "is" to "Icelandic",
    "ga" to "Irish",
    "eu" to "Basque",
    "ca" to "Catalan",
    "gl" to "Galician",
    "ur" to "Urdu",
    "ta" to "Tamil",
    "te" to "Telugu",
    "ml" to "Malayalam",
    "kn" to "Kannada",
    "mr" to "Marathi",
    "gu" to "Gujarati",
    "pa" to "Punjabi",
    "ne" to "Nepali",
    "si" to "Sinhala",
    "my" to "Burmese",
    "km" to "Khmer",
    "tl" to "Tagalog",
    "sw" to "Swahili",
    "af" to "Afrikaans",
    "ka" to "Georgian",
    "hy" to "Armenian",
    "az" to "Azerbaijani",
    "kk" to "Kazakh",
    "uz" to "Uzbek",
    "mn" to "Mongolian",
)
