package com.example.kotlinmovieapp.data.remote.dto

import com.example.kotlinmovieapp.domain.model.Episode

data class SeasonDTO(
    val _id: String,
    val air_date: String,
    val episodes: List<Episode>,
    val id: Int,
    val name: String,
    val overview: String,
    val poster_path: String,
    val season_number: Int,
    val vote_average: Double
)