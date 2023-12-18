package com.example.kotlinmovieapp.local.repository

import com.example.kotlinmovieapp.local.dao.WatchListDAO
import com.example.kotlinmovieapp.local.entities.WatchListMedia
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

}