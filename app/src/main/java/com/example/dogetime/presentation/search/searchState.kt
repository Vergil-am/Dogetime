package com.example.dogetime.presentation.search

import com.example.dogetime.domain.model.MovieHome

data class SearchState(
    var search: String = "",
    var movies: List<MovieHome>? = null,
    var shows : List<MovieHome>? = null,
    var anime : List<MovieHome>? = null
)
