package com.example.kotlinmovieapp.presentation.details

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO

data class MovieState(
    var id : Int? = null,
    val isLoading: Boolean = false,
    val movie: MovieDetailsDTO? = null,
    val show: ShowDetailsDTO? = null
)
