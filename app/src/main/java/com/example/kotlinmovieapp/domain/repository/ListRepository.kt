package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListResDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO

interface ListRepository {
    suspend fun getWatchList(
        type: String
    ): MoviesDTO

    suspend fun addToWatchList(
        body: AddToWatchListDTO
    ): AddToWatchListResDTO
    suspend fun getFavorites(
        type: String
    ): MoviesDTO

}