package com.example.kotlinmovieapp.presentation.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO
import com.example.kotlinmovieapp.domain.use_case.movies.get_movie.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase,
): ViewModel()  {
    private val _state = mutableStateOf(MovieState(movie = null))
    var state : State<MovieState> = _state


    fun getMovie(id: Int, type: String) {
        when (type) {
            "movie" ->             getMovieUseCase.getMovieDetails(id = id).onEach { movieDetailsDTO ->
                MovieState(isLoading = false, movie = movieDetailsDTO, id = null)
            }.launchIn(viewModelScope)
            "show" -> getMovieUseCase.getShow(id).onEach {showDetailsDTO ->
                MovieState(isLoading = false, id = null, show = showDetailsDTO)
            }
        }

    }
}