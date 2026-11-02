package dev.dogetime.domain

/**
 * Domain model representing a watched movie or episode.
 */
data class WatchHistoryItem(
    val mediaItem: MediaItem,
    val season: Int?,
    val episode: Int?,
    val positionMs: Long,
    val durationMs: Long,
    val lastWatchedAt: Long,
) {
    /** Watch progress as a fraction between 0.0 and 1.0 (if duration is known). */
    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
