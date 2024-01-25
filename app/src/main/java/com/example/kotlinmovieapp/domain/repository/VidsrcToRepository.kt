package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.domain.model.VidSrcSources
import com.example.kotlinmovieapp.domain.model.VidsrcSource
import retrofit2.Response

interface VidsrcToRepository {

    suspend fun getMovie(
        url: String
    ): Response<String>

    suspend fun getSources(
        dataId: String
    ): VidSrcSources

    suspend fun getSource(
        sourceId: String
    ): VidsrcSource
}