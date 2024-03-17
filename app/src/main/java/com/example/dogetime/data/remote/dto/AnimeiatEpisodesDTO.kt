package com.example.dogetime.data.remote.dto

import com.example.dogetime.data.remote.model.DataXX
import com.example.dogetime.data.remote.model.LinksX
import com.example.dogetime.data.remote.model.MetaX

data class AnimeiatEpisodesDTO(
    val `data`: List<DataXX>,
    val links: LinksX,
    val meta: MetaX
)