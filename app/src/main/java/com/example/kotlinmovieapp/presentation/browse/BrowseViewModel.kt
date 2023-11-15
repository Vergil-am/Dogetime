package com.example.kotlinmovieapp.presentation.browse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
   private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BrowseState())
    var state = _state

    init {
        getMovies(state.value.type.value ,state.value.catalog.value, 1)
    }
    fun getMovies(type: String , catalog: String , page: Int) {
        if (type == "movies") {
            getMoviesUseCase.getMovies(catalog, page).onEach {movies ->
                if (page == 1) {
                    _state.value = BrowseState(movies = movies, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
                } else if (page > 1) {
                    _state.value = BrowseState(movies = state.value.movies.plus(movies), type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)

                }
            }.launchIn(viewModelScope)

        } else if (type == "tv") {
            getMoviesUseCase.getShows(catalog, page).onEach { shows ->
                if (page == 1) {
                    _state.value = BrowseState(movies = shows, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
                } else if (page > 1) {
                    _state.value = BrowseState(movies = state.value.movies.plus(shows), type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)

                }
            }.launchIn(viewModelScope)
        }
    }

}