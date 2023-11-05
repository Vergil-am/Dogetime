package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO
import com.example.kotlinmovieapp.util.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesAPI {
    @GET("trending/movie/day")
    suspend fun getTrending( @Query("api_key") apiKey : String = API_KEY ) : MoviesDTO

    @GET("movie/popular")
    suspend fun getPopular (
        @Query("page") page: Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : MoviesDTO

    @GET("movie/{movie_id}")
    suspend fun getMovie (
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ) : MovieDetailsDTO

    @GET("tv/{show_id}")
    suspend fun getShow(
        @Path("show_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ) : ShowDetailsDTO

    @GET("trending/tv/day")
    suspend fun getShows(
        @Query("page") page: Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : MoviesDTO

    @GET("tv/{id}/season/{season}")
    suspend fun getSeason(
        @Path("id") id : Int,
        @Path("season") season: Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : SeasonDTO
}