package com.example.kotlinmovieapp.presentation.search

import com.example.kotlinmovieapp.domain.model.MovieHome

data class SearchState(
    var search: String = "",
    var movies: List<MovieHome>? = null,
    var shows : List<MovieHome>? = null,
    var anime : List<MovieHome>? = null
)
