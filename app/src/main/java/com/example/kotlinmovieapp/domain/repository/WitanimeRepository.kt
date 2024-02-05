package com.example.kotlinmovieapp.domain.repository

import retrofit2.Response


interface WitanimeRepository {
    suspend fun getSources(slug: String) : Response<String>
}