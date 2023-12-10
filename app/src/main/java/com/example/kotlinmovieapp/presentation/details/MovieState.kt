package com.example.kotlinmovieapp.presentation.details

import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.Movie

data class MovieState(
    val isLoading: Boolean = false,
    val media: Details? = null,
    val season: SeasonDTO? = null,
    var watchList: List<Movie> = listOf(),
)
