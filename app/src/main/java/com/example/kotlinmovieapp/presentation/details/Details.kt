package com.example.kotlinmovieapp.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(
    viewModel: DetailsViewModel
) {

    val movie = viewModel.state.value.movie
    val show = viewModel.state.value.show

    Column (
        modifier = Modifier

    ) {
        Text(text = viewModel.state.value.id.toString())
        if (movie != null) {
            Image(painter =
            rememberAsyncImagePainter(
                model = "${com.example.kotlinmovieapp.util.Constants.IMAGE_BASE_URL}/original${movie.backdrop_path}" ),
                contentDescription = movie.title
            )
            Row (
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally)
                ,
                Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "${movie.runtime} min")
                Text(text = movie.release_date)
                Text(text = movie.vote_average.toString().format("%.f"))

            }

            Text(text = movie.title)
            Card(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = movie.overview)
            }


            Row (
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally)
                ,
                Arrangement.spacedBy(16.dp)
            ) {
            movie.genres.map {
                genre -> SuggestionChip(onClick = { /*TODO*/ }, label = {
                    Text(text = genre.name)
            })


            }

            }
        } else if (show != null){
            Text(text = "Show")
            Text(text = show.name)
        }
    }
}
