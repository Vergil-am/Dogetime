package com.example.kotlinmovieapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kotlinmovieapp.local.entities.WatchListMedia


@Dao
interface WatchListDAO{

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addToList(media: WatchListMedia)


    @Query("SELECT * FROM watchlist")
    suspend fun getAll() : List<WatchListMedia>

    @Query("SELECT * FROM watchlist WHERE list = :list")
    suspend fun getList(list: String) : List<WatchListMedia>






}