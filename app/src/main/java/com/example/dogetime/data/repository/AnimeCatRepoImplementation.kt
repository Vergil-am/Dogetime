package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.AnimeCatAPI
import com.example.dogetime.domain.repository.AnimeCatRepository
import retrofit2.Response
import javax.inject.Inject

class AnimeCatRepoImplementation @Inject constructor(
    private val api: AnimeCatAPI
) : AnimeCatRepository {
    override suspend fun getLatestEpisodes(): Response<String> {
        return api.getLatestEpisodes()

    }

    override suspend fun getAnime(page: Int?): Response<String> {
        return api.getAnime(page)
    }

    override suspend fun getDetails(slug: String): Response<String> {
        return api.getDetails(slug)
    }

    override suspend fun getEpisode(slug: String): Response<String> {
        return api.getEpisode(slug)
    }
}