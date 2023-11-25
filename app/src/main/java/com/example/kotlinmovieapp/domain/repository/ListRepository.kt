package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO

interface ListRepository {
    suspend fun getWatchList(
    ): MoviesDTO
    suspend fun getFavorites(
        type: String
    ): MoviesDTO

}