package com.example.kotlinmovieapp.presentation.watchlist

import com.example.kotlinmovieapp.domain.model.Movie

data class ListState(
    val type: String = "movies",
    val movies: List<Movie> = mutableListOf(),
    val accountId: Int? = null,
    val sessionId: String? = null

)
