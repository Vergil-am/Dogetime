package dev.dogetime.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val MAPPING_URL = "https://raw.githubusercontent.com/TheBeastLT/stremio-kitsu-anime/master/static/data/imdb_mapping.json"
private const val ANIME_KITSU_META_BASE = "https://anime-kitsu.strem.fun/meta"

private val jsonParser = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

/**
 * Resolved IMDb coordinates for an anime item.
 */
data class ResolvedImdbEpisode(
    val imdbId: String,
    val season: Int? = null,
    val episode: Int? = null,
)

/**
 * Raw mapping item from TheBeastLT/stremio-kitsu-anime imdb_mapping.json.
 */
@Serializable
data class KitsuMappingEntry(
    @SerialName("kitsu_id") val kitsuId: Int,
    @SerialName("imdb_id") val imdbId: String? = null,
    val title: String? = null,
    val fromSeason: Int? = null,
    val fromEpisode: Int? = null,
    val imdbEpisodes: List<Int?>? = null,
)

@Serializable
private data class AnimeKitsuMetaResponse(
    val meta: AnimeKitsuMeta? = null,
)

@Serializable
private data class AnimeKitsuMeta(
    val id: String? = null,
    @SerialName("imdb_id") val imdbId: String? = null,
    val type: String? = null,
    val videos: List<AnimeKitsuVideo> = emptyList(),
)

@Serializable
private data class AnimeKitsuVideo(
    val id: String? = null,
    val episode: Int? = null,
    val season: Int? = null,
    @SerialName("imdb_id") val imdbId: String? = null,
    val imdbSeason: Int? = null,
    val imdbEpisode: Int? = null,
)

/**
 * Caches and resolves Kitsu IDs to IMDb IDs and Western season/episode numbers.
 */
class KitsuMappingRepository(
    private val client: HttpClient,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val mutex = Mutex()
    private var mappingsDeferred: Deferred<Map<Int, KitsuMappingEntry>>? = null

    /**
     * Eagerly initiates fetching and caching the mapping JSON in the background.
     */
    fun warmUp() {
        if (mappingsDeferred == null) {
            scope.async {
                ensureInitialized()
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private suspend fun ensureInitialized(): Deferred<Map<Int, KitsuMappingEntry>> {
        mappingsDeferred?.let { deferred ->
            if (!deferred.isCompleted || deferred.getCompleted().isNotEmpty()) {
                return deferred
            }
        }
        return mutex.withLock {
            val current = mappingsDeferred
            if (current != null && (!current.isCompleted || current.getCompleted().isNotEmpty())) {
                current
            } else {
                scope.async {
                    fetchMappings()
                }.also { mappingsDeferred = it }
            }
        }
    }

    private suspend fun fetchMappings(): Map<Int, KitsuMappingEntry> {
        return try {
            val rawJson = client.get(MAPPING_URL) {
                header("User-Agent", "Mozilla/5.0")
            }.bodyAsText()
            val list: List<KitsuMappingEntry> = jsonParser.decodeFromString(rawJson)
            println("Successfully cached ${list.size} Kitsu-to-IMDb mappings")
            list.associateBy { it.kitsuId }
        } catch (e: Exception) {
            println("Failed to fetch Kitsu-to-IMDb mappings: ${e.message}")
            emptyMap()
        }
    }

    /**
     * Looks up the raw mapping entry for [kitsuId].
     */
    suspend fun getMapping(kitsuId: Int): KitsuMappingEntry? {
        val deferred = ensureInitialized()
        val map = deferred.await()
        return map[kitsuId]
    }

    /**
     * Resolves [kitsuId] and optional local [kitsuEpisode] into an IMDb id with its corresponding
     * Western season and episode number.
     *
     * If the title is a movie (or no episode was requested and the mapping is not tied to a specific
     * episode), [ResolvedImdbEpisode.season] and [ResolvedImdbEpisode.episode] are `null`.
     */
    suspend fun resolve(kitsuId: Int, kitsuEpisode: Int? = null): ResolvedImdbEpisode? {
        // 1. Try cached static mapping
        val entry = getMapping(kitsuId)
        if (entry != null && !entry.imdbId.isNullOrBlank()) {
            val cleanImdbId = if (entry.imdbId.startsWith("tt", ignoreCase = true)) entry.imdbId else "tt${entry.imdbId}"
            val isEpisodeRequested = kitsuEpisode != null
            val hasExplicitSeasonOrEpisode = entry.fromSeason != null || entry.fromEpisode != null

            val (season, episode) = if (isEpisodeRequested || hasExplicitSeasonOrEpisode) {
                val s = entry.fromSeason ?: 1
                val ep = if (kitsuEpisode != null) {
                    val specialEpisodes = entry.imdbEpisodes
                    if (specialEpisodes != null && (kitsuEpisode - 1) in specialEpisodes.indices) {
                        specialEpisodes[kitsuEpisode - 1] ?: ((entry.fromEpisode ?: 1) + (kitsuEpisode - 1).coerceAtLeast(0))
                    } else {
                        (entry.fromEpisode ?: 1) + (kitsuEpisode - 1).coerceAtLeast(0)
                    }
                } else {
                    entry.fromEpisode ?: 1
                }
                Pair(s, ep)
            } else {
                Pair(null, null)
            }

            return ResolvedImdbEpisode(
                imdbId = cleanImdbId,
                season = season,
                episode = episode,
            )
        }

        // 2. Dynamic fallback to Anime-Kitsu meta endpoint
        return tryResolveFromAnimeKitsu(kitsuId, kitsuEpisode)
    }

    private suspend fun tryResolveFromAnimeKitsu(kitsuId: Int, kitsuEpisode: Int?): ResolvedImdbEpisode? {
        return try {
            val url = if (kitsuEpisode != null) {
                "$ANIME_KITSU_META_BASE/series/kitsu:$kitsuId.json"
            } else {
                "$ANIME_KITSU_META_BASE/movie/kitsu:$kitsuId.json"
            }
            val text = client.get(url) {
                header("User-Agent", "Mozilla/5.0")
            }.bodyAsText()
            val response: AnimeKitsuMetaResponse = jsonParser.decodeFromString(text)
            val meta = response.meta ?: return null
            val rawImdb = meta.imdbId ?: return null
            val imdbId = if (rawImdb.startsWith("tt", ignoreCase = true)) rawImdb else "tt$rawImdb"

            if (kitsuEpisode != null) {
                val video = meta.videos.firstOrNull { it.episode == kitsuEpisode }
                val season = video?.imdbSeason ?: video?.season ?: 1
                val ep = video?.imdbEpisode ?: video?.episode ?: kitsuEpisode
                ResolvedImdbEpisode(imdbId = imdbId, season = season, episode = ep)
            } else {
                ResolvedImdbEpisode(imdbId = imdbId, season = null, episode = null)
            }
        } catch (_: Exception) {
            null
        }
    }
}
