package com.example.kotlinmovieapp.presentation.home


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import com.example.kotlinmovieapp.domain.use_case.okanime.OKanimeUseCase
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val getMoviesUseCase: GetMoviesUseCase,
//    private val Animeiat: AnimeiatUseCase,
    private val watchList: WatchListUseCase,
    private val okanime: OKanimeUseCase
): ViewModel() {
    private val _state = mutableStateOf(MovieListState())
    val state : State<MovieListState> = _state

    init {
        getAll()


    }

    private fun getAll() {
        getMoviesUseCase.getTrending().onEach {
            _state.value = MovieListState(movies = it, trending = it, shows = state.value.shows, anime = state.value.anime, watchList = state.value.watchList )
        }.launchIn(viewModelScope)

        getMoviesUseCase.getTrendingShows().onEach {
                moviesDTO ->  _state.value = MovieListState(movies = state.value.movies, trending = state.value.trending, shows = moviesDTO, anime = state.value.anime, watchList = state.value.watchList)
        }.launchIn(viewModelScope)

//        Animeiat.getLatestEpisodes().onEach {
//            _state.value = MovieListState(movies = state.value.movies, trending = state.value.trending, shows = state.value.shows, anime = it, watchList = state.value.watchList)
//        }.launchIn(viewModelScope)
        getLatestEpisodes()
    }

    fun getWatchlist() {
        watchList.getList("Watching").onEach {list ->
            _state.value = MovieListState(movies = state.value.movies, trending = state.value.trending, shows = state.value.shows, anime = state.value.anime ,
                watchList = list.map { MovieHome(
                    id = it.id,
                    title = it.title,
                    poster = it.poster,
                    type = it.type,
                    slug = it.slug
                    ) }
                )

        }.launchIn(viewModelScope)
    }

    fun getLatestEpisodes() {
        okanime.getLatestEpisodes(1).onEach {
            _state.value = MovieListState(movies = state.value.movies, trending = state.value.trending, shows = state.value.shows, anime = it, watchList = state.value.watchList)
            Log.e("ViewModel", it.toString())
        }.launchIn(viewModelScope)
    }


}


