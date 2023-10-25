package com.example.kotlinmovieapp.domain.use_case.movies.get_movies

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject


class GetMoviesUseCase @Inject constructor(
    private val repo : MovieRepository
) {
    fun getTrending(): Flow<MoviesDTO> = flow {
        try {
            repo.getTrending()
        } catch (_: HttpException) {
            print("http exception")
        } catch (_: IOException) {
            print("IO exception")
        }
    }
}