package com.example.kotlinmovieapp.data.remote

class MoviesSource(
    private val MoviesAPI: MoviesAPI,
    private val source: String,
    private val query: String
) {
    suspend fun FetchMovies () {
        return try {
            val MoviesResponse = MoviesAPI.getMovies(source = source, query = query  )
        } catch (e:Exception) {
            e.printStackTrace()
            throw Error(e)
        }
    }

}