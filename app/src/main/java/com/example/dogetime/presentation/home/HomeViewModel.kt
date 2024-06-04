package com.example.dogetime.presentation.home


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.use_case.anime4up.Anime4upUseCase
import com.example.dogetime.domain.use_case.animecat.AnimeCatUseCase
import com.example.dogetime.domain.use_case.goganime.GogoAnimeUseCase
import com.example.dogetime.domain.use_case.history.HistoryUseCase
import com.example.dogetime.domain.use_case.movies.get_movies.GetMoviesUseCase
import com.example.dogetime.domain.use_case.mycima.MyCimaUseCase
import com.example.dogetime.domain.use_case.watchlist.WatchListUseCase
import com.example.dogetime.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val watchList: WatchListUseCase,
    private val anime4up: Anime4upUseCase,
    private val gogoanime: GogoAnimeUseCase,
    private val animeCat: AnimeCatUseCase,
    private val mycima: MyCimaUseCase,
    private val history: HistoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        getMovies()
        getShows()
        getLatestEpisodes()
        goganimeLatestEpisodes()
        animeCatLatestEpisodes()
        getMyCimaLatest()
        getHistory()
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
                    _state.value.copy(animeAR = MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(animeAR = MovieState(error = it.message, isLoading = false))

                is Resource.Success -> _state.value =
                    _state.value.copy(animeAR = MovieState(isLoading = false, data = it.data))
            }

        }.launchIn(viewModelScope)
    }

    private fun goganimeLatestEpisodes() {
        gogoanime.getLatestEpisodes().onEach {
            when (it) {
                is Resource.Loading -> _state.value =
                    _state.value.copy(animeEN = MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(animeEN = MovieState(error = it.message, isLoading = false))

                is Resource.Success -> _state.value =
                    _state.value.copy(animeEN = MovieState(isLoading = false, data = it.data))
            }

        }.launchIn(viewModelScope)
    }


    private fun animeCatLatestEpisodes() {
        animeCat.getLatestEpisodes().onEach {
            when (it) {
                is Resource.Loading -> _state.value =
                    _state.value.copy(animeFR = MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(animeFR = MovieState(error = it.message, isLoading = false))

                is Resource.Success -> _state.value =
                    _state.value.copy(animeFR = MovieState(isLoading = false, data = it.data))
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

    private fun getMyCimaLatest() {
        viewModelScope.launch {
            mycima.getLatest().onEach {

            }.launchIn(viewModelScope)
        }
        mycima.getLatestMovies().onEach {

            when (it) {
                is Resource.Loading -> _state.value =
                    _state.value.copy(myCimaMovies =  MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(
                        myCimaMovies = MovieState(
                            error = it.message,
                            isLoading = false
                        )
                    )

                is Resource.Success -> _state.value =
                    _state.value.copy(
                        myCimaMovies = MovieState(
                            isLoading = false,
                            data = it.data
                        )
                    )
            }
        }.launchIn(viewModelScope)
        mycima.getLatestEpisodes().onEach {

            when (it) {
                is Resource.Loading -> _state.value =
                    _state.value.copy(myCimaShows = MovieState(isLoading = true))

                is Resource.Error -> _state.value =
                    _state.value.copy(
                        myCimaShows = MovieState(
                            error = it.message,
                            isLoading = false
                        )
                    )

                is Resource.Success -> _state.value =
                    _state.value.copy(
                        myCimaShows = MovieState(
                            isLoading = false,
                            data = it.data
                        )
                    )
            }
        }.launchIn(viewModelScope)
    }

    private fun getHistory() {
        history.getHistory().onEach {
            Log.e("History", it.toString())
        }.launchIn(viewModelScope)
    }
}



