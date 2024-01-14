package com.example.kotlinmovieapp.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.use_case.anime4up.Anime4upUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import com.example.kotlinmovieapp.util.Resource
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
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        getMovies()
        getShows()
        getLatestEpisodes()

    }

    private fun getMovies() {
        getMoviesUseCase.getTrending().onEach {
            when (it) {
                is Resource.Loading -> _state.value =
                    _state.value.copy(movies = MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(movies = MovieState(error = it.message, isLoading = false))

                is Resource.Success -> _state.value =
                    _state.value.copy(movies = MovieState(isLoading = false, data = it.data))
            }
        }.launchIn(viewModelScope)
    }

    private fun getShows() {
        getMoviesUseCase.getTrendingShows().onEach {
            when (it) {
                is Resource.Loading -> _state.value =
                    _state.value.copy(shows = MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(shows = MovieState(error = it.message, isLoading = false))

                is Resource.Success -> _state.value =
                    _state.value.copy(shows = MovieState(isLoading = false, data = it.data))
            }

        }.launchIn(viewModelScope)
    }

    private fun getLatestEpisodes() {
        anime4up.getLatestEpisodes().onEach {
            when (it) {
                is Resource.Loading -> _state.value =
                    _state.value.copy(anime = MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(anime = MovieState(error = it.message, isLoading = false))

                is Resource.Success -> _state.value =
                    _state.value.copy(anime = MovieState(isLoading = false, data = it.data))
            }

        }.launchIn(viewModelScope)
    }

    fun getWatchlist() {
        watchList.getList("watching").onEach { list ->
            _state.value = _state.value.copy(watchList = list.map {
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


