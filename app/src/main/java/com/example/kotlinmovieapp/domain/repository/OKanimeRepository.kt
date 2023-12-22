package com.example.kotlinmovieapp.domain.repository

import retrofit2.Response

interface OKanimeRepository {

    suspend fun getLatestEpisodes(
//        page: Int
    ) : Response<String>

    suspend fun getAnimeDetails(slug: String) : Response<String>
}