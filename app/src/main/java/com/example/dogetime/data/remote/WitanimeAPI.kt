package com.example.dogetime.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WitanimeAPI {
    @GET("episode/{slug}")
    suspend fun getSources(
        @Path("slug", encoded = true) slug : String
    ) : Response<String>
}