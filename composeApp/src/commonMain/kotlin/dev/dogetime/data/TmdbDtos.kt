package dev.dogetime.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Raw TMDB responses. Only the fields the app uses are declared;
// unknown fields are ignored by the JSON configuration in TmdbApi.kt.

@Serializable
data class TmdbPage(
    val page: Int = 1,
    val results: List<TmdbMedia> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 1,
)

/** One entry in a list, trending or search response. Movies use [title], TV uses [name]. */
@Serializable
data class TmdbMedia(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("media_type") val mediaType: String? = null,
)

@Serializable
data class TmdbDetails(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val overview: String? = null,
    val tagline: String? = null,
    val status: String? = null,
    val genres: List<TmdbGenre> = emptyList(),
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    val runtime: Int? = null,
    @SerialName("episode_run_time") val episodeRunTime: List<Int> = emptyList(),
    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null,
    val seasons: List<TmdbSeason> = emptyList(),
    val credits: TmdbCredits? = null,
    val recommendations: TmdbPage? = null,
    val similar: TmdbPage? = null,
    /** Only movies have this at the top level; TV shows have it in [externalIds]. */
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("external_ids") val externalIds: TmdbExternalIds? = null,
    val images: TmdbImages? = null,
)

@Serializable
data class TmdbExternalIds(
    @SerialName("imdb_id") val imdbId: String? = null,
)

@Serializable
data class TmdbGenre(
    val id: Int,
    val name: String,
)

@Serializable
data class TmdbCredits(
    val cast: List<TmdbCast> = emptyList(),
)

@Serializable
data class TmdbCast(
    val name: String,
    val character: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
)

/** A season entry inside a TV show's details response. */
@Serializable
data class TmdbSeason(
    val id: Int,
    val name: String? = null,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("episode_count") val episodeCount: Int = 0,
    @SerialName("air_date") val airDate: String? = null,
)

/** Response of `tv/{id}/season/{n}`. */
@Serializable
data class TmdbSeasonDetails(
    val episodes: List<TmdbEpisode> = emptyList(),
)

@Serializable
data class TmdbEpisode(
    val id: Int,
    val name: String? = null,
    val overview: String? = null,
    @SerialName("episode_number") val episodeNumber: Int = 0,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("air_date") val airDate: String? = null,
    @SerialName("still_path") val stillPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    val runtime: Int? = null,
)

@Serializable
data class TmdbImages(
    val logos: List<TmdbImageEntry> = emptyList(),
)

@Serializable
data class TmdbImageEntry(
    @SerialName("file_path") val filePath: String? = null,
    @SerialName("iso_639_1") val language: String? = null,
    val width: Int = 0,
)
