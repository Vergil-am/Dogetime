package com.example.kotlinmovieapp.domain.model

data class MyMovieDetails(
    val adult: Boolean,
    val backdrop_path: String,
    val belongs_to_collection: BelongsToCollection,
    val genres: List<Genre>,
    val id: Int,
    val imdb_id: String,
    val overview: String,
    val poster: String,
    val release_date: String,
    val runtime: Int?,
//    val spoken_languages: List<SpokenLanguage>,
    val status: String,
    val tagline: String,
    val title: String,
//    val video: Boolean,
    val rating: Double,
    val last_air_date: String?,
    val last_episode_to_air: LastEpisodeToAir?,
    val seasons: List<Season>?,
)
