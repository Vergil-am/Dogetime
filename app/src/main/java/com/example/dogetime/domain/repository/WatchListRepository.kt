package com.example.dogetime.domain.repository

import com.example.dogetime.data.local.entities.WatchListMedia

interface WatchListRepository {
    suspend fun getAllLists() : List<WatchListMedia>

    suspend fun getList(list: String) : List<WatchListMedia>

    suspend fun addToWatchList(media: WatchListMedia)

    suspend fun getMediaById(id: String) : WatchListMedia

    suspend fun deleteFromList(media: WatchListMedia)

}