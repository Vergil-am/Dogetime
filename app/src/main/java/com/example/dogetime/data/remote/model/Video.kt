package com.example.dogetime.data.remote.model

data class Video(
    val created_at: String,
    val duration: Double,
    val id: Int,
    val name: String,
    val original: String,
    val processed_at: String,
    val processing: String,
    val progress: String,
    val slug: String,
    val thumbnail: Thumbnail,
    val thumbnail_id: Int,
    val updated_at: String
)