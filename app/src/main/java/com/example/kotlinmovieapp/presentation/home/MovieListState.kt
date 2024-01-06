package com.example.kotlinmovieapp.presentation.home

import com.example.kotlinmovieapp.domain.model.MovieHome

data class MovieListState(
    val movies: List<MovieHome>?  = null,
    val shows: List<MovieHome>? = null,
    val anime: List<MovieHome>? = null,
    val watchList: List<MovieHome>? = null,
)
