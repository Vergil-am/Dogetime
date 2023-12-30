package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.presentation.navgraph.Route
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OkanimeAPI {

    @GET(
//        "espisode-list"
        "episode"
    )
    suspend fun getLatestEpisodes(
//        @Query("page") page: Int = 1
    ) : Response<String>


    @GET("anime/{slug}")
    suspend fun getAnimeDetails(
        @Path("slug") slug: String
    ) : Response<String>

    @GET("episode/{slug}")
    suspend fun getEpisode(
        @Path(value = "slug", encoded = true) slug: String
    ) : Response<String>

    @GET("anime-list/")
    suspend fun getAnime(
        @Query("page") page: Int
    ) : Response<String>

    @GET("search/")
    suspend fun searchAnime(
        @Query("s") query: String
    ) : Response<String>
}