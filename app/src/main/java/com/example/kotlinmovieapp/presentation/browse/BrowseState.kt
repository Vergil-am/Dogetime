package com.example.kotlinmovieapp.presentation.browse

import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO

data class BrowseState(
    var type : Type = Types[0],
    var catalog : Item = Types[0].catalog[0],
    val genre: String = "all",
    val movies: MoviesDTO? = null
)
