package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.data.remote.dto.RequestTokenDTO
import com.example.kotlinmovieapp.data.remote.dto.SessionDTO
import com.example.kotlinmovieapp.domain.model.TokenReqBody
import com.example.kotlinmovieapp.util.Constants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthAPI {
    @GET("authentication/token/new")
    suspend fun generateToken(
        @Query("api_key") apiKey : String = Constants.API_KEY
    ) : RequestTokenDTO

    @POST("authentication/session/new")
    suspend fun createSession(
        @Body request: TokenReqBody,
        @Query("api_key") apiKey : String = Constants.API_KEY
    ): SessionDTO
}