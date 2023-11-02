package com.example.kotlinmovieapp.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun  Details(
    viewModel: DetailsViewModel,
    id: Int,
    type: String

) {
    val state = viewModel.state.collectAsState()
when (type ) {
    "movie" -> {
        viewModel.getMovie(id)
        Column(
            modifier = Modifier
                .fillMaxSize()
            ,
            verticalArrangement = Arrangement.Top

        ) {
            state.value.movie?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)

                    ,
                    painter = rememberAsyncImagePainter(
                        "${Constants.IMAGE_BASE_URL}/w500${it.backdrop_path}"
                    ),
                    contentDescription = it.title
                )
                Text(text = it.title)
                Text(text = it.tagline)
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Movie")
                    Text(text = it.release_date.split("-")[0])
                    Text(text = "${it.runtime} min")
                    Text(text = it.vote_average.toString().format("%.f"))
                }
                Text(text = it.overview)
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    it.genres.forEach{
                        genre -> AssistChip(
                        onClick = { /*TODO*/ },
                        label = { Text(text = genre.name)}
                        )
                    }
                }

            }
        }
    }
    "show" -> {
        viewModel.getShow(id)
        Column(
            modifier = Modifier
        ) {
            state.value.show?.let {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(

                        "${Constants.IMAGE_BASE_URL}/w500${it.backdrop_path}"
                    ),
                    contentDescription = it.name
                )
            }
        }
}


        }
    }

