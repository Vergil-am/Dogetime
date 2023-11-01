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
    private val getMovieUseCase: GetMovieUseCase,
): ViewModel()  {
    private val _state = mutableStateOf(MovieState(movie = null))
    var state : State<MovieState> = _state
    init {
        //TODO find a way to make the fetch data when id updates
        if (_state.value.type == "show") {
            getShow(state.value.id!!)

        } else if (_state.value.type == "movie") {
           getMovie(state.value.id!!)
        }
    }

    fun updateId(id: Int, type: String) {
       _state.value = MovieState(id= id, movie = null, show = null, isLoading = true, type = type)
    }

    private fun getMovie(id: Int) {
             getMovieUseCase.getMovieDetails(id = id).onEach { movieDetailsDTO ->
                 _state.value = MovieState(movie = movieDetailsDTO, show = null, isLoading = false)
            }.launchIn(viewModelScope)


    }
    private fun getShow(id: Int) {
        getMovieUseCase.getShow(id).onEach {showDetailsDTO ->

            _state.value = MovieState(movie = null , show = showDetailsDTO, isLoading = false)
        }.launchIn(viewModelScope)
    }

}