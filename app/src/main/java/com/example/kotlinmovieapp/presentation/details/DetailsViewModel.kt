package com.example.kotlinmovieapp.presentation.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.movies.get_movie.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase
): ViewModel()  {
    private val _state = mutableStateOf(MovieState(movie = null))
    val state : State<MovieState> = _state
    val id = state.value.id
    init {
        getMovie(this.id)
    }

    private fun getMovie(id: Int?) {
        if (id != null) {
            getMovieUseCase.getMovieDetails(id = id).onEach { movieDetailsDTO ->  state.value.movie = movieDetailsDTO
            }.launchIn(viewModelScope)
        }
    }
}