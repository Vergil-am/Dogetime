package com.example.kotlinmovieapp.domain.use_case.movies.get_movies

import android.util.Log
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import com.example.kotlinmovieapp.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject


class GetMoviesUseCase @Inject constructor(
    private val repo : MovieRepository
) {
    // Trending
    fun getTrending(): Flow<List<MovieHome>> = flow {
        try {
            val movies = repo.getTrending().results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.title,
                    type = "movie",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}" ,
                )
            }
            emit(movies)
        } catch (_: HttpException) {
            Log.e("TRENDING", "Http exception")
        } catch (_: IOException) {
            Log.e("TRENDING", "Http exception")

        }
    }
    fun getTrendingShows() : Flow<List<MovieHome>> = flow {
        try {
            val shows = repo.getTrendingShows(1).results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.name,
                    type = "show",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}" ,
                )
            }
            emit(shows)
        } catch (_: HttpException) {

        } catch (_: IOException) {}

    }
    // Movies
    fun getMovies(catalog: String, page: Int): Flow<List<MovieHome>> = flow {
        try {
            val movies = repo.getMovies(page = page, catalog).results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.title,
                    type = "movie",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}" ,
                )
            }
            emit(movies)
        } catch (_: HttpException) {
            Log.e("POPULAR", "Http exception")
        } catch (_: IOException) {
            Log.e("POPULAR", "Http exception")

        }
    }
    // Shows
    fun getShows(catalog: String, page: Int): Flow<List<MovieHome>> = flow {
        try {
            val shows = repo.getShows(page = page, catalog).results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.name,
                    type = "show",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}" ,
                )
            }
            emit(shows)
        } catch (_: HttpException) {
            Log.e("POPULAR", "Http exception")
        } catch (_: IOException) {
            Log.e("POPULAR", "Http exception")

        }

    }

}