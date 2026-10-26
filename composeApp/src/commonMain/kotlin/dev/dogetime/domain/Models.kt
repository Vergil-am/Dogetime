package dev.dogetime.domain

enum class MediaType(val label: String) {
    Movie("Movies"),
    Tv("TV Shows"),
}

/** A movie or TV show as shown in lists, rows and search results. */
data class MediaItem(
    val id: Int,
    val type: MediaType,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val rating: Double,
    val year: String?,
)

data class CastMember(
    val name: String,
    val character: String,
    val photoUrl: String?,
)

/** Everything the details screen shows about one movie or TV show. */
data class MediaDetails(
    val item: MediaItem,
    val tagline: String?,
    val status: String?,
    val genres: List<String>,
    val runtime: String?,
    val voteCount: Int,
    val seasonCount: Int?,
    val episodeCount: Int?,
    val seasons: List<Season>,
    val cast: List<CastMember>,
    /** TMDB's picks based on what people who watched this also watched. */
    val recommendations: List<MediaItem>,
    /** Titles with matching genres and keywords, without those already in [recommendations]. */
    val similar: List<MediaItem>,
    /** The title's IMDb id ("tt…"), which torrent sources look titles up by. */
    val imdbId: String? = null,
)

/** A TV season with at least one episode. */
data class Season(
    val number: Int,
    val name: String,
    val episodeCount: Int,
    val airDate: String?,
)

data class Episode(
    val number: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String,
    val airDate: String?,
    val stillUrl: String?,
    val rating: Double,
    val runtime: String?,
)

data class Page<T>(
    val items: List<T>,
    val page: Int,
    val totalPages: Int,
)

/** A browse subcategory, e.g. "Top Rated" maps to the TMDB path `top_rated`. */
data class BrowseCategory(
    val label: String,
    val path: String,
)

val popularCategory = BrowseCategory("Popular", "popular")

fun browseCategories(type: MediaType): List<BrowseCategory> = when (type) {
    MediaType.Movie -> listOf(
        popularCategory,
        BrowseCategory("Top Rated", "top_rated"),
        BrowseCategory("Now Playing", "now_playing"),
        BrowseCategory("Upcoming", "upcoming"),
    )
    MediaType.Tv -> listOf(
        popularCategory,
        BrowseCategory("Top Rated", "top_rated"),
        BrowseCategory("On The Air", "on_the_air"),
        BrowseCategory("Airing Today", "airing_today"),
    )
}
