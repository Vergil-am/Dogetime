package com.example.kotlinmovieapp.domain.use_case.movies.get_movies

import android.util.Log
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
            val movies = repo.getTrending()
            emit(movies)
        } catch (_: HttpException) {
            Log.e("TRENDING", "Http exception")
        } catch (_: IOException) {
            Log.e("TRENDING", "Http exception")

        }
    }
    fun getPopular(): Flow<MoviesDTO> = flow {
        try {
            val movies = repo.getPopular(page = 1)
            emit(movies)
        } catch (_: HttpException) {
            Log.e("POPULAR", "Http exception")
        } catch (_: IOException) {
            Log.e("POPULAR", "Http exception")

        }
    }
    fun getShows() : Flow<MoviesDTO> = flow {
        try {
            val shows = repo.getShows(1)
            emit(shows)
        } catch (_: HttpException) {

        } catch (_: IOException) {}

    }
}