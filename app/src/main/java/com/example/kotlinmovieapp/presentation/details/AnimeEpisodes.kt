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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeEpisodes(
   viewModel: DetailsViewModel,
   slug: String,
   navController: NavController
) {
    val state = viewModel.state.collectAsState().value
    val columnState = rememberLazyListState()
    val episodes = state.animeEpisodes
    var opened by remember {
        mutableStateOf(false)
    }

    LazyColumn (
        state = columnState
    ) {
        episodes.forEach { episode ->
            item {
                Card(
                    onClick = {
                              viewModel.getLinks(episode.slug)
//                        viewModel.getAnimeEpisodeId(episode.slug)
                        opened = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(10.dp),
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                        ) {
                            Image(
                                alignment = Alignment.TopStart,
                                modifier = Modifier
                                    .fillMaxSize(),
                                painter = rememberAsyncImagePainter(
                                    model = episode.poster
                                ),
                                contentDescription = "Episode ${episode.title}"
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color.Black)

                            ) {
                                Text(text = "EP ${episode.episodeNumber}")
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = episode.title)
                        }

                    }
                }
            }
        }
    }


    if (opened) {
        ModalBottomSheet(
            onDismissRequest = { opened = false },
//            modifier = Modifier.padding(10.dp),
            sheetState = rememberModalBottomSheetState()
        ) {
//            state.animeEpisodeId?.let { viewModel.getAnimeEpisodeSources(it) }

            val sources = state.animeEpisodeSources
            Text(
                text = "SELECT SOURCE",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
                )
            sources?.forEach {

                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {
//                        To DO
                        val url = URLEncoder.encode(it.url)
                        navController.navigate("web-view/${url}")
                    }
                ) {
                    Row  (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)

                    ){
                        Text(text = it.source)
                        Column {
//                            Text(text = it.label.uppercase(Locale.ROOT))
                            Text(text = "${it.label} ${it.quality}")
                        }
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "play"
                        )
                    }
                }
            }
        }
    }


}


