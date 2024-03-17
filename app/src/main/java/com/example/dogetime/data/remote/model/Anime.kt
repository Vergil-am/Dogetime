package com.example.dogetime.data.remote.model

data class Anime(
    val age: String,
    val anime_name: String,
    val created_at: String,
    val genres: List<GenreX>,
    val id: Int,
    val other_names: String,
    val poster_path: String,
    val published: Int,
    val published_at: String,
    val slug: String,
    val status: String,
    val story: String,
    val studios: List<StudioX>,
    val total_episodes: Int,
    val type: String,
    val updated_at: String,
    val user_id: Int,
    val year: YearX,
    val year_id: Int
)