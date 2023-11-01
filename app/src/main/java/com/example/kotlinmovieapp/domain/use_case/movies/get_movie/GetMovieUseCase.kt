package com.example.kotlinmovieapp.domain.use_case.movies.get_movie

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieUseCase @Inject constructor(
    private val repo : MovieRepository
) {
    fun getMovieDetails(id: Int): Flow<MovieDetailsDTO> = flow {
        try {
            val movie = repo.getMovie(movieId = id)
            Log.d("MOVIE REPO", movie.toString())
            emit(movie)
        } catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }
    }
    fun getShow(id: Int): Flow<ShowDetailsDTO> = flow {
        try {
            val  show = repo.getShow(id)
            emit(show)
        }catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }
    }

}