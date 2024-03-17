package com.example.dogetime.data.remote.dto

import com.example.dogetime.domain.model.Show

data class GetShowsDTO(
    val page: Int,
    val results: List<Show>,
    val total_pages: Int,
    val total_results: Int
)