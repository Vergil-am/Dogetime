package com.example.kotlinmovieapp.local.repository

import com.example.kotlinmovieapp.local.entities.WatchListMedia

interface WatchListRepository {
    suspend fun getAllLists() : List<WatchListMedia>

    suspend fun getList(list: String) : List<WatchListMedia>

    suspend fun addToWatchList(media: WatchListMedia)
}