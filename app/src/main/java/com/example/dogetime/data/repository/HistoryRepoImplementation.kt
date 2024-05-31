package com.example.dogetime.data.repository

import com.example.dogetime.data.local.dao.HistoryDao
import com.example.dogetime.data.local.entities.HistoryEpisode
import com.example.dogetime.data.local.entities.HistoryMedia
import com.example.dogetime.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryRepoImplementation @Inject constructor(
    private val dao: HistoryDao
) : HistoryRepository {
    override suspend fun addToHistory(media: HistoryMedia) {
        return dao.addToHistory(media)
    }

    override suspend fun getHistory(): List<HistoryMedia> {
        return dao.getHistory()
    }

    override suspend fun getHistoryById(id: String): HistoryMedia? {
        return dao.getHistoryMedia(id)
    }

    override suspend fun deleteFromHistory(id: String) {
//        return dao.del
    }

    override suspend fun getEpisodesBySerie(pid: String): List<HistoryEpisode> {
        return dao.getEpisodesBySerie(pid)
    }

    override suspend fun getEpisodeById(id: String): HistoryEpisode? {
        return dao.getEpisodeById(id)
    }

    override suspend fun addEpisode(episode: HistoryEpisode) {
        return dao.addEpisode(episode)
    }
}