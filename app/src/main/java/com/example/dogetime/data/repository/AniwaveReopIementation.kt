package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.AniwaveAPI
import com.example.dogetime.domain.repository.AniwaveRepository
import retrofit2.Response
import javax.inject.Inject

class AniwaveReopIementation @Inject constructor(
    private val api: AniwaveAPI
) : AniwaveRepository {
    override suspend fun getLatestEpisodes(): Response<String> {
        return api.getLatestEpisodes()
    }

    override suspend fun getNewestAnime(page: Int?): Response<String> {
        return api.getNewestAnime()
    }

    override suspend fun searchAnime(search: String, page: Int?): Response<String> {
        return api.searchAnime(search, page)
    }

    override suspend fun getAnimeDetails(id: String): Response<String> {
        return api.getAnimeDetails(id)
    }
}