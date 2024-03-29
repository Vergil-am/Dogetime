package com.example.dogetime.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AniwaveAPI {
    @GET("home")
    suspend fun getLatestEpisodes(): Response<String>

    @GET("newest")
    suspend fun getNewestAnime(
        @Query("page") page: Int? = 1
    ): Response<String>

    @GET("filter")
    suspend fun searchAnime(
        @Query("keyword") search: String,
        @Query("page") page: Int? = 1
    ): Response<String>

    @GET("watch/{id}")
    suspend fun getAnimeDetails(
        @Path("id") id: String
    ) : Response<String>

}