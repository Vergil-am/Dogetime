package com.example.kotlinmovieapp.presentation.details

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia
import com.example.kotlinmovieapp.presentation.components.DetailsHeader
import com.example.kotlinmovieapp.presentation.components.Source
import com.example.kotlinmovieapp.util.Constants
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(
    navController: NavController, viewModel: DetailsViewModel, id: String, type: String

) {
    viewModel.getMedia(type = type, id = id)
    val state by viewModel.state.collectAsState()
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
    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = {
            when (type) {
                "movie" -> {
                    opened = true
                }

                "show" -> navController.navigate("show/seasons/$id")
                "anime" -> navController.navigate("anime/episodes/$id")
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
                Source(source = "FHD 1080p",
                    link = URLEncoder.encode("${Constants.VIDSRC_FHD}/movie/$id?ds_langs=en,ar,fr"),
                    info = "Progress",
                    navController = navController,
                    onClick = { opened = false })
                Source(source = "Multi quality",
                    info = "No progress",
                    link = URLEncoder.encode("${Constants.VIDSRC_MULTI}/movie/$id"),
                    navController = navController,
                    onClick = { opened = false })

            }
        }
    }
}

