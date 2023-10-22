package com.example.kotlinmovieapp.domain.model

data class Movie (
    val id : Int,
    val adult: Boolean,
    val backDrop : String,
    val genres: List<Int>,
    val language: String,
    val title: String,
    val overview: String,
    val poster : String,
    val rating : Float,
)
