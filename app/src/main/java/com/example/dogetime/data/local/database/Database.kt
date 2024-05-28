package com.example.dogetime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dogetime.data.local.dao.HistoryDao
import com.example.dogetime.data.local.dao.WatchListDAO
import com.example.dogetime.data.local.entities.HistoryEpisode
import com.example.dogetime.data.local.entities.HistoryMedia
import com.example.dogetime.data.local.entities.WatchListMedia

@Database(
    entities = [WatchListMedia::class, HistoryMedia::class, HistoryEpisode::class],
    version = 4,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun watchListDao(): WatchListDAO
    abstract fun historyDao(): HistoryDao
}