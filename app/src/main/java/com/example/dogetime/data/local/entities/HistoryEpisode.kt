package com.example.dogetime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode")
data class HistoryEpisode(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val pid: String,
    val number: Int,
    val progress: Long,
    val duration: Long
)
