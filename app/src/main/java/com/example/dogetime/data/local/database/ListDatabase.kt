package com.example.dogetime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dogetime.data.local.dao.WatchListDAO
import com.example.dogetime.data.local.entities.WatchListMedia

@Database(
    entities = [WatchListMedia::class],
    version = 3,
    exportSchema = false
    )
abstract class ListDatabase : RoomDatabase() {
    abstract fun watchListDao(): WatchListDAO
}