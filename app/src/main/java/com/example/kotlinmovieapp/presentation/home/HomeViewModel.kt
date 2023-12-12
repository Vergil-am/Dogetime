package com.example.kotlinmovieapp.presentation.home


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.animeiat.AnimeiatUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val getMoviesUseCase: GetMoviesUseCase,
    private val Animeiat: AnimeiatUseCase
): ViewModel() {
    private val _state = mutableStateOf(MovieListState())
    val state : State<MovieListState> = _state

    init {
        getAll()

    }

    private fun getAll() {
        getMoviesUseCase.getTrending().onEach {
            _state.value = MovieListState(movies = it, trending = it, shows = state.value.shows, anime = state.value.anime )
        }.launchIn(viewModelScope)

        getMoviesUseCase.getTrendingShows().onEach {
                moviesDTO ->  _state.value = MovieListState(movies = state.value.movies, trending = state.value.trending, shows = moviesDTO, anime = state.value.anime)
        }.launchIn(viewModelScope)

        Animeiat.getLatestEpisodes().onEach {
            _state.value = MovieListState(movies = state.value.movies, trending = state.value.trending, shows = state.value.shows, anime = it)
        }.launchIn(viewModelScope)
    }


}


