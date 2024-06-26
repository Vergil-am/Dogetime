package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.GogoAnimeAPI
import com.example.dogetime.domain.repository.GogoAnimeRepository
import retrofit2.Response
import javax.inject.Inject

class GogoAnimeRepoImplementation @Inject constructor(
    private val api: GogoAnimeAPI
) : GogoAnimeRepository {
    override suspend fun getLatestEpisodes(): Response<String> {
        return api.getLatestEpisodes()
    }

    override suspend fun getPopular(page: Int?): Response<String> {
        return api.getPopularAnime(page)
    }

    override suspend fun searchAnime(search: String, page: Int?): Response<String> {
        return api.searchAnime(search, page)
    }

    override suspend fun getAnimeDetails(id: String): Response<String> {
        return api.getAnimeDetails(id)
    }

    override suspend fun getEpisodes(url: String): Response<String> {
        return api.getEpisodes(url)
    }

    override suspend fun getSources(slug: String): Response<String> {
        return api.getSources(slug)
    }
}