package com.example.kotlinmovieapp.presentation.home

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO

data class MovieListState(
    val loading: Boolean = false,
    val movies: MoviesDTO? = null

)
