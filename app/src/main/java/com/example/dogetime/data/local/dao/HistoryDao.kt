package com.example.dogetime.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.dogetime.data.local.entities.HistoryEpisode
import com.example.dogetime.data.local.entities.HistoryMedia

@Dao
interface HistoryDao {
    @Upsert
    suspend fun addToHistory(media: HistoryMedia)

    @Query("SELECT * FROM history")
    suspend fun getHistory(): List<HistoryMedia>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryMedia(id: String): HistoryMedia

    @Query("SELECT * FROM episode WHERE pid = :pid")
    suspend fun getEpisodesBySerie(pid: String): List<HistoryEpisode>

    @Query("SELECT * FROM episode WHERE id = :id")
    suspend fun getEpisodeById(id: String): HistoryEpisode

    @Upsert
    suspend fun addEpisode(episode: HistoryEpisode)
}