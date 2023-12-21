package com.example.kotlinmovieapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kotlinmovieapp.data.local.dao.WatchListDAO
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia

@Database(
    entities = [WatchListMedia::class],
    version = 1,
    exportSchema = false
    )
abstract class ListDatabase : RoomDatabase() {
    abstract fun watchListDao(): WatchListDAO

//    companion object{
//        @Volatile
//        private var INSTANCE: ListDatabase? = null
//
//
//        @OptIn(InternalCoroutinesApi::class)
//        fun getDatabase(context: Context): ListDatabase {
//            val tempInstance = INSTANCE
//            if (tempInstance != null) {
//                return tempInstance
//            }
//            synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    ListDatabase::class.java,
//                    "watchlist_database"
//                    ).build()
//                INSTANCE = instance
//                return instance
//            }
//        }
//    }
}