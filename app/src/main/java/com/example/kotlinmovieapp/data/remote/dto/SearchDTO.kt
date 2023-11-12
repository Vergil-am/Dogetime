package com.example.kotlinmovieapp.data.remote.dto

import com.example.kotlinmovieapp.domain.model.Result

data class SearchDTO(
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)