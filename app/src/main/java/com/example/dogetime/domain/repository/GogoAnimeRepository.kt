package com.example.dogetime.domain.repository

import retrofit2.Response

interface GogoAnimeRepository {
    suspend fun getLatestEpisodes(): Response<String>

    suspend fun getNewestAnime(page: Int? = 1): Response<String>

    suspend fun searchAnime(search: String, page: Int? = 1): Response<String>

    suspend fun getAnimeDetails(id: String ): Response<String>


    suspend fun getSources(slug : String ): Response<String>

}