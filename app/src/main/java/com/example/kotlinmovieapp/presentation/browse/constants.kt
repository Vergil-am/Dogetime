package com.example.kotlinmovieapp.presentation.browse

class Item(
    val title: String,
    val value: String,
)

class Type(
    val title: String,
    var value: String,
    val catalog: List<Item>
)

val MovieCatalog = listOf(
    Item("Popular", "popular"),
    Item("Top Rated", "top_rated"),
    Item("Now playing", "now_playing")
)
val ShowCatalog = listOf(
    Item("Popular", "popular"),
    Item("Top Rated", "top_rated"),
    Item("Airing today", "airing_today"),
    Item("On Air", "on_the_air")
)

val Types = listOf(
    Type("Movies", "movies", MovieCatalog),
    Type("Shows", "tv", ShowCatalog),
)