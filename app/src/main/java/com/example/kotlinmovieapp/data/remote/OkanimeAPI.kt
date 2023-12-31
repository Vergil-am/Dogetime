package com.example.kotlinmovieapp.data.remote

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

    @GET("anime-list-3/page/{page}")
    suspend fun getAnime(
//        @Query("page") page: Int
        @Path("page") page: Int
    ) : Response<String>

    @GET("/")
    suspend fun searchAnime(
        @Query("search_param") param: String = "animes",
        @Query("s") query: String
    ) : Response<String>
}