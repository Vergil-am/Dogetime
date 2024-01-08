package com.example.kotlinmovieapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kotlinmovieapp.data.local.dao.WatchListDAO
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia

@Database(
    entities = [WatchListMedia::class],
    version = 3,
    exportSchema = false
    )
abstract class ListDatabase : RoomDatabase() {
    abstract fun watchListDao(): WatchListDAO
}