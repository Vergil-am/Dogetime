package com.example.kotlinmovieapp.data.remote.dto

import com.example.kotlinmovieapp.data.remote.model.DataXXX
import com.example.kotlinmovieapp.data.remote.model.NextEpisode

data class AnimeiatEpisodeDTO(
    val `data`: DataXXX,
    val hash: String,
    val next_episode: NextEpisode,
    val prev_episode: Any
)