package dev.dogetime.data

import dev.dogetime.data.local.FavoriteDao
import dev.dogetime.data.local.FavoriteEntity
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

/** Favorites stored in the local database. */
class FavoritesRepository(private val dao: FavoriteDao) {

    /** All favorites, most recently added first. */
    fun observeFavorites(): Flow<List<MediaItem>> =
        dao.observeAll().map { favorites -> favorites.map { it.toMediaItem() } }

    fun observeIsFavorite(type: MediaType, id: Int): Flow<Boolean> =
        dao.observeExists(id, type.name)

    /** Adds [item] to favorites, or removes it if it is already there. */
    suspend fun toggle(item: MediaItem) {
        if (dao.exists(item.id, item.type.name)) {
            dao.delete(item.id, item.type.name)
        } else {
            dao.upsert(item.toEntity())
        }
    }
}

private fun MediaItem.toEntity(): FavoriteEntity = FavoriteEntity(
    id = id,
    type = type.name,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    year = year,
    addedAt = Clock.System.now().toEpochMilliseconds(),
)

private fun FavoriteEntity.toMediaItem(): MediaItem = MediaItem(
    id = id,
    type = MediaType.valueOf(type),
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    year = year,
)
