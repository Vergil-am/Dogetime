package com.example.kotlinmovieapp.domain.use_case.list

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class ListUseCase  @Inject constructor(
    private val repo : ListRepository
){
   fun getWatchList(type: String) : Flow<MoviesDTO> = flow {
        try {
            val res = repo.getWatchList(type)
            Log.e("LIST USE CASE", res.toString())
            emit(res)
        }catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }
    }

    suspend fun addToWatchList(body: AddToWatchListDTO) {
        try {
            repo.addToWatchList(body = body)
        } catch (e: HttpException) {
            Log.e("", "")
        } catch (e: IOException) {
            Log.e("", "")
        }
    }
    fun getFavorites(type: String) : Flow<MoviesDTO> = flow {
        try {
            val res = repo.getFavorites(type)
            emit(res)
        }catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }
    }
}