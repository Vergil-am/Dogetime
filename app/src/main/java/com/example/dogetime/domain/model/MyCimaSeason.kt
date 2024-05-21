package com.example.dogetime.domain.model

data class MyCimaSeason(
    val title: String,
    val seasonNumber: Int,
    val episodes: List<MyCimaEpisode>
)
