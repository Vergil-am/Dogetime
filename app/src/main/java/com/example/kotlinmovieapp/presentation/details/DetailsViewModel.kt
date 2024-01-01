package com.example.kotlinmovieapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.movies.get_movie.GetMovieUseCase
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia
import com.example.kotlinmovieapp.domain.use_case.anime4up.Anime4upUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase,
    private val watchList: WatchListUseCase,
    private val anime4up: Anime4upUseCase
): ViewModel()  {
    private val _state = MutableStateFlow(MovieState())
    var state : StateFlow<MovieState> = _state



    fun getMedia(type: String, id: String) {
        when (type) {
            "movie" -> getMovie(id.toInt())
            "show" -> getShow(id.toInt())
            "anime" -> getAnime(id)
        }
    }
    private fun getMovie(id: Int) {
            getMovieUseCase.getMovieDetails(id).onEach {
                _state.value = MovieState(
                    media = it,
                    isLoading = false,
                    watchList = state.value.watchList,
                    animeEpisodes = listOf()
                    )
            }.launchIn(viewModelScope)


    }
    private fun getShow(id: Int) {
            getMovieUseCase.getShow(id).onEach {
                _state.value = MovieState(
                    media = it,
                    isLoading = false,
                    season = state.value.season,
                    watchList = state.value.watchList,
                    animeEpisodes = listOf(),
                    animeEpisodeSources = state.value.animeEpisodeSources
                )
            }.launchIn(viewModelScope)
    }

    fun getSeason(id: Int, season: Int) {
        getMovieUseCase.getSeason(id, season).onEach {
            _state.value = MovieState(
                media = state.value.media,
                isLoading = false,
                season = it,
                watchList = state.value.watchList,
                animeEpisodes = listOf(),
                animeEpisodeSources = state.value.animeEpisodeSources
            )
        }.launchIn(viewModelScope)
    }


    // WatchList
    fun addToWatchList(media: WatchListMedia) {
        viewModelScope.launch {
            watchList.addToWatchList(media)
        }
    }

    fun getMediaFromWatchList(id: String) {
            watchList.getMediaById(id).onEach {
                _state.value =  MovieState(
                    media = state.value.media,
                    isLoading = false,
                    season = null,
                    watchList = it,
                    animeEpisodes = state.value.animeEpisodes,
                    animeEpisodeId =  state.value.animeEpisodeId,
                    animeEpisodeSources = state.value.animeEpisodeSources
                )
            }.launchIn(viewModelScope)

    }
    private fun getAnime(slug: String) {
            anime4up.getAnimeDetails(slug).onEach {
                _state.value = MovieState(
                    media = it.details,
                    isLoading = false,
                    season = null,
                    watchList = state.value.watchList,
                    animeEpisodes = it.episodes,
                    animeEpisodeSources = state.value.animeEpisodeSources
                )
            }.launchIn(viewModelScope)


    }

    fun getLinks(slug: String) {
        anime4up.getEpisode(slug).onEach {
            _state.value = MovieState(
                media = state.value.media,
                isLoading = false,
                season = null,
                watchList = state.value.watchList,
                animeEpisodes = state.value.animeEpisodes,
                animeEpisodeSources = it
            )
        }.launchIn(viewModelScope)
    }

}