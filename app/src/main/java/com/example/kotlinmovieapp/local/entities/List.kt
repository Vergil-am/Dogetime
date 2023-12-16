
package com.example.kotlinmovieapp.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list")
data class List(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val media: Media
)