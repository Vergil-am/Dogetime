package com.example.kotlinmovieapp.domain.repository
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import kotlinx.coroutines.flow.Flow


interface MoviesRepo {
    fun getMovies(source: String.Companion, query: String) : Flow<MoviesDTO>
}