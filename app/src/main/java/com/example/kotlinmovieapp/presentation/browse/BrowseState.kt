package com.example.kotlinmovieapp.presentation.browse

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO

data class BrowseState(
    var type : String = "movies",
    val catalog : String = "popular",
    val genre: String = "all",
    val movies: MoviesDTO? = null
)
