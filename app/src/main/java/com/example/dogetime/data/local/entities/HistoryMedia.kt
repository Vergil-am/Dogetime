package com.example.dogetime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryMedia(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val poster: String,
    val title: String,
    val type: String,
    val progress: Long?,
    val duration: Long?
)