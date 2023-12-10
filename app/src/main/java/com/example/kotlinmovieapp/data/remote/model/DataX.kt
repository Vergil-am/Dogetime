package com.example.kotlinmovieapp.data.remote.model

data class DataX(
    val age: String,
    val anime_name: String,
    val created_at: String,
    val genres: List<Genre>,
    val id: Int,
    val other_names: String,
    val poster_path: String,
    val published: Boolean,
    val published_at: String,
    val slug: String,
    val status: String,
    val story: String,
    val studios: List<Studio>,
    val total_episodes: Int,
    val type: String,
    val updated_at: String,
    val year: Year,
    val year_id: Int
)