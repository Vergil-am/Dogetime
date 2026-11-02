package dev.dogetime.data

import dev.dogetime.data.local.WatchHistoryDao
import dev.dogetime.data.local.WatchHistoryEntity
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.WatchHistoryItem
import dev.dogetime.domain.WatchTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

/**
 * Repository for managing watch history, progress, and resume timestamps.
 */
class WatchHistoryRepository(private val dao: WatchHistoryDao) {

    /** Observes the latest watch history entry per media item, newest first. */
    fun observeHistory(): Flow<List<WatchHistoryItem>> =
        dao.observeAllRecent().map { list -> list.map { it.toDomain() } }

    /** Observes all watched episodes/entries for a given media title. */
    fun observeByMedia(type: MediaType, id: Int): Flow<List<WatchHistoryItem>> =
        dao.observeByMedia(id, type.name).map { list -> list.map { it.toDomain() } }

    /** Observes the watch history entry for a specific key. */
    fun observeHistory(type: MediaType, id: Int, season: Int? = null, episode: Int? = null): Flow<WatchHistoryItem?> {
        val key = WatchHistoryEntity.key(type.name, id, season, episode)
        return dao.observeByKey(key).map { it?.toDomain() }
    }

    /** Fetches the watch history entry for a specific movie or episode. */
    suspend fun getHistory(type: MediaType, id: Int, season: Int? = null, episode: Int? = null): WatchHistoryItem? {
        val key = WatchHistoryEntity.key(type.name, id, season, episode)
        return dao.getByKey(key)?.toDomain()
    }

    /** Fetches the latest watched episode or movie for a title. */
    suspend fun getLatestHistory(type: MediaType, id: Int): WatchHistoryItem? =
        dao.getLatestByMedia(id, type.name)?.toDomain()

    /**
     * Records or updates watch progress for a movie or episode.
     *
     * @param target The target being watched (Movie or Episode).
     * @param mediaItem The media card item (for displaying in Continue Watching).
     * @param positionMs Current position in milliseconds.
     * @param durationMs Total duration in milliseconds.
     */
    suspend fun saveProgress(
        target: WatchTarget,
        mediaItem: MediaItem,
        positionMs: Long,
        durationMs: Long,
    ) {
        val (season, episode) = when (target) {
            is WatchTarget.Movie -> Pair(null, null)
            is WatchTarget.Episode -> Pair(target.season, target.episode)
        }

        val key = WatchHistoryEntity.key(mediaItem.type.name, mediaItem.id, season, episode)
        val entity = WatchHistoryEntity(
            watchKey = key,
            id = mediaItem.id,
            type = mediaItem.type.name,
            title = mediaItem.title,
            overview = mediaItem.overview,
            posterUrl = mediaItem.posterUrl,
            backdropUrl = mediaItem.backdropUrl,
            rating = mediaItem.rating,
            year = mediaItem.year,
            season = season,
            episode = episode,
            positionMs = positionMs,
            durationMs = durationMs,
            lastWatchedAt = Clock.System.now().toEpochMilliseconds(),
        )
        dao.upsert(entity)
    }

    /** Removes watch progress for a specific key. */
    suspend fun delete(type: MediaType, id: Int, season: Int? = null, episode: Int? = null) {
        val key = WatchHistoryEntity.key(type.name, id, season, episode)
        dao.deleteByKey(key)
    }

    /** Removes all watch history for a given media title. */
    suspend fun deleteByMedia(type: MediaType, id: Int) {
        dao.deleteByMedia(id, type.name)
    }

    /** Clears all watch history. */
    suspend fun clearAll() {
        dao.clearAll()
    }
}

private fun WatchHistoryEntity.toDomain(): WatchHistoryItem = WatchHistoryItem(
    mediaItem = MediaItem(
        id = id,
        type = MediaType.valueOf(type),
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        rating = rating,
        year = year,
    ),
    season = season,
    episode = episode,
    positionMs = positionMs,
    durationMs = durationMs,
    lastWatchedAt = lastWatchedAt,
)
