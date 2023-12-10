package com.example.kotlinmovieapp.data.remote.dto

import com.example.kotlinmovieapp.data.remote.model.Data
import com.example.kotlinmovieapp.data.remote.model.Links
import com.example.kotlinmovieapp.data.remote.model.Meta

data class AnimeiatDTO(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)