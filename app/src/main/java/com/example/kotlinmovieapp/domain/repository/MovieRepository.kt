package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO

interface MovieRepository {

    // Trending
    suspend fun getTrending() : MoviesDTO
    suspend fun getTrendingShows(page : Int) : MoviesDTO

    // Movies
    suspend fun getMovies(page : Int, catalog: String) : MoviesDTO

    suspend fun getMovie(movieId: Int) : MovieDetailsDTO


    // Shows
    suspend fun getShow(showId: Int) : ShowDetailsDTO
    suspend fun getShows(page: Int, catalog: String) : MoviesDTO

    suspend fun getSeason(seasonId: Int, season: Int) : SeasonDTO
}