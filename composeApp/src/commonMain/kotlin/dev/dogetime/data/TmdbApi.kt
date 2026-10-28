package dev.dogetime.data

import dev.dogetime.Secrets
import dev.dogetime.domain.MediaType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val API_BASE_URL = "https://api.themoviedb.org/3/"

fun createHttpClient(): HttpClient = HttpClient {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

/** Thin wrapper over the TMDB v3 REST API. Returns raw DTOs; mapping happens in the repository. */
class TmdbApi(private val client: HttpClient) {

    suspend fun list(type: MediaType, path: String, page: Int): TmdbPage =
        fetch("${type.apiName}/$path") {
            parameter("page", page)
        }

    suspend fun trending(type: MediaType, page: Int): TmdbPage =
        fetch("trending/${type.apiName}/week") {
            parameter("page", page)
        }

    suspend fun details(type: MediaType, id: Int): TmdbDetails =
        fetch("${type.apiName}/$id") {
            parameter("append_to_response", "credits,recommendations,similar,external_ids,images")
            parameter("include_image_language", "en,null")
        }

    suspend fun season(tvId: Int, seasonNumber: Int): TmdbSeasonDetails =
        fetch("tv/$tvId/season/$seasonNumber") {}

    suspend fun search(query: String, page: Int): TmdbPage =
        fetch("search/multi") {
            parameter("query", query)
            parameter("page", page)
            parameter("include_adult", false)
        }

    private suspend inline fun <reified T> fetch(path: String, block: HttpRequestBuilder.() -> Unit): T =
        client.get("$API_BASE_URL$path") {
            parameter("api_key", Secrets.TMDB_API_KEY)
            block()
        }.body()

    private val MediaType.apiName: String
        get() = if (this == MediaType.Movie) "movie" else "tv"
}
