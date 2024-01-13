package com.example.kotlinmovieapp.presentation.browse
import com.example.kotlinmovieapp.data.remote.dto.GenresDTO
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.util.Resource

data class BrowseState(
    val isLoading : Boolean = false,
    var type: Type = Types[0],
    var catalog: Item = Types[0].catalog[0],
    var genres: GenresDTO? = null,
    var genre: Genre? = null,
//    val movies: List<MovieHome> = mutableListOf(),
    val movies: Resource<List<MovieHome>>?  = Resource.Success(data = mutableListOf()),
    var page: Int = 1
) {
}
