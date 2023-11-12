package com.example.kotlinmovieapp.domain.use_case.movies.search

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.SearchDTO
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class Search @Inject constructor(
   private val repo : MovieRepository
) {
    fun getSearch(query: String): Flow<SearchDTO> = flow {
        try {
            val result = repo.getSearch(query)
            emit(result)
        } catch (_: HttpException) {
            Log.e("Search", "Http exception")
        }
        catch(_: IOException) {
            Log.e("TRENDING", "Http exception")
        }
    }
}