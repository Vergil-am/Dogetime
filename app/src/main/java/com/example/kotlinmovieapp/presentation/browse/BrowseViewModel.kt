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
        getMovies(state.value.type.value ,state.value.catalog.value)
    }
    fun getMovies(type: String , catalog: String) {
        if (type == "movies") {
            getMoviesUseCase.getMovies(catalog).onEach {movies ->
                _state.value = BrowseState(movies = movies, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
            }.launchIn(viewModelScope)

        } else if (type == "tv") {
            getMoviesUseCase.getShows(catalog, 1).onEach { shows ->
                _state.value = BrowseState(movies = shows, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
            }.launchIn(viewModelScope)
        }
    }

}