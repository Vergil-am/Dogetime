package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.domain.model.VidSrcSources
import com.example.kotlinmovieapp.domain.model.VidsrcSource
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface VidsrcToAPI {
    @GET
    suspend fun getMovie(
        @Url url: String
    ): Response<String>

    @GET("ajax/embed/episode/{dataId}/sources")
    suspend fun getSources(
        @Path("dataId") dataId: String
    ): VidSrcSources

    @GET("ajax/embed/source/{sourceId}")
    suspend fun getSource(
        @Path("sourceId") sourceId: String
    ): VidsrcSource
}