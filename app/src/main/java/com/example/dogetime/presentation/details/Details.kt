package com.example.dogetime.presentation.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dogetime.data.local.entities.WatchListMedia
import com.example.dogetime.presentation.components.DetailsHeader
import com.example.dogetime.presentation.components.Source
import com.example.dogetime.presentation.navgraph.Route

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(
    navController: NavController, viewModel: DetailsViewModel, id: String, type: String
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(key1 = id) {
        viewModel.getMedia(type = type, id = id)
        if (type == "movie") {
            viewModel.getVidsrc(
                id = id, type = type, episode = null, season = null
            )
        }
    }
    val addToWatchList: (media: WatchListMedia) -> Unit = {
        viewModel.addToWatchList(it)
    }
    val deleteFromList: (media: WatchListMedia) -> Unit = {
        viewModel.deleteFromList(it)
    }
    viewModel.getMediaFromWatchList(id)
    val media = state.media

    var opened by remember {
        mutableStateOf(false)
    }

    when (state.isLoading) {
        true -> Text(text = "Loading ...")
        false -> Scaffold(floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    when (type) {
                        "movie" -> {
                            opened = true
                        }

                        "mycima - movie" -> {
                            opened = true
                        }

//                        "mycima - show" -> navController.navigate("mycima - show/seasons/$id")

                        "mycima - show" -> navController.navigate("show/seasons/$id")
                        "show" -> navController.navigate("show/seasons/$id")
                        "animeAR" -> navController.navigate("anime/episodes/$id")
                        "animeFR" -> navController.navigate("anime/episodes/$id")
                        "animeEN" -> navController.navigate("anime/episodes/$id")
                    }
                }) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "play")
                Text(text = "Watch")
            }
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues), verticalArrangement = Arrangement.Top


            ) {
                media?.let {
                    DetailsHeader(
                        backDrop = media.backdrop,
                        title = media.title,
                        poster = media.poster,
                        status = media.status,
                        id = media.id,
                        type = media.type,
                        watchList = if (state.watchList?.id == id) {
                            state.watchList
                        } else {
                            null
                        },
                        addToWatchList = addToWatchList,
                        deleteFromList = deleteFromList
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Movie")
                        Text(text = it.releaseDate.split("-")[0])
                        Text(text = "${it.runtime} min")
                        Text(text = it.rating.toString().format("%.f"))
                    }
                    if (it.tagline != null) {
                        Text(
                            text = it.tagline,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()

                        )
                    }
                    Text(
                        text = it.overview, modifier = Modifier.padding(10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)

                    ) {
                        it.genres.forEach { genre ->
                            ElevatedSuggestionChip(onClick = {}, label = { Text(text = genre) })
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
                state.movieSources.onEach {
                    Source(source = it,
                        subtitles = state.subtitles,
                        title = state.media?.title ?: "movie",
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