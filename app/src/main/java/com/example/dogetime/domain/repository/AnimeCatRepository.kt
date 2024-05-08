package com.example.dogetime.domain.repository

import retrofit2.Response

interface AnimeCatRepository {

    suspend fun getLatestEpisodes(): Response<String>

//    suspend fun getAnime(
//        page: Int?
//    ): Response<String>
    suspend fun getAnime(): Response<String>

    suspend fun getDetails(
        slug: String
    ): Response<String>


    suspend fun getEpisode(
        slug: String
    ): Response<String>


}