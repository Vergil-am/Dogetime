package com.example.kotlinmovieapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.movies.get_movie.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase,
): ViewModel()  {
    private val _state = MutableStateFlow(MovieState())
    var state : StateFlow<MovieState> = _state

    fun getMovie(id: Int) {
            getMovieUseCase.getMovieDetails(id).onEach { movieDetailsDTO ->

                _state.value = MovieState(movie = movieDetailsDTO, show = null, isLoading = false)
            }.launchIn(viewModelScope)


    }
    fun getShow(id: Int) {
            getMovieUseCase.getShow(id).onEach { showDetailsDTO ->
                _state.value = MovieState(movie = null, show = showDetailsDTO, isLoading = false)
            }.launchIn(viewModelScope)
    }

    fun getSeason(id: Int, season: Int) {
        getMovieUseCase.getSeason(id, season).onEach {seasonDTO ->
            _state.value = MovieState(movie = null, show = state.value.show, isLoading = false, seasons = seasonDTO)
        }.launchIn(viewModelScope)
    }

}