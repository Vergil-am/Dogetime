package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.util.Constants
import com.example.kotlinmovieapp.util.Constants.ACCOUNT_ID
import com.example.kotlinmovieapp.util.Constants.SESSION_ID
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ListAPI {
    @GET("{account_id}/watchlist/{type}")
    fun getWatchList(
        @Path("account_id") accountId: Int = ACCOUNT_ID,
        @Path("type") type: String = "movies",
        @Query("session_id") sessionId : String = SESSION_ID,
        @Query("api_key") apiKey : String = Constants.API_KEY
    ): MoviesDTO

    @GET("{account_id}/favorite/{type}")
    fun getFavorites(
        @Path("account_id") accountId: Int = ACCOUNT_ID,
        @Path("type") type: String = "movies",
        @Query("session_id") sessionId : String = SESSION_ID,
        @Query("api_key") apiKey : String = Constants.API_KEY
    ): MoviesDTO
}