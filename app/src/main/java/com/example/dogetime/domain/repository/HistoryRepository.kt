package com.example.dogetime.domain.repository

import com.example.dogetime.data.local.entities.HistoryEpisode
import com.example.dogetime.data.local.entities.HistoryMedia

interface HistoryRepository {


    suspend fun addToHistory(media: HistoryMedia)
    suspend fun getHistory() : List<HistoryMedia>

    suspend fun getHistoryById(id: String) : HistoryMedia?
    suspend fun deleteFromHistory(id: String)

    suspend fun getEpisodesBySerie(pid: String) : List<HistoryEpisode>
    suspend fun getEpisodeById(id: String) : HistoryEpisode?

    suspend fun addEpisode(episode: HistoryEpisode)
}