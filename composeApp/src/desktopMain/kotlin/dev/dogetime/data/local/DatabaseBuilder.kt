package dev.dogetime.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

internal actual fun databaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val file = File(System.getProperty("user.home"), ".dogetime/$DATABASE_NAME")
    file.parentFile.mkdirs()
    return Room.databaseBuilder<AppDatabase>(name = file.absolutePath)
}
