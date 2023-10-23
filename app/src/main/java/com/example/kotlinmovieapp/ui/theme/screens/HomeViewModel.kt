package com.example.kotlinmovieapp.ui.theme.screens

import androidx.lifecycle.ViewModel
import com.example.kotlinmovieapp.domain.use_case.movies.MoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moviesUseCase: MoviesUseCase
): ViewModel() {
    val Movies = moviesUseCase.getMovies(
        source = "trending",
        query = "/movie/day?language=en-US"
    )
}