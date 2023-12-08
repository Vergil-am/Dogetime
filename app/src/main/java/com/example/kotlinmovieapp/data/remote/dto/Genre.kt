package com.example.kotlinmovieapp.data.remote.dto

data class Genre(
    val created_at: String,
    val description: String,
    val id: Int,
    val name: String,
    val pivot: Pivot,
    val slug: String,
    val updated_at: String
)