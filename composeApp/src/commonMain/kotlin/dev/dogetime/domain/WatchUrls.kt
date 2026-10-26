package dev.dogetime.domain

/** What to watch: a movie, or one episode of a TV show. Ids are TMDB ids. */
sealed interface WatchTarget {
    data class Movie(val id: Int) : WatchTarget
    data class Episode(val showId: Int, val season: Int, val episode: Int) : WatchTarget
}

/**
 * Embed sites that can play a [WatchTarget]. Each one's page is loaded hidden, and the HLS stream
 * it requests is played in the app's player.
 */
enum class VideoSource(val label: String, private val baseUrl: String, private val query: String = "") {
    VidSrc("VidSrc", "https://vidsrc.sh/embed"),
    // These two start their player themselves when asked to in the URL.
    VidLink("VidLink", "https://vidlink.pro", "?autoplay=true"),
    VidCore("VidCore", "https://vidcore.io", "?autoPlay=true");

    /** Player URL for [target]. */
    fun url(target: WatchTarget): String = when (target) {
        is WatchTarget.Movie -> "$baseUrl/movie/${target.id}$query"
        is WatchTarget.Episode -> "$baseUrl/tv/${target.showId}/${target.season}/${target.episode}$query"
    }
}
