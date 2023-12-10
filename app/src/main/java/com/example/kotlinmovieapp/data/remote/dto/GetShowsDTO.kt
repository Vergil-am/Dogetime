package com.example.kotlinmovieapp.data.remote.dto

import com.example.kotlinmovieapp.domain.model.Show

data class GetShowsDTO(
    val page: Int,
    val results: List<Show>,
    val total_pages: Int,
    val total_results: Int
)