package com.example.kotlinmovieapp.domain.use_case.movies.get_movies

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.domain.model.Movie
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject


class GetMoviesUseCase @Inject constructor(
    private val repo : MovieRepository
) {
    // Trending
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
    fun getTrendingShows() : Flow<MoviesDTO> = flow {
        try {
            val shows = repo.getTrendingShows(1)
            emit(shows)
        } catch (_: HttpException) {

        } catch (_: IOException) {}

    }
    // Movies
    fun getMovies(catalog: String, page: Int): Flow<List<Movie>> = flow {
        try {
            val movies = repo.getMovies(page = page, catalog)
            emit(movies.results)
        } catch (_: HttpException) {
            Log.e("POPULAR", "Http exception")
        } catch (_: IOException) {
            Log.e("POPULAR", "Http exception")

        }
    }
    // Shows
    fun getShows(catalog: String, page: Int): Flow<List<Movie>> = flow {
        try {
            val shows = repo.getShows(page = page, catalog)
            emit(shows.results)
        } catch (_: HttpException) {
            Log.e("POPULAR", "Http exception")
        } catch (_: IOException) {
            Log.e("POPULAR", "Http exception")

        }

    }

}