package com.example.kotlinmovieapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.domain.model.Movie
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
    private val list: ListUseCase
): ViewModel()  {
    private val _state = MutableStateFlow(MovieState())
    var state : StateFlow<MovieState> = _state



    fun getMovie(id: Int) {
            getMovieUseCase.getMovieDetails(id).onEach { movieDetailsDTO ->
                _state.value = MovieState(movie = movieDetailsDTO, show = null, isLoading = false,
                    watchList = state.value.watchList
                    )
            }.launchIn(viewModelScope)


    }
    fun getShow(id: Int) {
            getMovieUseCase.getShow(id).onEach { showDetailsDTO ->
                _state.value = MovieState(movie = null, show = showDetailsDTO, isLoading = false, season = state.value.season,
                        watchList = state.value.watchList
                )
            }.launchIn(viewModelScope)
    }

    fun getSeason(id: Int, season: Int) {
        getMovieUseCase.getSeason(id, season).onEach {seasonDTO ->
            _state.value = MovieState(movie = null, show = state.value.show, isLoading = false, season = seasonDTO,
                watchList = state.value.watchList
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
//    fun getWatchList(type: String) {
//       list.getWatchList(type).onEach {
//           list -> _state.value = MovieState(movie = state.value.movie, show = state.value.show, isLoading = false, season = state.value.season,
//               watchList = list.results
//           )
//       }.launchIn(viewModelScope)
//    }

}