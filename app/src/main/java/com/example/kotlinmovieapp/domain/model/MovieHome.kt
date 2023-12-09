package com.example.kotlinmovieapp.domain.model

data class MovieHome(
    val id: Int,
    val title: String,
    val type: String,
    val poster: String,
    val slug: String?
)
