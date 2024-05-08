package com.example.dogetime.data.remote.dto

class AnimeCatDTO : ArrayList<AnimeCatDTOItem>()


data class AnimeCatDTOItem(
    val genres: List<String>,
    val id: Int,
    val nb_eps: String,
    val others: String,
    val popularity: Double,
    val score: String,
    val start_date_year: String,
    val status: String,
    val title: String,
    val title_english: String,
    val title_french: Any,
    val title_romanji: String,
    val type: String,
    val url: String,
    val url_image: String
)