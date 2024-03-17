package com.example.dogetime.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.use_case.anime4up.Anime4upUseCase
import com.example.dogetime.domain.use_case.movies.get_movies.GetMoviesUseCase
import com.example.dogetime.util.Resource
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
            state.value.type.value, state.value.catalog.value, _state.value.page, genre = null
        )
    }

    fun getMovies(type: String, catalog: String?, page: Int, genre: Genre?) {
        when (type) {
            "movie" -> getMoviesUseCase.getMovies(catalog ?: "popular", page).onEach {
                if (page == 1) {
                    when (it) {
                        is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                        is Resource.Success -> _state.value =
                            _state.value.copy(movies = it.data ?: emptyList(), isLoading = false)

                        is Resource.Error -> {
//                            TODO()
                        }
                    }
                } else if (page > 1) {
                    when (it) {
                        is Resource.Success<List<MovieHome>> -> {
                            val movies = _state.value.movies.plus(it.data ?: emptyList())
                            _state.value = _state.value.copy(
                                movies = movies, page = page
                            )
                        }

                        else -> {}
                    }
                }
            }.launchIn(viewModelScope)

            "tv" -> getMoviesUseCase.getShows(catalog ?: "popular", page).onEach {
                if (page == 1) {
                    when (it) {
                        is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                        is Resource.Success -> _state.value =
                            _state.value.copy(movies = it.data ?: emptyList(), isLoading = false)

                        is Resource.Error -> {
//                            TODO()
                        }
                    }
                } else if (page > 1) {
                    when (it) {
                        is Resource.Success<List<MovieHome>> -> {
                            val movies = _state.value.movies.plus(it.data ?: emptyList())
                            _state.value = _state.value.copy(
                                movies = movies, page = page
                            )
                        }

                        else -> {}
                    }
                }
            }.launchIn(viewModelScope)

            "anime" -> anime4up.getAnime(page, genre?.name, catalog).onEach {
                if (page == 1) {
                    when (it) {
                        is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                        is Resource.Success -> _state.value =
                            _state.value.copy(movies = it.data ?: emptyList(), isLoading = false)

                        is Resource.Error -> {
//                            TODO()
                        }
                    }
                } else if (page > 1 && genre == null) {
                    when (it) {
                        is Resource.Success<List<MovieHome>> -> {
                            val movies = _state.value.movies.plus(it.data ?: emptyList())
                            _state.value = _state.value.copy(
                                movies = movies, page = page
                            )
                        }

                        else -> {}
                    }
                }
            }.launchIn(viewModelScope)
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