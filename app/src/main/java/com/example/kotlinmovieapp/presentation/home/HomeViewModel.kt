package com.example.kotlinmovieapp.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.use_case.anime4up.Anime4upUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val watchList: WatchListUseCase,
    private val anime4up: Anime4upUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()

    init {
        getMovies()
        getShows()
        getLatestEpisodes()

    }

    private fun getMovies() {
        getMoviesUseCase.getTrending().onEach {
            _state.value = _state.value.copy(movies = it)
        }.launchIn(viewModelScope)
    }

    private fun getShows() {
        getMoviesUseCase.getTrendingShows().onEach {
            _state.value = _state.value.copy(shows = it)
        }.launchIn(viewModelScope)
    }

    private fun getLatestEpisodes() {
        anime4up.getLatestEpisodes().onEach {
            _state.value = _state.value.copy(anime = it)
        }.launchIn(viewModelScope)
    }

    fun getWatchlist() {
        watchList.getList("Watching").onEach { list ->
            _state.value = _state.value.copy(
                watchList = list.map {
                    MovieHome(
                        id = it.id,
                        title = it.title,
                        poster = it.poster,
                        type = it.type,
                    )
                })

        }.launchIn(viewModelScope)
    }

}


