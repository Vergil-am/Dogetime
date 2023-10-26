package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.MoviesAPI
import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepoImplementation @Inject constructor(
    private val api : MoviesAPI
): MovieRepository {
    override suspend fun getTrending(): MoviesDTO {
        return api.getTrending()
    }

    override suspend fun getPopular(page : Int): MoviesDTO {
        return api.getPopular(page)
    }

    override suspend fun getMovie(movieId: Int): MovieDetailsDTO {
        return api.getMovie(movieId)
    }


}