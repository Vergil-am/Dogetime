package dev.dogetime.data

import dev.dogetime.domain.Subtitle
import dev.dogetime.domain.SubtitleGroup
import dev.dogetime.domain.WatchTarget
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.readRawBytes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val OPEN_SUBTITLES_V3_BASE = "https://opensubtitles-v3.strem.io/subtitles"
private const val ANIME_KITSU_BASE = "https://anime-kitsu.strem.fun/subtitles/anime"
private const val USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

/**
 * Fetches subtitles from Stremio OpenSubtitles v3 and Anime Kitsu subtitle add-ons.
 */
class StremioSubtitlesApi(
    private val client: HttpClient,
    private val kitsuMapping: KitsuMappingRepository? = null,
) {

    /**
     * Searches for subtitles matching [target] and returns them grouped by language.
     * Uses Anime Kitsu for anime titles with a Kitsu id, and OpenSubtitles v3 for movies/series with an IMDb id.
     * Falls back to resolving the Kitsu id to IMDb coordinates via [kitsuMapping] when Anime Kitsu is offline.
     */
    suspend fun search(target: WatchTarget): List<SubtitleGroup> {
        val kitsuId = target.kitsuId
        val rawImdbId = target.imdbId
        val imdbId = rawImdbId?.let { if (it.startsWith("tt", ignoreCase = true)) it else "tt$it" }

        val primaryUrl = when {
            kitsuId != null -> {
                val formattedKitsuId = if ("$kitsuId".startsWith("kitsu:")) "$kitsuId" else "kitsu:$kitsuId"
                when (target) {
                    is WatchTarget.Movie -> "$ANIME_KITSU_BASE/$formattedKitsuId.json"
                    is WatchTarget.Episode -> "$ANIME_KITSU_BASE/$formattedKitsuId:${target.episode}.json"
                }
            }
            imdbId != null -> {
                when (target) {
                    is WatchTarget.Movie -> "$OPEN_SUBTITLES_V3_BASE/movie/$imdbId.json"
                    is WatchTarget.Episode -> "$OPEN_SUBTITLES_V3_BASE/series/$imdbId:${target.season}:${target.episode}.json"
                }
            }
            else -> return emptyList()
        }

        var subtitles = fetchSubtitlesFromUrl(primaryUrl)

        // Offline / empty fallback for anime: resolve via cached KitsuMappingRepository
        if (subtitles.isEmpty() && kitsuId != null) {
            val kitsuEp = (target as? WatchTarget.Episode)?.episode
            val resolved = kitsuMapping?.resolve(kitsuId, kitsuEp)
            if (resolved != null) {
                val fallbackUrl = when (target) {
                    is WatchTarget.Movie -> "$OPEN_SUBTITLES_V3_BASE/movie/${resolved.imdbId}.json"
                    is WatchTarget.Episode -> {
                        val s = resolved.season ?: target.season
                        val ep = resolved.episode ?: target.episode
                        "$OPEN_SUBTITLES_V3_BASE/series/${resolved.imdbId}:$s:$ep.json"
                    }
                }
                subtitles = fetchSubtitlesFromUrl(fallbackUrl)
            } else if (imdbId != null) {
                val fallbackUrl = when (target) {
                    is WatchTarget.Movie -> "$OPEN_SUBTITLES_V3_BASE/movie/$imdbId.json"
                    is WatchTarget.Episode -> "$OPEN_SUBTITLES_V3_BASE/series/$imdbId:${target.season}:${target.episode}.json"
                }
                subtitles = fetchSubtitlesFromUrl(fallbackUrl)
            }
        }

        return subtitles
            .filter { it.url.isNotBlank() }
            .map { it.toDomain() }
            .groupBy { it.language }
            .map { (lang, subs) -> SubtitleGroup(language = lang, subtitles = subs) }
            .sortedBy { it.language }
    }

    private suspend fun fetchSubtitlesFromUrl(url: String): List<StremioSubtitleItem> {
        return try {
            val response: StremioSubtitlesResponse = client.get(url) {
                header("User-Agent", USER_AGENT)
            }.body()
            response.subtitles
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Downloads subtitle bytes from [url].
     */
    suspend fun download(url: String): ByteArray {
        return client.get(url) {
            header("User-Agent", USER_AGENT)
        }.readRawBytes()
    }
}

@Serializable
private data class StremioSubtitlesResponse(
    val subtitles: List<StremioSubtitleItem> = emptyList(),
)

@Serializable
private data class StremioSubtitleItem(
    val id: String = "",
    val url: String = "",
    val lang: String = "",
    @SerialName("SubEncoding") val subEncoding: String? = null,
    val subtitleFileName: String? = null,
    val movieReleaseName: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
) {
    fun toDomain(): Subtitle {
        val releaseName = subtitleFileName?.takeIf { it.isNotBlank() }
            ?: movieReleaseName?.takeIf { it.isNotBlank() }
            ?: "Subtitle ($id)"
        val languageName = resolveLanguageName(lang)
        return Subtitle(
            releaseName = releaseName,
            language = languageName,
            author = "OpenSubtitles",
            downloadUrl = url,
            hearingImpaired = false,
            season = season,
            episode = episode,
        )
    }
}

private fun resolveLanguageName(code: String): String {
    val clean = code.trim().lowercase()
    return STREMIO_LANGUAGE_MAP[clean]
        ?: STREMIO_LANGUAGE_MAP[clean.take(2)]
        ?: clean.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private val STREMIO_LANGUAGE_MAP = mapOf(
    "eng" to "English",
    "en" to "English",
    "spa" to "Spanish",
    "es" to "Spanish",
    "fre" to "French",
    "fra" to "French",
    "fr" to "French",
    "ger" to "German",
    "deu" to "German",
    "de" to "German",
    "ita" to "Italian",
    "it" to "Italian",
    "por" to "Portuguese",
    "pob" to "Portuguese (BR)",
    "pt" to "Portuguese",
    "pt-br" to "Portuguese (BR)",
    "pt-pt" to "Portuguese (PT)",
    "ara" to "Arabic",
    "ar" to "Arabic",
    "rus" to "Russian",
    "ru" to "Russian",
    "tur" to "Turkish",
    "tr" to "Turkish",
    "ell" to "Greek",
    "gre" to "Greek",
    "el" to "Greek",
    "nld" to "Dutch",
    "dut" to "Dutch",
    "nl" to "Dutch",
    "pol" to "Polish",
    "pl" to "Polish",
    "swe" to "Swedish",
    "sv" to "Swedish",
    "dan" to "Danish",
    "da" to "Danish",
    "fin" to "Finnish",
    "fi" to "Finnish",
    "nor" to "Norwegian",
    "no" to "Norwegian",
    "cze" to "Czech",
    "ces" to "Czech",
    "cs" to "Czech",
    "hun" to "Hungarian",
    "hu" to "Hungarian",
    "ron" to "Romanian",
    "rum" to "Romanian",
    "ro" to "Romanian",
    "bul" to "Bulgarian",
    "bg" to "Bulgarian",
    "ukr" to "Ukrainian",
    "uk" to "Ukrainian",
    "heb" to "Hebrew",
    "he" to "Hebrew",
    "per" to "Persian",
    "fas" to "Persian",
    "fa" to "Persian",
    "hin" to "Hindi",
    "hi" to "Hindi",
    "ben" to "Bengali",
    "bn" to "Bengali",
    "ind" to "Indonesian",
    "id" to "Indonesian",
    "may" to "Malay",
    "msa" to "Malay",
    "ms" to "Malay",
    "tha" to "Thai",
    "th" to "Thai",
    "vie" to "Vietnamese",
    "vi" to "Vietnamese",
    "kor" to "Korean",
    "ko" to "Korean",
    "jpn" to "Japanese",
    "ja" to "Japanese",
    "chi" to "Chinese",
    "zho" to "Chinese",
    "zh" to "Chinese",
    "zh-cn" to "Chinese (Simplified)",
    "zh-tw" to "Chinese (Traditional)",
    "srp" to "Serbian",
    "sr" to "Serbian",
    "hrv" to "Croatian",
    "hr" to "Croatian",
    "slv" to "Slovenian",
    "sl" to "Slovenian",
    "slk" to "Slovak",
    "slo" to "Slovak",
    "sk" to "Slovak",
    "lit" to "Lithuanian",
    "lt" to "Lithuanian",
    "lav" to "Latvian",
    "lv" to "Latvian",
    "est" to "Estonian",
    "et" to "Estonian",
    "alb" to "Albanian",
    "sqi" to "Albanian",
    "sq" to "Albanian",
    "bos" to "Bosnian",
    "bs" to "Bosnian",
    "mkd" to "Macedonian",
    "mac" to "Macedonian",
    "mk" to "Macedonian",
    "isl" to "Icelandic",
    "ice" to "Icelandic",
    "is" to "Icelandic",
    "gle" to "Irish",
    "ga" to "Irish",
    "baq" to "Basque",
    "eus" to "Basque",
    "eu" to "Basque",
    "cat" to "Catalan",
    "ca" to "Catalan",
    "glg" to "Galician",
    "gl" to "Galician",
    "urd" to "Urdu",
    "ur" to "Urdu",
    "tam" to "Tamil",
    "ta" to "Tamil",
    "tel" to "Telugu",
    "te" to "Telugu",
    "mal" to "Malayalam",
    "ml" to "Malayalam",
    "fil" to "Filipino",
    "tgl" to "Tagalog",
    "tl" to "Tagalog",
)
