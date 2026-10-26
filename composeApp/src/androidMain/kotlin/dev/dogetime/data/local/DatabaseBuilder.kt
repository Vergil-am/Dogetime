package dev.dogetime.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.dogetime.AndroidApp

internal actual fun databaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = AndroidApp.context
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = context.getDatabasePath(DATABASE_NAME).absolutePath,
    )
}
