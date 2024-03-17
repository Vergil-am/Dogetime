package com.example.dogetime.data.remote.dto

import com.example.dogetime.domain.model.Movie

data class MoviesDTO(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)