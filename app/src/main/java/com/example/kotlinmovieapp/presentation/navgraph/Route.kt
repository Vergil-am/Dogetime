package com.example.kotlinmovieapp.presentation.navgraph

sealed class Route (
    val route: String

) {
    object Home : Route("home")
    object Search : Route("search")

    object Favorites : Route("favorites")

    object Account : Route("account")

    object MovieDetails: Route("movie/{id}")

    object ShowDetails: Route("show/{id}")

}
