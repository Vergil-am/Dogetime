package com.example.dogetime.data.remote.dto

import com.example.dogetime.data.remote.model.Data
import com.example.dogetime.data.remote.model.Links
import com.example.dogetime.data.remote.model.Meta

data class AnimeiatDTO(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)