package com.example.dogetime.domain.model

data class AnimeDetails(
    val details: Details,
    val episodes: List<OkanimeEpisode>,
)