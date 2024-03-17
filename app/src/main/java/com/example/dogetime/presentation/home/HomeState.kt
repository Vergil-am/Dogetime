package com.example.dogetime.presentation.home

import com.example.dogetime.domain.model.MovieHome

data class HomeState(
    val movies: MovieState = MovieState(),
    val shows: MovieState = MovieState(),
    val anime: MovieState = MovieState(),
    val watchList: List<MovieHome>? = null,
)

data class MovieState(
    val isLoading: Boolean = false,
    val data: List<MovieHome>? = null,
    val error: String? = null
)