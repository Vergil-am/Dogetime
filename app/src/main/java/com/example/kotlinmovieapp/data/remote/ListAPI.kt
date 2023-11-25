package com.example.kotlinmovieapp.data.remote
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.util.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ListAPI {

    @GET("account/{account_id}/watchlist/{type}")
    suspend fun getWatchList(
        @Path("account_id") accountId: Int = Constants.ACCOUNT_ID,
        @Path("type") type: String,
        @Query("session_id") sessionId: String = Constants.SESSION_ID,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): MoviesDTO

    @GET("account/{account_id}/favorite/{type}")
    suspend fun getFavorites(
        @Path("account_id") accountId: Int = Constants.ACCOUNT_ID,
        @Path("type") type: String = "tv",
        @Query("session_id") sessionId: String = Constants.SESSION_ID,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): MoviesDTO

}