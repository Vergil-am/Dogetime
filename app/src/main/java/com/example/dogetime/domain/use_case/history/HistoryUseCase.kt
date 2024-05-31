package com.example.dogetime.domain.use_case.history

import com.example.dogetime.data.local.entities.HistoryEpisode
import com.example.dogetime.data.local.entities.HistoryMedia
import com.example.dogetime.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HistoryUseCase @Inject constructor(
    private val repo: HistoryRepository
) {

    fun getHistory(): Flow<List<HistoryMedia>> = flow {
        try {
            val history = repo.getHistory()
            emit(history)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }

    }

    fun getHistoryById(id: String): Flow<HistoryMedia?> = flow {
        try {
            val history = repo.getHistoryById(id)
            emit(history)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }

    fun getEpisodesBySerie(pid: String): Flow<List<HistoryEpisode>> = flow {
        try {
            val episodes = repo.getEpisodesBySerie(pid)
            emit(episodes)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    suspend fun addToHistory(media: HistoryMedia) {
        try {
            repo.addToHistory(media)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addEpisodeToHistory(episode: HistoryEpisode) {
        try {
            repo.addEpisode(episode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}