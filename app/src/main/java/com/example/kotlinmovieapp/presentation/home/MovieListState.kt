package com.example.kotlinmovieapp.presentation.home

import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.util.Resource

data class MovieListState(
    val movies: Resource<List<MovieHome>>?  = null,
    val shows: Resource<List<MovieHome>>? = null,
    val anime: List<MovieHome>? = null,
    val watchList: List<MovieHome>? = null,
)
