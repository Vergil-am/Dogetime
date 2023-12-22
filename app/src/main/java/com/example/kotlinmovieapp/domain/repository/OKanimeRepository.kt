package com.example.kotlinmovieapp.domain.repository

import retrofit2.Response

interface OKanimeRepository {

    suspend fun getHome() : Response<String>

    suspend fun getAnimeDetails(slug: String) : Response<String>
}