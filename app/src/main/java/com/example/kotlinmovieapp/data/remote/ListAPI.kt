package com.example.kotlinmovieapp.data.remote
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListResDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.util.Constants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ListAPI {

    // WatchList
    @GET("account/{account_id}/watchlist/{type}")
    suspend fun getWatchList(
        @Path("account_id") accountId: Int,
        @Path("type") type: String,
        @Query("session_id") sessionId: String,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): MoviesDTO

    @POST("account/{account_id}/watchlist")
    suspend fun addToWatchList(
        @Path("account_id") accountId: Int = Constants.ACCOUNT_ID,
        @Query("session_id") sessionId: String = Constants.SESSION_ID,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Body body : AddToWatchListDTO
    ): AddToWatchListResDTO

    // Favorites
    @GET("account/{account_id}/favorite/{type}")
    suspend fun getFavorites(
        @Path("account_id") accountId: Int = Constants.ACCOUNT_ID,
        @Path("type") type: String = "tv",
        @Query("session_id") sessionId: String = Constants.SESSION_ID,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): MoviesDTO



}