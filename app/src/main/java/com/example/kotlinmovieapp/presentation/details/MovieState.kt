package com.example.kotlinmovieapp.presentation.details

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO

data class MovieState(
    val isLoading: Boolean = false,
    var movie: MovieDetailsDTO? = null
)
