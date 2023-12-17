package com.example.kotlinmovieapp.local.repository

import com.example.kotlinmovieapp.local.dao.WatchListDAO
import com.example.kotlinmovieapp.local.entities.WatchListMedia

class WatchListRepository(
    private val watchList : WatchListDAO
){

    suspend fun getAllLists() : List<WatchListMedia> {
        return  watchList.getAll()
    }

    suspend fun getList(list: String) : List<WatchListMedia>{
        return watchList.getList(list)
    }

    suspend fun addToWatchList(media: WatchListMedia) {
        return watchList.addToList(media)
    }

}