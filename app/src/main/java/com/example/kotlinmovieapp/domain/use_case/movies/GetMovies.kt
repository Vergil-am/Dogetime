package com.example.kotlinmovieapp.domain.use_case.movies

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.domain.repository.MoviesRepo
import kotlinx.coroutines.flow.Flow

class GetMovies(
    private val MoviesRepo: MoviesRepo
) {
    operator fun invoke(source: String, query: String): Flow<MoviesDTO> {
        return MoviesRepo.getMovies(source = String , query = query)
    }
}