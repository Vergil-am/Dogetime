package com.example.dogetime.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.dogetime.presentation.details.DetailsViewModel
import com.example.dogetime.presentation.navgraph.Route
import com.example.dogetime.util.Constants

data class SelectedEpisode(
    val season: Int, val episode: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Episodes(
    season: Int, viewModel: DetailsViewModel, id: Int, navController: NavController
) {
    val state by viewModel.state.collectAsState()

    var opened by remember {
        mutableStateOf(false)
    }
    var selected by remember {
        mutableStateOf(SelectedEpisode(season = 1, episode = 1))
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        if (!state.media?.seasons.isNullOrEmpty()) {
            state.media?.seasons?.get(season)?.episodes?.forEach { episode ->
                Card(
                    onClick = {
                        viewModel.getVidsrc(
                            id = id,
                            type = "show",
                            episode = episode.episode_number,
                            season = season
                        )
//                        viewModel.addToWatchList(
//                            WatchListMedia(
//                                id = id.toString(),
//                                list = "watching",
//                                season = season,
//                                episode = episode.episode_number,
//                                poster = state.media?.poster ?: "",
//                                title = state.media?.title ?: "",
//                                type = "show"
//                            )
//                        )
                        opened = true
                        selected = SelectedEpisode(
                            season = season, episode = episode.episode_number
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(10.dp),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row {
                            Box(
                                modifier = Modifier.width(180.dp)
                            ) {
                                Image(
                                    alignment = Alignment.TopStart,
                                    modifier = Modifier.fillMaxSize(),
                                    painter = rememberAsyncImagePainter(
                                        model = "${Constants.IMAGE_BASE_URL}/w200${episode.still_path}"
                                    ),
                                    contentDescription = "Episode ${episode.episode_number}"
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Black)

                                ) {
                                    Text(text = "EP ${episode.episode_number}")
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                            ) {
                                episode.name?.let { Text(text = it) }
                                episode.air_date?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End

                                    )
                                }
                                Text(
                                    text = "${episode.runtime} min",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )

                            }
                        }
                        val progressSeason = state.watchList?.season
                        val progressEpisode = state.watchList?.episode
                        if (progressEpisode != null && progressSeason != null) {
                            if (progressSeason > season) {
                                WatchedIndicator()
                            } else if (progressSeason == season && progressEpisode >= episode.episode_number) {
                                WatchedIndicator()
                            }
                        }

                    }
                }

            }
        }
        if (opened) {
            ModalBottomSheet(
                onDismissRequest = { opened = false }, sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "SELECT SOURCE",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (state.movieSources.isEmpty()) {
                        Text(text = "Sorry no sources available",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                            )
                    } else {
                        state.movieSources.forEach {
                            Source(source = it,
                                subtitles = state.subtitles,
                                title = "${state.media?.title} S${selected.season} EP${selected.episode}",
                                onClick = {
                                    opened = false
                                    viewModel.selectSource(it)
                                    navController.navigate(Route.Mediaplayer.route)
                                })
                        }
                    }


                }
            }
        }
    }
}
