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
        getMovies(state.value.catalog)
    }
    fun getMovies(catalog: String) {
        getMoviesUseCase.getMovies(catalog).onEach {movies ->
            _state.value = BrowseState(movies = movies, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
        }.launchIn(viewModelScope)
    }
}