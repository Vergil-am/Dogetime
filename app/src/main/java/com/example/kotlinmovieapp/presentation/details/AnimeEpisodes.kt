package com.example.kotlinmovieapp.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeEpisodes(
   viewModel: DetailsViewModel,
   navController: NavController,
   slug: String
) {
    viewModel.getAnimeEpisodes(slug)
    
    val episodes = viewModel.state.collectAsState().value.animeEpisodes

    Column {
        episodes?.data?.forEach { episode ->
            Card (
                onClick = {
//                    navController.navigate("")
                          viewModel.getAnimeEpisode(episode.slug)
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(10.dp)
                ,
            ) {
                Row {
                    Box (
                        modifier = Modifier
                            .width(180.dp)
                    ) {
                        Image(
                            alignment = Alignment.TopStart,
                            modifier = Modifier
                                .fillMaxSize()
                            ,
                            painter = rememberAsyncImagePainter(
                                model = "${Constants.AIMEIAT_IMAGE_URL}/${episode.poster_path}" ),
                            contentDescription = "Episode ${episode.number}" )
                        Box (
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(Color.Black)

                        ) {
                            Text(text = "EP ${episode.number}")
                        }
                    }

                    Column (
                        modifier = Modifier.fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = episode.title)
                         Text(text = episode.published_at.split("T")[0],
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End

                        )
                    }

                }
            }
        }

    }

}


