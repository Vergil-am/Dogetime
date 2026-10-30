package dev.dogetime.data

import dev.dogetime.domain.BrowseCategory
import dev.dogetime.domain.CastMember
import dev.dogetime.domain.Episode
import dev.dogetime.domain.MediaDetails
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.Page
import dev.dogetime.domain.Season

/** Single entry point for the ViewModels. Turns TMDB and Kitsu DTOs into domain models. */
class MediaRepository(
    private val api: TmdbApi,
    private val kitsuApi: KitsuApi? = null,
) {

    suspend fun browse(type: MediaType, category: BrowseCategory, page: Int): Page<MediaItem> =
        when (type) {
            MediaType.Anime -> browseAnime(category, page)
            MediaType.Movie, MediaType.Tv -> api.list(type, category.path, page).toPage { it.toMediaItem(type) }
        }

    suspend fun trending(type: MediaType): List<MediaItem> =
        when (type) {
            MediaType.Anime -> trendingAnime()
            MediaType.Movie, MediaType.Tv -> api.trending(type, page = 1).results.map { it.toMediaItem(type) }
        }

    suspend fun details(type: MediaType, id: Int): MediaDetails =
        when (type) {
            MediaType.Anime -> detailsAnime(id)
            MediaType.Movie, MediaType.Tv -> api.details(type, id).toDomain(type)
        }

    suspend fun episodes(id: Int, seasonNumber: Int, type: MediaType = MediaType.Tv): List<Episode> =
        when (type) {
            MediaType.Anime -> episodesAnime(id)
            MediaType.Movie, MediaType.Tv -> api.season(id, seasonNumber).episodes.map { it.toDomain() }
        }

    /** Multi-search also returns people; only movies and TV shows are kept. */
    suspend fun search(query: String, page: Int): Page<MediaItem> =
        api.search(query, page).toPage { result ->
            when (result.mediaType) {
                "movie" -> result.toMediaItem(MediaType.Movie)
                "tv" -> result.toMediaItem(MediaType.Tv)
                else -> null
            }
        }

    suspend fun searchAnime(query: String, page: Int = 1, pageSize: Int = 20): Page<MediaItem> {
        val kitsu = requireNotNull(kitsuApi) { "KitsuApi is required for anime." }
        val offset = (page - 1) * pageSize
        val res = kitsu.search(query, limit = pageSize, offset = offset)
        val items = res.data.mapNotNull { it.toMediaItem() }
        val total = res.meta?.count ?: items.size
        val totalPages = (total + pageSize - 1) / pageSize
        return Page(items = items, page = page, totalPages = maxOf(1, totalPages))
    }

    private suspend fun browseAnime(category: BrowseCategory, page: Int, pageSize: Int = 20): Page<MediaItem> {
        val kitsu = requireNotNull(kitsuApi) { "KitsuApi is required for anime." }
        val offset = (page - 1) * pageSize
        val res = when (category.path) {
            "top_rated" -> kitsu.topRated(limit = pageSize, offset = offset)
            "current" -> kitsu.currentlyAiring(limit = pageSize, offset = offset)
            "upcoming" -> kitsu.upcoming(limit = pageSize, offset = offset)
            else -> kitsu.popular(limit = pageSize, offset = offset)
        }
        val items = res.data.mapNotNull { it.toMediaItem() }
        val total = res.meta?.count ?: items.size
        val totalPages = (total + pageSize - 1) / pageSize
        return Page(items = items, page = page, totalPages = maxOf(1, totalPages))
    }

    private suspend fun trendingAnime(limit: Int = 20): List<MediaItem> {
        val kitsu = requireNotNull(kitsuApi) { "KitsuApi is required for anime." }
        return kitsu.trending(limit).data.mapNotNull { it.toMediaItem() }
    }

    private suspend fun detailsAnime(kitsuId: Int): MediaDetails {
        val kitsu = requireNotNull(kitsuApi) { "KitsuApi is required for anime." }
        val res = kitsu.details(kitsuId).data
        val attrs = res.attributes

        val genres = try {
            val genreRes = kitsu.genres(kitsuId).data.map { it.attributes.name }
            if (genreRes.isNotEmpty()) genreRes else kitsu.categories(kitsuId).data.map { it.attributes.title }.take(6)
        } catch (_: Exception) {
            emptyList()
        }

        val title = attrs.canonicalTitle ?: attrs.titles.values.firstOrNull { !it.isNullOrBlank() } ?: "Anime"
        val poster = attrs.posterImage?.large ?: attrs.posterImage?.original ?: attrs.posterImage?.medium
        val backdrop = attrs.coverImage?.large ?: attrs.coverImage?.original ?: poster

        val rawRating = attrs.averageRating?.toDoubleOrNull() ?: 0.0
        val rating10 = if (rawRating > 0) rawRating / 10.0 else 0.0

        val isMovie = attrs.subtype.equals("movie", ignoreCase = true) || (attrs.episodeCount == 1 && (attrs.episodeLength ?: 0) > 40)
        val episodeCount = attrs.episodeCount?.takeIf { it > 0 }
        val seasons = if (isMovie) {
            emptyList()
        } else {
            listOf(
                Season(
                    number = 1,
                    name = "Episodes",
                    episodeCount = episodeCount ?: 0,
                    airDate = attrs.startDate,
                )
            )
        }

        val item = MediaItem(
            id = kitsuId,
            type = MediaType.Anime,
            title = title,
            overview = attrs.synopsis ?: attrs.description.orEmpty(),
            posterUrl = poster,
            backdropUrl = backdrop,
            rating = rating10,
            year = attrs.startDate?.take(4),
        )

        val runtime = if (isMovie) {
            attrs.episodeLength?.let { "$it min" }
        } else {
            attrs.episodeLength?.let { "$it min per episode" }
        }

        return MediaDetails(
            item = item,
            tagline = attrs.ageRatingGuide,
            status = attrs.status?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            genres = genres,
            runtime = runtime,
            voteCount = attrs.userCount ?: 0,
            seasonCount = if (isMovie) null else 1,
            episodeCount = if (isMovie) null else episodeCount,
            seasons = seasons,
            cast = emptyList(),
            recommendations = emptyList(),
            similar = emptyList(),
            imdbId = null,
            kitsuId = kitsuId,
            logoUrl = null,
        )
    }

    private suspend fun episodesAnime(kitsuId: Int): List<Episode> {
        val kitsu = requireNotNull(kitsuApi) { "KitsuApi is required for anime." }
        val list = mutableListOf<Episode>()
        var offset = 0
        val pageSize = 20
        while (true) {
            val batch = try {
                kitsu.episodes(kitsuId, limit = pageSize, offset = offset).data
            } catch (e: Exception) {
                println("Failed to fetch anime episodes at offset $offset: ${e.message}")
                emptyList()
            }
            if (batch.isEmpty()) break
            batch.forEach { epRes ->
                val epAttrs = epRes.attributes
                val epNum = epAttrs.number ?: (list.size + 1)
                val epTitle = epAttrs.canonicalTitle ?: "Episode $epNum"
                list.add(
                    Episode(
                        number = epNum,
                        seasonNumber = epAttrs.seasonNumber ?: 1,
                        name = epTitle,
                        overview = epAttrs.synopsis ?: epAttrs.description.orEmpty(),
                        airDate = epAttrs.airdate,
                        stillUrl = epAttrs.thumbnail?.original ?: epAttrs.thumbnail?.medium,
                        rating = 0.0,
                        runtime = epAttrs.length?.let { "$it min" },
                    )
                )
            }
            if (batch.size < pageSize) break
            offset += pageSize
            // Cap at 500 episodes safety net
            if (offset >= 500) break
        }
        return list
    }
}

