package dev.dogetime.domain

/**
 * What to watch: a movie, or one episode of a TV show. Ids are TMDB ids; [imdbId] is the movie's
 * or show's IMDb id, when known, for sources that look titles up by it.
 */
sealed interface WatchTarget {
    val imdbId: String?

    data class Movie(val id: Int, override val imdbId: String? = null) : WatchTarget
    data class Episode(val showId: Int, val season: Int, val episode: Int, override val imdbId: String? = null) : WatchTarget
}

fun WatchTarget.withImdbId(imdbId: String?): WatchTarget = when (this) {
    is WatchTarget.Movie -> copy(imdbId = imdbId)
    is WatchTarget.Episode -> copy(imdbId = imdbId)
}

/**
 * Embed sites that can play a [WatchTarget]. Each one's page is loaded hidden, and the HLS stream
 * it requests is played in the app's player. Titles are looked up by IMDb id where the site
 * accepts it, and by TMDB id otherwise.
 */
enum class VideoSource(
    val label: String,
    private val baseUrl: String,
    private val query: String = "",
    private val imdbForEpisodes: Boolean = true,
) {
    VidSrc("VidSrc", "https://vidsrc.sh/embed"),
    // These two start their player themselves when asked to in the URL.
    // VidLink answers episode pages by IMDb id with a server error, so episodes use TMDB ids there.
    VidLink("VidLink", "https://vidlink.pro", "?autoplay=true", imdbForEpisodes = false),
    VidCore("VidCore", "https://vidcore.io", "?autoPlay=true");

    /** Player URL for [target]. */
    fun url(target: WatchTarget): String = when (target) {
        is WatchTarget.Movie -> "$baseUrl/movie/${target.imdbId ?: target.id}$query"
        is WatchTarget.Episode -> {
            val id = target.imdbId?.takeIf { imdbForEpisodes } ?: target.showId
            "$baseUrl/tv/$id/${target.season}/${target.episode}$query"
        }
    }
}
