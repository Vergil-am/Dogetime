package com.example.kotlinmovieapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.domain.model.Movie
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.use_case.animeiat.AnimeiatUseCase
import com.example.kotlinmovieapp.domain.use_case.list.ListUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movie.GetMovieUseCase
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import com.example.kotlinmovieapp.local.entities.WatchListMedia
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
    private val animeiat: AnimeiatUseCase,
//    private val list: ListUseCase
    private val watchList: WatchListUseCase
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

//    fun addToWatchlist(body: AddToWatchListDTO) {
//        viewModelScope.launch {
//            list.addToWatchList(body = body)
//        }
//    }

    fun updateWatchList(list: List<Movie>) {
       _state.value.watchList = list
    }

    private fun getAnime(slug: String) {
        animeiat.getAnimeDetails(slug).onEach {
            _state.value = MovieState(
                media = it,
                isLoading = false,
                season = null,
                watchList = state.value.watchList,
                animeEpisodes = state.value.animeEpisodes,
                animeEpisodeSources = state.value.animeEpisodeSources
            )
        }.launchIn(viewModelScope)
    }

    fun getAnimeEpisodes(slug: String, page: Int) {
        animeiat.getAnimeEpisodes(slug, page).onEach {
            if (page == 1) {
            _state.value = MovieState(
                media = state.value.media,
                isLoading = false,
                season = null,
                watchList = state.value.watchList,
                animeEpisodes = it.data,
                animeEpisodeId = state.value.animeEpisodeId,
                animeEpisodeSources = state.value.animeEpisodeSources
            )} else {
                _state.value = MovieState(
                    media = state.value.media,
                    isLoading = false,
                    season = null,
                    watchList = state.value.watchList,
                    animeEpisodes = state.value.animeEpisodes.plus(it.data),
                    animeEpisodeId = state.value.animeEpisodeId,
                    animeEpisodeSources = state.value.animeEpisodeSources
                )
            }
        }.launchIn(viewModelScope)
    }

    fun getAnimeEpisodeId(slug: String) {
        animeiat.getAnimeEpisodeId(slug).onEach {
            _state.value = MovieState(
                media = state.value.media,
                isLoading = false,
                season = null,
                watchList = state.value.watchList,
                animeEpisodes = state.value.animeEpisodes,
                animeEpisodeId =  it,
                animeEpisodeSources = null
            )
        }.launchIn(viewModelScope)
    }

    fun getAnimeEpisodeSources(slug: String) {
        animeiat.getAnimeEpisodeSources(slug).onEach {
            _state.value =  MovieState(
                media = state.value.media,
                isLoading = false,
                season = null,
                watchList = state.value.watchList,
                animeEpisodes = state.value.animeEpisodes,
                animeEpisodeId =  state.value.animeEpisodeId,
                animeEpisodeSources = it
            )
        }.launchIn(viewModelScope)
    }

    // WatchList
    fun addToWatchList(media: WatchListMedia ) {
        viewModelScope.launch {
            watchList.addToWatchList(media)
        }
    }

//    fun getWatchList(type: String) {
//       list.getWatchList(type).onEach {
//           list -> _state.value = MovieState(movie = state.value.movie, show = state.value.show, isLoading = false, season = state.value.season,
//               watchList = list.results
//           )
//       }.launchIn(viewModelScope)
//    }

}