private fun KitsuAnimeResource.toMediaItem(): MediaItem? {
    val kitsuId = id.toIntOrNull() ?: return null
    val attrs = attributes
    val title = attrs.canonicalTitle ?: attrs.titles.values.firstOrNull { !it.isNullOrBlank() } ?: return null
    val poster = attrs.posterImage?.large ?: attrs.posterImage?.original ?: attrs.posterImage?.medium
    val backdrop = attrs.coverImage?.large ?: attrs.coverImage?.original ?: poster
    val rawRating = attrs.averageRating?.toDoubleOrNull() ?: 0.0
    val rating10 = if (rawRating > 0) rawRating / 10.0 else 0.0
    return MediaItem(
        id = kitsuId,
        type = MediaType.Anime,
        title = title,
        overview = attrs.synopsis ?: attrs.description.orEmpty(),
        posterUrl = poster,
        backdropUrl = backdrop,
        rating = rating10,
        year = attrs.startDate?.take(4),
    )
}

private const val POSTER_SIZE = "w342"
private const val BACKDROP_SIZE = "w780"
private const val PROFILE_SIZE = "w185"
private const val STILL_SIZE = "w300"
private const val LOGO_SIZE = "w500"

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
    val logoUrl = images?.logos
        ?.filter { it.language == "en" || it.language == null }
        ?.maxByOrNull { it.width }
        ?.filePath
        ?.toImageUrl(LOGO_SIZE)
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
        logoUrl = logoUrl,
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
