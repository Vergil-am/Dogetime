package com.example.kotlinmovieapp.domain.use_case.movies.get_movie

import com.example.kotlinmovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieUseCase @Inject constructor(
    private val repo : MovieRepository
) {
}