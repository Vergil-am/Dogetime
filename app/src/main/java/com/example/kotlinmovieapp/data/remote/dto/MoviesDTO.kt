package com.example.kotlinmovieapp.data.remote.dto

import com.example.kotlinmovieapp.ui.theme.screens.Movie
data class MoviesDTO(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)