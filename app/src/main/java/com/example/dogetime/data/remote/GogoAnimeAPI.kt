package com.example.dogetime.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GogoAnimeAPI {
    @GET("/")
    suspend fun getLatestEpisodes(): Response<String>

    @GET("popular.html")
    suspend fun getPopularAnime(
        @Query("page") page: Int? = 1
    ): Response<String>

    @GET("search.html")
    suspend fun searchAnime(
        @Query("keyword") search: String,
        @Query("page") page: Int? = 1
    ): Response<String>

    @GET("category/{id}")
    suspend fun getAnimeDetails(
        @Path("id") id: String
    ) : Response<String>

    @GET("{slug}")
    suspend fun getSources(
        @Path("slug") slug: String
    ) : Response<String>


}