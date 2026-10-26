package dev.dogetime.data

import dev.dogetime.domain.BrowseCategory
import dev.dogetime.domain.CastMember
import dev.dogetime.domain.Episode
import dev.dogetime.domain.MediaDetails
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.Page
import dev.dogetime.domain.Season

/** Single entry point for the ViewModels. Turns TMDB DTOs into domain models. */
class MediaRepository(private val api: TmdbApi) {

    suspend fun browse(type: MediaType, category: BrowseCategory, page: Int): Page<MediaItem> =
        api.list(type, category.path, page).toPage { it.toMediaItem(type) }

    suspend fun trending(type: MediaType): List<MediaItem> =
        api.trending(type, page = 1).results.map { it.toMediaItem(type) }

    suspend fun details(type: MediaType, id: Int): MediaDetails =
        api.details(type, id).toDomain(type)

    suspend fun episodes(tvId: Int, seasonNumber: Int): List<Episode> =
        api.season(tvId, seasonNumber).episodes.map { it.toDomain() }

    /** Multi-search also returns people; only movies and TV shows are kept. */
    suspend fun search(query: String, page: Int): Page<MediaItem> =
        api.search(query, page).toPage { result ->
            when (result.mediaType) {
                "movie" -> result.toMediaItem(MediaType.Movie)
                "tv" -> result.toMediaItem(MediaType.Tv)
                else -> null
            }
        }
}

private const val POSTER_SIZE = "w342"
private const val BACKDROP_SIZE = "w780"
private const val PROFILE_SIZE = "w185"
private const val STILL_SIZE = "w300"

private fun TmdbPage.toPage(transform: (TmdbMedia) -> MediaItem?): Page<MediaItem> =
    Page(
        items = results.mapNotNull(transform),
        page = page,
        totalPages = totalPages,
    )

private fun TmdbMedia.toMediaItem(type: MediaType): MediaItem = MediaItem(
    id = id,
    type = type,
    title = title ?: name ?: "Untitled",
    overview = overview.orEmpty(),
    posterUrl = posterPath.toImageUrl(POSTER_SIZE),
    backdropUrl = backdropPath.toImageUrl(BACKDROP_SIZE),
    rating = voteAverage,
    year = (releaseDate ?: firstAirDate).toYear(),
)

private fun TmdbDetails.toDomain(type: MediaType): MediaDetails {
    val item = MediaItem(
        id = id,
        type = type,
        title = title ?: name ?: "Untitled",
        overview = overview.orEmpty(),
        posterUrl = posterPath.toImageUrl(POSTER_SIZE),
        backdropUrl = backdropPath.toImageUrl(BACKDROP_SIZE),
        rating = voteAverage,
        year = (releaseDate ?: firstAirDate).toYear(),
    )
    val isTv = type == MediaType.Tv
    val recommended = recommendations.toRelatedItems(type)
    val recommendedIds = recommended.mapTo(HashSet()) { it.id }
    return MediaDetails(
        item = item,
        tagline = tagline?.takeIf { it.isNotBlank() },
        status = status,
        genres = genres.map { it.name },
        runtime = if (isTv) {
            episodeRunTime.firstOrNull()?.let { "$it min per episode" }
        } else {
            runtime?.let { "$it min" }
        },
        voteCount = voteCount,
        seasonCount = if (isTv) numberOfSeasons else null,
        episodeCount = if (isTv) numberOfEpisodes else null,
        // Skip seasons with no episodes; list "Specials" (season 0) after the numbered seasons.
        seasons = if (isTv) {
            seasons
                .filter { it.episodeCount > 0 }
                .sortedWith(compareBy({ it.seasonNumber == 0 }, { it.seasonNumber }))
                .map { it.toDomain() }
        } else {
            emptyList()
        },
        cast = credits?.cast.orEmpty().take(15).map { cast ->
            CastMember(
                name = cast.name,
                character = cast.character.orEmpty(),
                photoUrl = cast.profilePath.toImageUrl(PROFILE_SIZE),
            )
        },
        recommendations = recommended,
        similar = similar.toRelatedItems(type).filter { it.id !in recommendedIds },
        imdbId = (imdbId ?: externalIds?.imdbId)?.takeIf { it.isNotBlank() },
    )
}

/**
 * Recommended or similar titles. Only recommendations say whether each entry is a movie or a
 * TV show; otherwise they are the same type as the title they were found for.
 */
private fun TmdbPage?.toRelatedItems(type: MediaType): List<MediaItem> =
    this?.results.orEmpty().map { result ->
        val resultType = when (result.mediaType) {
            "movie" -> MediaType.Movie
            "tv" -> MediaType.Tv
            else -> type
        }
        result.toMediaItem(resultType)
    }

private fun TmdbSeason.toDomain(): Season = Season(
    number = seasonNumber,
    name = name ?: "Season $seasonNumber",
    episodeCount = episodeCount,
    airDate = airDate.toNonBlank(),
)

private fun TmdbEpisode.toDomain(): Episode = Episode(
    number = episodeNumber,
    seasonNumber = seasonNumber,
    name = name ?: "Episode $episodeNumber",
    overview = overview.orEmpty(),
    airDate = airDate.toNonBlank(),
    stillUrl = stillPath.toImageUrl(STILL_SIZE),
    rating = voteAverage,
    runtime = runtime?.let { "$it min" },
)

private fun String?.toImageUrl(size: String): String? =
    this?.let { "https://image.tmdb.org/t/p/$size$it" }

private fun String?.toYear(): String? =
    this?.take(4)?.takeIf { it.isNotBlank() }

private fun String?.toNonBlank(): String? =
    this?.takeIf { it.isNotBlank() }
