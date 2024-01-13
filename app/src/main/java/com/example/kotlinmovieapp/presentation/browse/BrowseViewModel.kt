package com.example.kotlinmovieapp.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.use_case.anime4up.Anime4upUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import com.example.kotlinmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase, private val anime4up: Anime4upUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BrowseState())
    var state = _state.asStateFlow()

    init {
        getMovies(
            state.value.type.value,
            state.value.catalog.value,
            _state.value.page,
            genre = null
        )
    }

    fun getMovies(type: String, catalog: String?, page: Int, genre: Genre?) {
        when (type) {
            "movie" -> getMoviesUseCase.getMovies(catalog ?: "popular", page).onEach {
                if (page == 1) {
                    _state.value = _state.value.copy(movies = it)
                } else if (page > 1) {
//                    val movies = _state.value.movies.plus(it)
                    when (it) {
                        is Resource.Success<List<MovieHome>> -> {
                            val data = it.data ?: emptyList()
                            val currentMovies = _state.value.movies?.data ?: emptyList()
                            val combinedMovies = (currentMovies + data)
                            _state.value = _state.value.copy(movies = Resource.Success(combinedMovies))
                        }
                        else -> _state.value = _state.value
                    }
//                    _state.value = _state.value.copy(movies = movies, page = page)
                }
            }.launchIn(viewModelScope)

            "tv" -> getMoviesUseCase.getShows(catalog ?: "popular", page).onEach {
                if (page == 1) {
                    _state.value = _state.value.copy(movies = it)
                } else if (page > 1) {
                    when (it) {
                        is Resource.Success<List<MovieHome>> -> {
                            val data = it.data ?: emptyList()
                            val currentMovies = _state.value.movies?.data ?: emptyList()
                            val combinedMovies = (currentMovies + data)
                            _state.value = _state.value.copy(movies = Resource.Success(combinedMovies))
                        }
                        else -> _state.value = _state.value
                    }
                }
            }.launchIn(viewModelScope)

//            "anime" -> anime4up.getAnime(page, genre?.name, catalog).onEach {
//                if (page == 1) {
//                    _state.value = _state.value.copy(movies = it)
//                } else if (page > 1 && genre == null) {
//                    val movies = _state.value.movies.plus(it)
//                    _state.value = _state.value.copy(movies = movies, page = page)
//                } else {
//                    Log.e("ELSE", "")
//                }
//            }.launchIn(viewModelScope)
        }
    }

    fun updateType(type: Type, catalog: Item) {
        _state.value = _state.value.copy(type = type, catalog = catalog, genre = null)
    }

    fun updateCatalog(catalog: Item) {
        _state.value = _state.value.copy(catalog = catalog)
    }

    fun updateGenre(genre: Genre) {
        _state.value = _state.value.copy(genre = genre)
    }

}