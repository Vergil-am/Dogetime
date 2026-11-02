package dev.dogetime.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    /**
     * Observes the most recently watched entry for each distinct media title (movie or series).
     */
    @Query("""
        SELECT * FROM watch_history w1
        WHERE w1.watchKey = (
            SELECT w2.watchKey FROM watch_history w2
            WHERE w2.id = w1.id AND w2.type = w1.type
            ORDER BY w2.lastWatchedAt DESC
            LIMIT 1
        )
        ORDER BY w1.lastWatchedAt DESC
    """)
    fun observeAllRecent(): Flow<List<WatchHistoryEntity>>

    /**
     * Observes all watched episodes/entries for a specific media item.
     */
    @Query("SELECT * FROM watch_history WHERE id = :id AND type = :type ORDER BY season ASC, episode ASC")
    fun observeByMedia(id: Int, type: String): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE watchKey = :watchKey LIMIT 1")
    fun observeByKey(watchKey: String): Flow<WatchHistoryEntity?>

    @Query("SELECT * FROM watch_history WHERE watchKey = :watchKey LIMIT 1")
    suspend fun getByKey(watchKey: String): WatchHistoryEntity?

    @Query("SELECT * FROM watch_history WHERE id = :id AND type = :type ORDER BY lastWatchedAt DESC LIMIT 1")
    suspend fun getLatestByMedia(id: Int, type: String): WatchHistoryEntity?

    @Upsert
    suspend fun upsert(entry: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE watchKey = :watchKey")
    suspend fun deleteByKey(watchKey: String)

    @Query("DELETE FROM watch_history WHERE id = :id AND type = :type")
    suspend fun deleteByMedia(id: Int, type: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearAll()
}
