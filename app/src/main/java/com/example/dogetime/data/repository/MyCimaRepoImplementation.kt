package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.MyCimaAPI
import com.example.dogetime.domain.repository.MyCimaRepository
import retrofit2.Response
import javax.inject.Inject

class MyCimaRepoImplementation @Inject constructor(
    private val api: MyCimaAPI
) : MyCimaRepository {
    override suspend fun getLatest(): Response<String> {
        return api.getLatest()
    }

    override suspend fun getLatestMovies(): Response<String> {
        return api.getLatestMovies()
    }

    override suspend fun getLatestEpisodes(): Response<String> {
        return api.getLatestEpisodes()
    }

    override suspend fun getMovieDetails(id: String): Response<String> {
        return api.getMovieDetails(id)
    }

    override suspend fun getShowDetails(id: String): Response<String> {
        return api.getShowDetails(id)
    }

    override suspend fun getSources(url: String): Response<String> {
        return api.getSources(url)
    }

    override suspend fun getSeasons(url: String): Response<String> {
        return api.getSeasons(url)
    }

    override suspend fun search(query: String): Response<String> {
        return api.search(query)
    }


}