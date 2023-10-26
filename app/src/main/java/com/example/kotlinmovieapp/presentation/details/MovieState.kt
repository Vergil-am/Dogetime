package com.example.kotlinmovieapp.presentation.details

import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO

data class MovieState(
    var id : Int? = null,
    var movie: MovieDetailsDTO? = null
)
