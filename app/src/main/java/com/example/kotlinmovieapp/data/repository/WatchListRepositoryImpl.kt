package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.local.dao.WatchListDAO
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia
import com.example.kotlinmovieapp.domain.repository.WatchListRepository
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

    override suspend fun getMediaById(id: Int): WatchListMedia {
        return watchList.getMediaById(id)
    }

}