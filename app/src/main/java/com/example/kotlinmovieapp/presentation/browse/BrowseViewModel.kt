package com.example.kotlinmovieapp.presentation.browse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.anime4up.Anime4upUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
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
        getMovies(state.value.type.value, state.value.catalog.value, _state.value.page)
    }

    fun getMovies(type: String, catalog: String, page: Int) {
        Log.e("page", page.toString())
        when (type) {
            "movie" -> getMoviesUseCase.getMovies(catalog, page).onEach {
                if (page == 1) {
                    _state.value = _state.value.copy(movies = it)
                } else if (page > 1) {
                    val movies  = _state.value.movies.plus(it)
                    _state.value = _state.value.copy(movies = movies)
                }
            }.launchIn(viewModelScope)
            "tv" -> getMoviesUseCase.getShows(catalog, page).onEach {
                if (page == 1) {
                    _state.value = _state.value.copy(movies = it)
                } else if (page > 1) {
                    val movies  = _state.value.movies.plus(it)
                    _state.value = _state.value.copy(movies = movies)
                }
            }.launchIn(viewModelScope)

            "anime" -> anime4up.getAnime(page).onEach {
                if (page == 1) {
                    _state.value = _state.value.copy(movies = it)
                } else if (page > 1) {
                    val movies  = _state.value.movies.plus(it)
                    _state.value = _state.value.copy(movies = movies)
                }
            }.launchIn(viewModelScope)
        }
    }

//    fun updateState(type: Type?, catalog:Item?, genre: Genre?) {
//        v
//    }

}