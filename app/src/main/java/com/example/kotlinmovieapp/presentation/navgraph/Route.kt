package com.example.kotlinmovieapp.presentation.navgraph

sealed class Route (
    val route: String

) {
    object Home : Route("Home")
    object Search : Route("Search")

    object Favorites : Route("Favorites")

    object Account : Route("Account")

    object MovieDetails: Route("Movie/{id}")

    object ShowDetails: Route("Show/{id}")
}
