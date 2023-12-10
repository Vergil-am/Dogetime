package com.example.kotlinmovieapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.domain.model.Movie
import com.example.kotlinmovieapp.domain.use_case.animeiat.AnimeiatUseCase
import com.example.kotlinmovieapp.domain.use_case.list.ListUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movie.GetMovieUseCase
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
    private val list: ListUseCase
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
                    animeEpisodes = null
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
                    animeEpisodes = null
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
                animeEpisodes = null
            )
        }.launchIn(viewModelScope)
    }

    fun addToWatchlist(body: AddToWatchListDTO) {
        viewModelScope.launch {
            list.addToWatchList(body = body)
        }
    }

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
                animeEpisodes = state.value.animeEpisodes
            )
        }.launchIn(viewModelScope)
    }

    fun getAnimeEpisodes(slug: String) {
        animeiat.getAnimeEpisodes(slug).onEach {
            _state.value = MovieState(
                media = state.value.media,
                isLoading = false,
                season = null,
                watchList = state.value.watchList,
                animeEpisodes = it,
                animeEpisodeId = state.value.animeEpisodeId
            )
        }.launchIn(viewModelScope)
    }

    fun getAnimeEpisode(slug: String) {
        animeiat.getAnimeEpisode(slug).onEach {
            _state.value = MovieState(
                media = state.value.media,
                isLoading = false,
                season = null,
                watchList = state.value.watchList,
                animeEpisodes = state.value.animeEpisodes,
                animeEpisodeId =  it
            )
        }.launchIn(viewModelScope)
    }

//    fun getWatchList(type: String) {
//       list.getWatchList(type).onEach {
//           list -> _state.value = MovieState(movie = state.value.movie, show = state.value.show, isLoading = false, season = state.value.season,
//               watchList = list.results
//           )
//       }.launchIn(viewModelScope)
//    }

}