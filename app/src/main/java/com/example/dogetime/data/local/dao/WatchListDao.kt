package com.example.dogetime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dogetime.data.local.entities.WatchListMedia


@Dao
interface WatchListDAO{

    @Upsert()
    suspend fun addToList(media: WatchListMedia)


    @Query("SELECT * FROM watchlist")
    suspend fun getAll() : List<WatchListMedia>

    @Query("SELECT * FROM watchlist WHERE list = :list")
    suspend fun getList(list: String) : List<WatchListMedia>


    @Query("SELECT * FROM watchlist WHERE id = :id")
    suspend fun getMediaById(id: String) : WatchListMedia

    @Delete
    suspend fun deleteFromList(media: WatchListMedia)




}