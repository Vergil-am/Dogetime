package com.example.dogetime.presentation.browse
import com.example.dogetime.data.remote.dto.GenresDTO
import com.example.dogetime.domain.model.MovieHome

data class BrowseState(
    val isLoading : Boolean = false,
    var type: Type = Types[0],
    var catalog: Item = Types[0].catalog[0],
    var genres: GenresDTO? = null,
    var genre: Genre? = null,
    val movies: List<MovieHome> = mutableListOf(),
    var page: Int = 1
) {
}
