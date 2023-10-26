package com.example.kotlinmovieapp.presentation.home


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val getMoviesUseCase: GetMoviesUseCase
): ViewModel() {
    private val _state = mutableStateOf(MovieListState())
    val state : State<MovieListState> = _state

    init {
        getTrending()
    }

    private fun getTrending() {
        getMoviesUseCase.getTrending().onEach {
            moviesDTO -> _state.value = MovieListState(loading = false, movies = moviesDTO)
        }.launchIn(viewModelScope)

    }
}


