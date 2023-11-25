package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.ListAPI
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListResDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.domain.repository.ListRepository
import javax.inject.Inject

class ListRepoImplementation @Inject constructor(
    private val api: ListAPI
) : ListRepository{
    override suspend fun getWatchList(type: String): MoviesDTO {
        return api.getWatchList(type = type)
    }

    override suspend fun addToWatchList(body: AddToWatchListDTO): AddToWatchListResDTO {
        return api.addToWatchList(body = body)
    }

    override suspend fun getFavorites(type: String): MoviesDTO {
        return api.getFavorites(type = type)
    }

}