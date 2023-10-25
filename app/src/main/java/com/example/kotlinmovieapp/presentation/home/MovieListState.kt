package com.example.kotlinmovieapp.presentation.home

import com.example.kotlinmovieapp.domain.model.Movie

data class MovieListState(
    val loading: Boolean = false,
    val movies: List<Movie> = emptyList(),

)
