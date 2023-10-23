package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.MoviesAPI
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.domain.repository.MoviesRepo
import kotlinx.coroutines.flow.Flow

class MoviesRepositoryImplementation (
    private val moviesAPI: MoviesAPI
): MoviesRepo {
    override fun getMovies(source: String.Companion, query: String): Flow<MoviesDTO> {
        TODO("Not yet implemented")
    }
}