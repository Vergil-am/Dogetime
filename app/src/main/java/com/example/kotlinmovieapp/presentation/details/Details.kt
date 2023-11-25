package com.example.kotlinmovieapp.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.presentation.components.SeasonsTabs
import com.example.kotlinmovieapp.util.Constants
import com.google.common.base.Ascii.toUpperCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun  Details(
    navController: NavController,
    viewModel: DetailsViewModel,
    id: Int,
    type: String

) {
    val state = viewModel.state.collectAsState()
when (type ) {
    "movie" -> {
        viewModel.getMovie(id)
        val movie = state.value.movie
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ,
            verticalArrangement = Arrangement.Top


        ) {
            movie?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)

                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(10.dp) ,
                        painter = rememberAsyncImagePainter(
                            "${Constants.IMAGE_BASE_URL}/w500${it.backdrop_path}"
                        ),
                        contentDescription = it.title
                    )
                    Row (
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(10.dp)
                            .fillMaxWidth()
                            ,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            modifier = Modifier,
                            painter = rememberAsyncImagePainter(
                                "${Constants.IMAGE_BASE_URL}/w300${it.poster_path}"
                            ),
                            contentDescription = it.title
                        )
                        Column (
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(text = toUpperCase(it.title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = it.status,
                                style =
                                MaterialTheme.typography.labelLarge)
                            Text(
                                text = it.tagline,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            OutlinedIconButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                ,
                                onClick = {
                                    viewModel.addToWishlist(
                                        AddToWatchListDTO(
                                            media_id = it.id,
                                            media_type = "movie",
                                            watchlist = true
                                        )
                                    )
                                }
                                ) {
                               Text(text = "ADD TO WATCHLIST")
                            }

                        }

                    }

                }

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
                Text(
                    text = it.overview,
                    modifier = Modifier.padding(10.dp)
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    it.genres.forEach{
                        genre -> AssistChip(
                        onClick = {},
                        label = { Text(text = genre.name)}
                        )
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                    ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    onClick = {
                    navController.navigate("video_player/${it.imdb_id}/0/0")
                }
                ) {
                    Text(text = "Play")
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play"
                    )

                }

            }
        }
    }
    "show" -> {
        viewModel.getShow(id)
        val show = state.value.show
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            show?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)

                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(10.dp) ,
                        painter = rememberAsyncImagePainter(
                            "${Constants.IMAGE_BASE_URL}/w500${it.backdrop_path}"
                        ),
                        contentDescription = it.name
                    )
                    Row (
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(10.dp)
                            .fillMaxWidth()
                            ,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            modifier = Modifier,
                            painter = rememberAsyncImagePainter(
                                "${Constants.IMAGE_BASE_URL}/w300${it.poster_path}"
                            ),
                            contentDescription = it.name
                        )
                        Column (
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(text = toUpperCase(it.name),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = it.status,
                                style =
                                MaterialTheme.typography.labelLarge)
                            Text(
                                text = it.tagline,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            OutlinedIconButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                ,
                                onClick = {
                                    viewModel.addToWishlist(
                                        AddToWatchListDTO(
                                            media_id = it.id,
                                            media_type = "tv",
                                            watchlist = true
                                        )
                                    )
                                }
                                ) {
                               Text(text = "ADD TO WATCHLIST")
                            }

                        }

                    }

                }

                Text(text = it.tagline,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight(300),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                    )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TV Show")
                    Text(text = it.first_air_date.split("-")[0])
                    Text(text = "${it.episode_run_time} min")
                    Text(text = it.vote_average.toString().format("%.f"))
                }
                Text(text = it.overview,
                    modifier = Modifier.padding(10.dp)
                    )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    it.genres.forEach{
                        genre -> AssistChip(
                        onClick = {},
                        label = { Text(text = genre.name)}
                        )
                    }
                }
                SeasonsTabs(
                    id = it.id,
                    seasons = it.seasons,
                    viewModel = viewModel,
                    navController = navController
                    )

            }
        }
    }
}

}


