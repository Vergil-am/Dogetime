package com.example.kotlinmovieapp.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.kotlinmovieapp.local.entities.WatchListMedia


@Dao
interface WatchListDAO{

    @Upsert()
    suspend fun addToList(media: WatchListMedia)


    @Query("SELECT * FROM watchlist")
    suspend fun getAll() : List<WatchListMedia>

    @Query("SELECT * FROM watchlist WHERE list = :list")
    suspend fun getList(list: String) : List<WatchListMedia>






}