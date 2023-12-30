package com.example.kotlinmovieapp.domain.model

data class Details(
    val id: String,
    val imdbId: String?,
    val title: String,
    val backdrop: String,
    val poster: String,
    val homepage: String,
    val genres: List<String>,
    val overview: String,
    val releaseDate: String,
    val runtime: Int?,
    val status: String,
    val tagline: String?,
    val rating: Double?,
    val type: String,
    val seasons: List<Season>?,
    val lastAirDate: String?,
    val episodes: String?,

    )
