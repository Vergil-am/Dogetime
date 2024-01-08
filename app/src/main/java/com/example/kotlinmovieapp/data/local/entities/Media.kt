package com.example.kotlinmovieapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchListMedia(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val type: String,
    val season: Int?,
    val episode: Int?,
    val poster: String,
    val list: String,
)
