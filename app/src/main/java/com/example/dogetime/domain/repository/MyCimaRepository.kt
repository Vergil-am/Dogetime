package com.example.dogetime.domain.repository

import retrofit2.Response

interface MyCimaRepository {
    suspend fun getLatest(): Response<String>
    suspend fun getDetails(id: String): Response<String>

    suspend fun getSources(url: String): Response<String>
    suspend fun getSeasons(url: String): Response<String>
}