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
        getAll()

    }

    private fun getAll() {
        getMoviesUseCase.getTrending().onEach {
            moviesDTO -> _state.value = MovieListState(movies = moviesDTO, trending = moviesDTO, shows = state.value.shows )
        }.launchIn(viewModelScope)
//        getMoviesUseCase.getPopular().onEach {
//                moviesDTO ->  _state.value = MovieListState(movies = moviesDTO, trending = state.value.trending, shows = state.value.shows )
//        }.launchIn(viewModelScope)
        getMoviesUseCase.getShows().onEach {
                moviesDTO ->  _state.value = MovieListState(movies = state.value.movies, trending = state.value.trending, shows = moviesDTO)
        }.launchIn(viewModelScope)
    }


}


