package com.example.kotlinmovieapp.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "list_media_join_table",
    primaryKeys = ["listId"],
    foreignKeys = [
        ForeignKey(entity = Media::class, parentColumns = ["mediaId"], childColumns = ["id"])
    ])
data class ListMediaJoinTable(
    val listId : Int,
    val mediaId: Int
)
