package com.example.dogetime.data.remote

import com.example.dogetime.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url

interface MyCimaAPI {


    @GET("/")
    suspend fun getLatest(): Response<String>


    @GET("/seriestv")
    suspend fun getLatestEpisodes(): Response<String>

    @GET("/movies")
    suspend fun getLatestMovies(): Response<String>

    @GET("watch/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: String,
        @Header("Referer") referer: String = Constants.WE_CIMA_URL
    ): Response<String>

    @GET("series/{id}")
    suspend fun getShowDetails(
        @Path("id") id: String
    ): Response<String>

    @GET
    suspend fun getSources(
        @Url url: String
    ): Response<String>

    @GET
    suspend fun getSeasons(
        @Url url: String
    ): Response<String>


    @GET("AjaxCenter/Searching/{query}")
    suspend fun search(
        @Path("query") query: String
    ): Response<String>
}