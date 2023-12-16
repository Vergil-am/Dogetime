package com.example.kotlinmovieapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kotlinmovieapp.local.entities.List


@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createList(list: List)


    @Query("SELECT * FROM list")
    suspend fun getLists() : kotlin.collections.List<List>


}