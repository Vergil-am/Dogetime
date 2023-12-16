package com.example.kotlinmovieapp.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class Media(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val type: String,
    val poster: String
)
