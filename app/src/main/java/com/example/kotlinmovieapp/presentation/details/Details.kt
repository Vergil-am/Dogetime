package com.example.kotlinmovieapp.presentation.details

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Details(
    id : Int?,
    viewModel: DetailsViewModel
) {
    val movie = viewModel.state.value.movie
    movie.let {
        movieDetailsDTO ->
        if (movieDetailsDTO != null) {
            Text(text = movieDetailsDTO.title)
        } else {
            Text(text = "$id")
        }
    }
    if (movie != null) {
        Text(text = movie.title)
    }

}
