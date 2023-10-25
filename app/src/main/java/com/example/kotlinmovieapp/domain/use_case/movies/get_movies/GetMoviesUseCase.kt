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
    operator fun invoke(): Flow<MoviesDTO> = flow {
        try {
            val movies = repo.getTrending().results
        } catch (_: HttpException) {

        } catch (_: IOException) {

        }
    }
}