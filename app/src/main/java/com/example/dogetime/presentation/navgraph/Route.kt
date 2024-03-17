package com.example.dogetime.presentation.navgraph

sealed class Route (
    val route: String

) {
    object Home : Route("home")
    object Search : Route("search")

    object Browse : Route("browse")

    object WatchList: Route("watchlist")

    object Account : Route("account")

    object WebView: Route("web-view/{url}")
    object MovieDetails: Route("movie/{id}")

    object ShowDetails: Route("show/{id}")
    object ShowSeasons: Route("show/seasons/{id}")

    object AnimeDetails: Route("anime/{slug}")

    object AnimeEpisodes: Route("anime/episodes/{slug}")

    object Mediaplayer: Route("media-player")


}
