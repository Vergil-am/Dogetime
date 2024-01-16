package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.domain.model.VidSrcSources
import com.example.kotlinmovieapp.domain.model.VidsrcSource
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VidsrcToAPI {
    @GET("embed/movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int
    ): Response<String>

    @GET("embed/movie/{id}/{season}/{episode}")
    suspend fun getTV(
        @Path("id") id: Int, @Path("season") season: Int, @Path("episode") episode: Int
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