package com.example.dogetime.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeCatAPI {
    @GET("/")
    suspend fun getLatestEpisodes(): Response<String>

//    @GET("anime/{page}")
//    suspend fun getAnime(
//        @Path("page") page: Int? = null
//    ) : Response<String>

    @GET("animes-search-vostfr.json")
    suspend fun getAnime(): Response<String>

    @GET("anime/info/{slug}")
    suspend fun getDetails(
        @Path("slug") slug: String
    ): Response<String>


    @GET("anime/episode/{slug}")
    suspend fun getEpisode(
        @Path("slug") slug: String
    ): Response<String>

}