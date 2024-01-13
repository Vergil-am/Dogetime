package com.example.kotlinmovieapp.domain.use_case.movies.get_movies

import android.util.Log
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import com.example.kotlinmovieapp.util.Constants
import com.example.kotlinmovieapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject


class GetMoviesUseCase @Inject constructor(
    private val repo: MovieRepository
) {
    // Trending
    fun getTrending(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val movies = repo.getTrending().results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.title,
                    type = "movie",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}",
                )
            }
            emit(Resource.Success(movies))
        } catch (_: HttpException) {
            Log.e("TRENDING", "Http exception")
            emit(Resource.Error("Server error"))
        } catch (_: IOException) {
            Log.e("TRENDING", "Http exception")
            emit(Resource.Error("Connection error"))

        }
    }

    fun getTrendingShows(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val shows = repo.getTrendingShows(1).results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.name,
                    type = "show",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}",
                )
            }
            emit(Resource.Success(shows))
        } catch (_: HttpException) {
            emit(Resource.Error("Server error"))
        } catch (_: IOException) {
            emit(Resource.Error("Connection error"))
        }

    }

    // Movies
    fun getMovies(catalog: String, page: Int): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val movies = repo.getMovies(page = page, catalog).results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.title,
                    type = "movie",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}",
                )
            }
            emit(Resource.Success(movies))
        } catch (_: HttpException) {
            Log.e("POPULAR", "Http exception")
            emit(Resource.Error("Server error"))
        } catch (_: IOException) {
            emit(Resource.Error("Connection error"))
            Log.e("POPULAR", "Http exception")

        }
    }

    // Shows
    fun getShows(catalog: String, page: Int): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val shows = repo.getShows(page = page, catalog).results.map {
                MovieHome(
                    id = it.id.toString(),
                    title = it.name,
                    type = "show",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}",
                )
            }
            emit(Resource.Success(shows))
        } catch (_: HttpException) {
            emit(Resource.Error("Server error"))
            Log.e("POPULAR", "Http exception")
        } catch (_: IOException) {
            Log.e("POPULAR", "Http exception")
            emit(Resource.Error("Connection error"))
        }

    }

}