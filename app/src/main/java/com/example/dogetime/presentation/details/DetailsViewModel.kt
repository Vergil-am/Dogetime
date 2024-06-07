package com.example.dogetime.presentation.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogetime.data.local.entities.WatchListMedia
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.use_case.anime4up.Anime4upUseCase
import com.example.dogetime.domain.use_case.animecat.AnimeCatUseCase
import com.example.dogetime.domain.use_case.goganime.GogoAnimeUseCase
import com.example.dogetime.domain.use_case.movies.get_movie.GetMovieUseCase
import com.example.dogetime.domain.use_case.mycima.MyCimaUseCase
import com.example.dogetime.domain.use_case.watchlist.WatchListUseCase
import com.example.dogetime.domain.use_case.witanime.WitanimeUseCase
import com.example.dogetime.util.Resource
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
    private val witanime: WitanimeUseCase,
    private val gogoanime: GogoAnimeUseCase,
    private val animeCat: AnimeCatUseCase,
    private val myCima: MyCimaUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MovieState())
    var state = _state.asStateFlow()


    fun getMedia(type: String, id: String) {
        when (type) {
            "movie" -> getMovie(id.toInt())
            "show" -> getShow(id.toInt())
            "animeAR" -> getAnimeAR(id)
            "animeEN" -> getAnimeEN(id)
            "animeFR" -> getAnimeFR(id)
            "mycima - movie" -> getMyCimaDetails(id, type)
            "mycima - show" -> getMyCimaDetails(id, type)
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

    private fun getAnimeAR(slug: String) {
        anime4up.getAnimeDetails(slug).onEach {
            when (it) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> _state.value = _state.value.copy(
                    media = it.data?.details,
                    animeEpisodes = it.data?.episodes ?: emptyList(),
                    isLoading = false
                )

                is Resource.Error -> {
//                    TODO()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAnimeEN(slug: String) {
        gogoanime.getAnimeDetails(slug).onEach {
            when (it) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> _state.value = _state.value.copy(
                    media = it.data?.details,
                    animeEpisodes = it.data?.episodes ?: emptyList(),
                    isLoading = false
                )

                is Resource.Error -> {
//                    TODO()
                }
            }
        }.launchIn(viewModelScope)


    }

    private fun getAnimeFR(slug: String) {
        animeCat.getAnimeDetails(slug).onEach {
            when (it) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        media = it.data?.details,
                        animeEpisodes = it.data?.episodes ?: emptyList(),
                        isLoading = false
                    )
                }

                is Resource.Error -> {
//                    TODO()
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

        animeCat.getSources(slug).onEach {
            _state.value = _state.value.copy(
                animeEpisodeSources = _state.value.animeEpisodeSources.plus(it)
            )
        }.launchIn(viewModelScope)
        gogoanime.getSources(slug).onEach {
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
    fun getVidsrc(id: String?, type: String, episode: Int?, season: Int?) {
        _state.value = _state.value.copy(
            movieSources = emptyList(), subtitles = emptyList()
        )
        if (id != null) {
            getMovieUseCase.getSources(id, type, episode, season).onEach {
                _state.value = _state.value.copy(
                    movieSources = it.sources, subtitles = it.subtitles
                )
            }.launchIn(viewModelScope)
        }
    }

    fun selectSource(source: Source) {
        _state.value = _state.value.copy(
            selectedSource = source
        )
    }

    private fun getMyCimaDetails(id: String, type: String) {
        myCima.getMovieDetails(id, type).onEach {
            when (it) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        media = it.data?.details,
                        movieSources = it.data?.sources ?: emptyList(),
                        isLoading = false
                    )
                }

                is Resource.Error -> {
//                    TODO()
                }
            }
        }.launchIn(viewModelScope)

    }

    fun getMyCimaEpisodeSources(id: String) {
        myCima.getEpisodeSources(id).onEach {
            _state.value = _state.value.copy(movieSources = it)
        }.launchIn(viewModelScope)
    }
}