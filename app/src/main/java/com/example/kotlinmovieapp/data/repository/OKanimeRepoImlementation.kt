package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.OkanimeAPI
import com.example.kotlinmovieapp.domain.repository.OKanimeRepository
import retrofit2.Response
import javax.inject.Inject

class OKanimeRepoImlementation @Inject constructor(
    private val okanime: OkanimeAPI
): OKanimeRepository {

    override suspend fun getLatestEpisodes(
//        page: Int
    ): Response<String> {
        return okanime.getLatestEpisodes(
//            page
        )
    }

    override suspend fun getAnimeDetails(slug: String): Response<String> {
        return okanime.getAnimeDetails(slug)
    }

}