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
import io.ktor.client.statement.readRawBytes

private const val API_BASE_URL = "https://api.subdl.com/api/v1/subtitles"

/** Download URLs on SubDL can be fetched directly without an API key using the direct subtitle path. */
private const val DOWNLOAD_BASE_URL = "https://api.subdl.com"

/** Thin wrapper over the SubDL REST API. Returns domain models grouped by language. */
class SubdlApi(
    private val client: HttpClient,
    private val kitsuMapping: KitsuMappingRepository? = null,
) {

    /**
     * Searches SubDL for subtitles matching [target] and returns them grouped by language.
     * Automatically resolves Kitsu anime ids to IMDb ids and season/episode numbers via [kitsuMapping].
     *
     * @param languages optional comma-separated ISO codes (e.g. `"EN,FR,AR"`).
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

        val response: SubdlSearchResponse = client.get(API_BASE_URL) {
            parameter("api_key", Secrets.SUBDL_API_KEY)

            // Identify the title – prefer IMDb id, fall back to TMDB id.
            if (effectiveImdbId != null) {
                parameter("imdb_id", effectiveImdbId)
            } else {
                val tmdbId = when (target) {
                    is WatchTarget.Movie -> target.id
                    is WatchTarget.Episode -> target.showId
                }
                parameter("tmdb_id", tmdbId)
            }

            parameter("type", if (isMovie) "movie" else "tv")

            if (!isMovie) {
                effectiveSeason?.let { parameter("season_number", it) }
                effectiveEpisode?.let { parameter("episode_number", it) }
            }

            languages?.let { parameter("languages", it) }
            parameter("subs_per_page", 30)
        }.body()

        return response.subtitles
            .map { it.toDomain() }
            .groupBy { it.language }
            .map { (language, subs) -> SubtitleGroup(language = language, subtitles = subs) }
            .sortedBy { it.language }
    }

    /**
     * Downloads a subtitle file by its [url] path (as returned in [Subtitle.downloadUrl]).
     *
     * The download request intentionally does NOT send any API key or authorization header.
     */
    suspend fun download(url: String): ByteArray {
        val cleanPath = url.substringBefore('?').let { if (it.startsWith("/")) it else "/$it" }
        return client.get("$DOWNLOAD_BASE_URL$cleanPath").readRawBytes()
    }
}

// ── DTO → Domain mapping ──────────────────────────────────────────────

private fun SubdlSubtitle.toDomain(): Subtitle = Subtitle(
    releaseName = releaseName.ifBlank { name },
    language = lang.ifBlank { language },
    author = author,
    downloadUrl = url.substringBefore('?'),
    hearingImpaired = hearingImpaired,
    season = season,
    episode = episode,
)
