package com.example.kotlinmovieapp.presentation.browse
import com.example.kotlinmovieapp.data.remote.dto.GenresDTO
import com.example.kotlinmovieapp.domain.model.Genre
import com.example.kotlinmovieapp.domain.model.Movie

data class BrowseState(
    var type: Type = Types[0],
    var catalog: Item = Types[0].catalog[0],
    var genres: GenresDTO? = null,
    var genre: Genre? = null,
    val movies: List<Movie> = mutableListOf(),
    var page: Int = 1
)
