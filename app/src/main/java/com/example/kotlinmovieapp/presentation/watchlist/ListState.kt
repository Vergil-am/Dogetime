package com.example.kotlinmovieapp.presentation.watchlist

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO

data class ListState(
    val movies: MoviesDTO? = null
)
