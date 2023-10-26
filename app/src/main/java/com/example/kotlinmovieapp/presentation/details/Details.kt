package com.example.kotlinmovieapp.presentation.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Details(
    id : Int?,
    viewModel: DetailsViewModel
) {
    val movie = viewModel.state.value.movie
    viewModel.state.value.id = id
    if (id != null) {
        if (movie != null) {
            Text(text = movie.title)
        }
    }
}
