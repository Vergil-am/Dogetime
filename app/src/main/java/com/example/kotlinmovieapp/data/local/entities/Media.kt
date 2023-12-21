package com.example.kotlinmovieapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchListMedia(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val type: String,
    val poster: String,
    val list: String,
    val slug: String?
)
