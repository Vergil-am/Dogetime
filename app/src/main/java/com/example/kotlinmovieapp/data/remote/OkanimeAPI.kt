package com.example.kotlinmovieapp.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface OkanimeAPI {

    @GET("/")
    suspend fun getHomePage() : Response<String>


    @GET("anime/{slug}")
    suspend fun getAnimeDetails(slug: String) : Response<String>
}