package com.example.dogetime.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface CimaLekAPI {


    @GET("/")
    suspend fun getLatest() : Response<String>
}