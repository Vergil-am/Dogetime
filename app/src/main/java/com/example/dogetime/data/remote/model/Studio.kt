package com.example.dogetime.data.remote.model

data class Studio(
    val created_at: String,
    val description: String,
    val id: Int,
    val name: String,
    val pivot: PivotX,
    val slug: String,
    val updated_at: String
)