package dev.dogetime.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

private const val KITSU_BASE_URL = "https://kitsu.app/api/edge"

class KitsuApi(private val client: HttpClient) {

    suspend fun trending(limit: Int = 20): KitsuListResponse =
        client.get("$KITSU_BASE_URL/trending/anime") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
            parameter("limit", limit)
        }.body()

    suspend fun popular(limit: Int = 20, offset: Int = 0): KitsuListResponse =
        client.get("$KITSU_BASE_URL/anime") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
            parameter("sort", "-userCount")
            parameter("page[limit]", limit)
            parameter("page[offset]", offset)
        }.body()

    suspend fun topRated(limit: Int = 20, offset: Int = 0): KitsuListResponse =
        client.get("$KITSU_BASE_URL/anime") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
            parameter("sort", "-averageRating")
            parameter("page[limit]", limit)
            parameter("page[offset]", offset)
        }.body()

    suspend fun currentlyAiring(limit: Int = 20, offset: Int = 0): KitsuListResponse =
        client.get("$KITSU_BASE_URL/anime") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
            parameter("filter[status]", "current")
            parameter("sort", "-userCount")
            parameter("page[limit]", limit)
            parameter("page[offset]", offset)
        }.body()

    suspend fun upcoming(limit: Int = 20, offset: Int = 0): KitsuListResponse =
        client.get("$KITSU_BASE_URL/anime") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
            parameter("filter[status]", "upcoming")
            parameter("sort", "-userCount")
            parameter("page[limit]", limit)
            parameter("page[offset]", offset)
        }.body()

    suspend fun search(query: String, limit: Int = 20, offset: Int = 0): KitsuListResponse =
        client.get("$KITSU_BASE_URL/anime") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
            parameter("filter[text]", query)
            parameter("page[limit]", limit)
            parameter("page[offset]", offset)
        }.body()

    suspend fun details(kitsuId: Int): KitsuSingleResponse =
        client.get("$KITSU_BASE_URL/anime/$kitsuId") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
        }.body()

    suspend fun episodes(kitsuId: Int, limit: Int = 20, offset: Int = 0): KitsuEpisodeListResponse =
        client.get("$KITSU_BASE_URL/anime/$kitsuId/episodes") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
            parameter("page[limit]", limit.coerceAtMost(20))
            parameter("page[offset]", offset)
            parameter("sort", "number")
        }.body()

    suspend fun genres(kitsuId: Int): KitsuGenreListResponse =
        client.get("$KITSU_BASE_URL/anime/$kitsuId/genres") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
        }.body()

    suspend fun categories(kitsuId: Int): KitsuCategoryListResponse =
        client.get("$KITSU_BASE_URL/anime/$kitsuId/categories") {
            header("User-Agent", "Mozilla/5.0")
            header("Accept", "application/vnd.api+json")
        }.body()
}

@Serializable
data class KitsuListResponse(
    val data: List<KitsuAnimeResource> = emptyList(),
    val meta: KitsuMeta? = null,
)

@Serializable
data class KitsuSingleResponse(
    val data: KitsuAnimeResource,
)

@Serializable
data class KitsuMeta(
    val count: Int? = null,
)

@Serializable
data class KitsuAnimeResource(
    val id: String,
    val type: String = "anime",
    val attributes: KitsuAnimeAttributes,
)

@Serializable
data class KitsuAnimeAttributes(
    val slug: String? = null,
    val synopsis: String? = null,
    val description: String? = null,
    val canonicalTitle: String? = null,
    val titles: Map<String, String?> = emptyMap(),
    val averageRating: String? = null,
    val startDate: String? = null,
    val status: String? = null,
    val posterImage: KitsuImage? = null,
    val coverImage: KitsuImage? = null,
    val episodeCount: Int? = null,
    val episodeLength: Int? = null,
    val subtype: String? = null,
    val ageRatingGuide: String? = null,
    val userCount: Int? = null,
)

@Serializable
data class KitsuImage(
    val tiny: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val original: String? = null,
)

@Serializable
data class KitsuEpisodeListResponse(
    val data: List<KitsuEpisodeResource> = emptyList(),
)

@Serializable
data class KitsuEpisodeResource(
    val id: String,
    val attributes: KitsuEpisodeAttributes,
)

@Serializable
data class KitsuEpisodeAttributes(
    val canonicalTitle: String? = null,
    val synopsis: String? = null,
    val description: String? = null,
    val seasonNumber: Int? = null,
    val number: Int? = null,
    val airdate: String? = null,
    val length: Int? = null,
    val thumbnail: KitsuImage? = null,
)

@Serializable
data class KitsuGenreListResponse(
    val data: List<KitsuGenreResource> = emptyList(),
)

@Serializable
data class KitsuGenreResource(
    val id: String,
    val attributes: KitsuGenreAttributes,
)

@Serializable
data class KitsuGenreAttributes(
    val name: String,
)

@Serializable
data class KitsuCategoryListResponse(
    val data: List<KitsuCategoryResource> = emptyList(),
)

@Serializable
data class KitsuCategoryResource(
    val id: String,
    val attributes: KitsuCategoryAttributes,
)

@Serializable
data class KitsuCategoryAttributes(
    val title: String,
)
