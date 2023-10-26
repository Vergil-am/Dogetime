package com.example.kotlinmovieapp.domain.use_case.movies.get_movie

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
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
            emit(movie)
                } catch (_: HttpException) {
            Log.e("Movies Repo", "Http exception")
        } catch (_: IOException) {
            Log.e("Movies Repo", "Http exception")

        }
    }

}