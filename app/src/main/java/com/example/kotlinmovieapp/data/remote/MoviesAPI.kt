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


    //Trending
    @GET("trending/movie/day")
    suspend fun getTrending( @Query("api_key") apiKey : String = API_KEY ) : MoviesDTO

    @GET("trending/tv/day")
    suspend fun getTrendingShows(
        @Query("page") page: Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : MoviesDTO


    //Movies
    @GET("movie/{catalog}")
    suspend fun getMovies(
        @Path("catalog") catalog: String = "popular",
        @Query("page") page: Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : MoviesDTO

    @GET("movie/{movie_id}")
    suspend fun getMovie (
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ) : MovieDetailsDTO


    //Shows

    @GET("tv/{catalog}")
    suspend fun getShows(
        @Path("catalog") catalog: String = "popular",
        @Query("page") page : Int = 1,
        @Query("api_key") apiKey: String = API_KEY
    ) : MoviesDTO

    @GET("tv/{show_id}")
    suspend fun getShow(
        @Path("show_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ) : ShowDetailsDTO

    @GET("tv/{id}/season/{season}")
    suspend fun getSeason(
        @Path("id") id : Int,
        @Path("season") season: Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : SeasonDTO
}