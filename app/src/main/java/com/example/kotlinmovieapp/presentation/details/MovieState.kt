package com.example.kotlinmovieapp.presentation.details

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO

data class MovieState(
    val isLoading: Boolean = false,
    val movie: MovieDetailsDTO? = null,
    val show: ShowDetailsDTO? = null,
    val seasons: SeasonDTO? = null
)
