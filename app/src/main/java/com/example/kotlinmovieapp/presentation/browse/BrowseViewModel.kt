package com.example.kotlinmovieapp.presentation.browse
import androidx.lifecycle.ViewModel
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class BrowseViewModel @Inject constructor(
   private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BrowseState())
    var state = _state

    init {
//        getMovies()
    }

//    fun getMovies() {
//        getMoviesUseCase.getMovies(state.value.catalog).onEach {movies ->
//            _state.value = BrowseState(movies = movies, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
//        }.launchIn(viewModelScope)
//    }
}