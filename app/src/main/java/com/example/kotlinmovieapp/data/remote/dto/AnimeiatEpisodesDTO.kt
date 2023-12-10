package com.example.kotlinmovieapp.data.remote.dto

import com.example.kotlinmovieapp.data.remote.model.DataXX
import com.example.kotlinmovieapp.data.remote.model.LinksX
import com.example.kotlinmovieapp.data.remote.model.MetaX

data class AnimeiatEpisodesDTO(
    val `data`: List<DataXX>,
    val links: LinksX,
    val meta: MetaX
)