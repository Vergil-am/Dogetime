package dev.dogetime.data.local

import androidx.room.Entity

/** A favorited movie or TV show. Keeps the card data so favorites show without a network call. */
@Entity(tableName = "favorites", primaryKeys = ["id", "type"])
data class FavoriteEntity(
    val id: Int,
    val type: String,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val rating: Double,
    val year: String?,
    val addedAt: Long,
)
