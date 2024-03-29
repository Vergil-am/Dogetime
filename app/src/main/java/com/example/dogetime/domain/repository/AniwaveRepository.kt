package com.example.dogetime.domain.repository

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AniwaveRepository {
    suspend fun getLatestEpisodes(): Response<String>

    suspend fun getNewestAnime(page: Int? = 1): Response<String>

    suspend fun searchAnime(search: String, page: Int? = 1): Response<String>

    suspend fun getAnimeDetails(id: String ): Response<String>

}