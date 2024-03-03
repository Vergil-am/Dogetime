package com.example.kotlinmovieapp.presentation.details

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia
import com.example.kotlinmovieapp.domain.use_case.anime4up.Anime4upUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movie.GetMovieUseCase
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import com.example.kotlinmovieapp.domain.use_case.witanime.WitanimeUseCase
import com.example.kotlinmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase,
    private val watchList: WatchListUseCase,
    private val anime4up: Anime4upUseCase,
    private val witanime: WitanimeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MovieState())
    var state = _state.asStateFlow()


    fun getMedia(type: String, id: String) {
        when (type) {
            "movie" -> getMovie(id.toInt())
            "show" -> getShow(id.toInt())
            "anime" -> getAnime(id)
        }
    }

    private fun getMovie(id: Int) {
        getMovieUseCase.getMovieDetails(id).onEach {
            when (it) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, media = null)
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, media = it.data)
                    Log.e("TV", it.data?.seasons.toString())
                }

                is Resource.Error -> _state.value =
                    _state.value.copy(isLoading = false, media = null)
            }
        }.launchIn(viewModelScope)


    }

    private fun getShow(id: Int) {
        getMovieUseCase.getShow(id).onEach {
            when (it) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, media = null)
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, media = it.data)
                    Log.e("DETAILS", "hello ${it.data?.seasons.toString()}")
                }

                is Resource.Error -> _state.value =
                    _state.value.copy(isLoading = false, media = null)
            }
        }.launchIn(viewModelScope)
    }


    // WatchList
    fun addToWatchList(media: WatchListMedia) {
        viewModelScope.launch {
            watchList.addToWatchList(media)
        }
        _state.value = _state.value.copy(watchList = media)
    }

    fun getMediaFromWatchList(id: String) {
        watchList.getMediaById(id).onEach {
            _state.value = _state.value.copy(watchList = it)
        }.launchIn(viewModelScope)

    }

    private fun getAnime(slug: String) {
        anime4up.getAnimeDetails(slug).onEach {
            when (it) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> _state.value = _state.value.copy(
                    media = it.data?.details,
                    animeEpisodes = it.data?.episodes ?: emptyList(),
                    isLoading = false
                )

                is Resource.Error -> {
                    TODO()
                }
            }
        }.launchIn(viewModelScope)


    }

    fun getLinks(slug: String) {
        _state.value = _state.value.copy(
            animeEpisodeSources = emptyList()
        )
        anime4up.getEpisode(slug).onEach {
            _state.value = _state.value.copy(
                animeEpisodeSources = _state.value.animeEpisodeSources.plus(it)
            )
        }.launchIn(viewModelScope)
        witanime.getSources(slug).onEach {
            _state.value = _state.value.copy(
                animeEpisodeSources = _state.value.animeEpisodeSources.plus(it)
            )
        }.launchIn(viewModelScope)
    }

    fun deleteFromList(media: WatchListMedia) {
        viewModelScope.launch {
            watchList.deleteFromList(media)
        }
        _state.value = _state.value.copy(watchList = null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getVidsrc(id: Int?, type: String, episode: Int?, season: Int?) {
       _state.value = _state.value.copy(
           movieSources = emptyList(),
           subtitles = emptyList()
       )
        if (id != null) {
            getMovieUseCase.getSources(id, type, episode, season).onEach {
                _state.value = _state.value.copy(
                    movieSources = it.sources,
                    subtitles = it.subtitles
                    )
            }.launchIn(viewModelScope)
//            vidsrc.getSources(url).onEach {
//                _state.value = _state.value.copy(
//                    movieSources = it.plus(
//                        Source(
//                            source = "vidsrc", url = when (type) {
//                                "movie" -> {
//                                    "${Constants.VIDSRC_FHD}/movie/$id?ds_langs=en,ar,fr"
//                                }
//
//                                "show" -> {
//                                    "${Constants.VIDSRC_FHD}/tv/$id/$season/$episode?ds_langs=en,ar,fr"
//                                }
//                                else -> ""
//                            }, quality = "1080p", label = "webview", header = null
//                        )
//                    ).plus(
//                        Source(
//                            source = "vidsrc", url = when (type) {
//                                "movie" -> {
//                                    "${Constants.VIDSRC_MULTI}/embed/movie/$id"
//                                }
//
//                                "show" -> {
//                                    "${Constants.VIDSRC_MULTI}/embed/tv/$id/$season/$episode"
//                                }
//
//                                else -> ""
//                            }, quality = "mutli", label = "webview", header = null
//                        )
//                    )
//                )
//            }.launchIn(viewModelScope)
        }
    }
}