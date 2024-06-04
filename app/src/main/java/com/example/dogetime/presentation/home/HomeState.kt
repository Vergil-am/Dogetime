package com.example.dogetime.presentation.home

import com.example.dogetime.domain.model.MovieHome

data class HomeState(
    val movies: MovieState = MovieState(),
    val shows: MovieState = MovieState(),
    val animeAR: MovieState = MovieState(),
    val animeEN: MovieState = MovieState(),
    val animeFR: MovieState = MovieState(),
    val myCimaMovies: MovieState = MovieState(),
    val myCimaShows: MovieState = MovieState(),
    val watchList: List<MovieHome>? = null,
)

data class MovieState(
    val isLoading: Boolean = false,
    val data: List<MovieHome>? = null,
    val error: String? = null
)