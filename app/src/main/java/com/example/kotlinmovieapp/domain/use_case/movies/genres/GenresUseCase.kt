package com.example.kotlinmovieapp.domain.use_case.movies.genres

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.GenresDTO
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject


class GenresUseCase @Inject constructor(
    private val repo: MovieRepository
){
    fun getGenres(type : String) : Flow<GenresDTO> = flow {
        try {
            val genres = repo.getGenres(type)
            emit(genres)
        }catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }
    }
}