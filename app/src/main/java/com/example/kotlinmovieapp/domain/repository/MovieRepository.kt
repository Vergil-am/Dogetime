package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO

interface MovieRepository {

    suspend fun getTrending() : MoviesDTO
    suspend fun getMovies(page : Int, catalog: String) : MoviesDTO


    suspend fun getShows(page : Int) : MoviesDTO


    suspend fun getMovie(movieId: Int) : MovieDetailsDTO

    suspend fun getShow(showId: Int) : ShowDetailsDTO

    suspend fun getSeason(seasonId: Int, season: Int) : SeasonDTO
}