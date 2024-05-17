package com.example.dogetime.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface MyCimaAPI {


    @GET("/")
    suspend fun getLatest(): Response<String>

    @GET("{id}")
    suspend fun getDetails(
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
}