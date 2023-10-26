package com.example.kotlinmovieapp.presentation.details

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
    val state : State<MovieState> = _state
    init {
            getMovie(id)
    }

    private fun getMovie(id: Int) {
            getMovieUseCase.getMovieDetails(id = id).onEach { movieDetailsDTO ->
                Log.d("MOVIE FETCH", movieDetailsDTO.toString())
                state.value.movie = movieDetailsDTO
            }.launchIn(viewModelScope)
    }
}