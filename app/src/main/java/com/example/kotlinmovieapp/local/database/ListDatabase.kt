package com.example.kotlinmovieapp.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinmovieapp.local.dao.ListDao
import com.example.kotlinmovieapp.local.entities.List
import com.example.kotlinmovieapp.local.entities.Media
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(
    entities = [List::class, Media::class],
    version = 1,
    exportSchema = false
    )
abstract class ListDatabase : RoomDatabase() {
    abstract fun listDao(): ListDao

    companion object{
        @Volatile
        private var INSTANCE: ListDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): ListDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ListDatabase::class.java,
                    "list_database"
                    ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}