package com.example.dogetime.data.repository

import com.example.dogetime.data.local.dao.WatchListDAO
import com.example.dogetime.data.local.entities.WatchListMedia
import com.example.dogetime.domain.repository.WatchListRepository
import javax.inject.Inject

class WatchListRepositoryImpl @Inject constructor(
    private val watchList : WatchListDAO
): WatchListRepository {

    override suspend fun getAllLists() : List<WatchListMedia> {
        return  watchList.getAll()
    }

    override suspend fun getList(list: String) : List<WatchListMedia>{
        return watchList.getList(list)
    }

    override suspend fun addToWatchList(media: WatchListMedia) {
        return watchList.addToList(media)
    }

    override suspend fun getMediaById(id: String): WatchListMedia {
        return watchList.getMediaById(id)
    }

    override suspend fun deleteFromList(media: WatchListMedia) {
        return watchList.deleteFromList(media)
    }

}