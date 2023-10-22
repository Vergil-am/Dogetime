package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.util.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MoviesAPI {

    @GET("movies")
    suspend fun getMovies(
        @Query("source") source: String,
        @Query("query") query: String,
        @Header("api_key") token: String = API_KEY
    )

}