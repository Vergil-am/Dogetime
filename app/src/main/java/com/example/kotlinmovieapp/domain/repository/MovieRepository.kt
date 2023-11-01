package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO

interface MovieRepository {

    suspend fun getTrending() : MoviesDTO
    suspend fun getPopular(page : Int) : MoviesDTO


    suspend fun getShows(page : Int) : MoviesDTO


    suspend fun getMovie(movieId: Int) : MovieDetailsDTO
}