package com.example.kotlinmovieapp.presentation.details

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia
import com.example.kotlinmovieapp.presentation.components.Source
import com.example.kotlinmovieapp.presentation.components.WatchedIndicator

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeEpisodes(
    viewModel: DetailsViewModel, navController: NavController
) {
    val state = viewModel.state.collectAsState().value
    val columnState = rememberLazyListState()
    val episodes = state.animeEpisodes
    var opened by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    LazyColumn(
        state = columnState
    ) {
        episodes.forEach { episode ->
            item {
                Card(
                    onClick = {
                        val media = state.media
                        if (media != null) {
                            viewModel.addToWatchList(
                                WatchListMedia(
                                    id = media.id,
                                    title = media.title,
                                    poster = media.poster,
                                    type = media.type,
                                    list = "watching",
                                    season = null,
                                    episode = episode.episodeNumber.toIntOrNull()
                                )
                            )
                        }
                        viewModel.getLinks(episode.slug)
                        opened = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(10.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row {
                            Box(
                                modifier = Modifier.width(180.dp)
                            ) {
                                Image(
                                    alignment = Alignment.TopStart,
                                    modifier = Modifier.fillMaxSize(),
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
                        val episodeNum = episode.episodeNumber.toIntOrNull()
                        val progress = state.watchList?.episode
                        if (progress != null && episodeNum != null) {
                            if (progress >= episodeNum) {
                                WatchedIndicator()
                            }
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
            val sources = state.animeEpisodeSources
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
                sources.map {
                    Source(source = it.source,
                                info = it.quality,
                                link = it.url,
                                navController,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(Uri.parse(it.url), "video/*")
                                    intent.putExtra("Referer", it.header)
                                    context.startActivity(intent)

                                    opened = false
                                })
                        }
                }
//                if (sources != null) {
//                    if (sources.fhd != null) {
//                        Text(text = "Full HD")
//                        sources.fhd.forEach { (source, link) ->
//                            Source(source = source,
//                                info = "1080p",
//                                link = link,
//                                navController,
//                                onClick = { opened = false })
//                        }
//                    }
//                    if (sources.hd != null) {
//                        Text(text = "HD")
//                        sources.hd.forEach { (source, link) ->
//                            Source(source = source,
//                                info = "720p",
//                                link = link,
//                                navController,
//                                onClick = { opened = false })
//                        }
//                    }
//                    if (sources.sd != null) {
//                        Text(text = "Low quality")
//                        sources.sd.forEach { (source, link) ->
//                            Source(source = source,
//                                info = "480p",
//                                link = link,
//                                navController,
//                                onClick = { opened = false })
//                        }
//                    }

                }
            }
        }

//    }
//}


