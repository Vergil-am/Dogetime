package dev.dogetime.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Watch history entry for a movie, TV show, or anime episode.
 *
 * For movies: [season] and [episode] are null, keyed by "$type:$id".
 * For TV shows / Anime: [season] and [episode] hold the episode coordinates,
 * keyed by "$type:$id:$season:$episode".
 */
@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey
    val watchKey: String,
    val id: Int,
    val type: String,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val rating: Double,
    val year: String?,
    /** Season number for series/anime; null for movies. */
    val season: Int?,
    /** Episode number for series/anime; null for movies. */
    val episode: Int?,
    /** Current playback position in milliseconds. */
    val positionMs: Long,
    /** Total duration in milliseconds, or 0 if unknown/live. */
    val durationMs: Long,
    /** Timestamp when this item was last watched (epoch milliseconds). */
    val lastWatchedAt: Long,
) {
    companion object {
        fun key(type: String, id: Int, season: Int? = null, episode: Int? = null): String =
            if (season != null && episode != null) "$type:$id:$season:$episode" else "$type:$id"
    }
}
