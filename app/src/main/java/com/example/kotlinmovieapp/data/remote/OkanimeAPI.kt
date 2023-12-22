package com.example.kotlinmovieapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OkanimeAPI {

    @GET("espisode-list")
    suspend fun getLatestEpisodes(
//        @Query("page") page: Int = 1
    ) : Response<String>


    @GET("anime/{slug}")
    suspend fun getAnimeDetails(
        @Path("slug") slug: String
    ) : Response<String>
}