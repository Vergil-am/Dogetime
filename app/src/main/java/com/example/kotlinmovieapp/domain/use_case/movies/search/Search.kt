package com.example.kotlinmovieapp.domain.use_case.movies.search

import android.util.Log
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import com.example.kotlinmovieapp.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class Search @Inject constructor(
   private val repo : MovieRepository
) {
    fun searchMovies(query: String): Flow<List<MovieHome>> = flow {
        try {
            val movies = repo.searchMovies(query).results.filter {
                it.poster_path != null
            }.map {
                MovieHome(
                    id = it.id,
                    title = it.title,
                    type = "show",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}",
                    slug = null
                )
            }
            Log.e("Search Movies", movies.toString())
            emit(movies)
        } catch (_: HttpException) {
            Log.e("Search", "Http exception")
        }
        catch(_: IOException) {
            Log.e("Search", "Http exception")
        }
    }
    fun searchShows(query: String): Flow<List<MovieHome>> = flow {
        try {
            val shows = repo.searchShows(query).results.filter { it.poster_path != null }.map {
                MovieHome(
                    id = it.id,
                    title = it.name,
                    type = "show",
                    poster = "${Constants.IMAGE_BASE_URL}/w200/${it.poster_path}",
                    slug = null
                )
            }

            Log.e("Search Shows", shows.toString())
            emit(shows)
        } catch (_: HttpException) {
            Log.e("Search", "Http exception")
        }
        catch(_: IOException) {
            Log.e("Search", "Http exception")
        }
    }
}