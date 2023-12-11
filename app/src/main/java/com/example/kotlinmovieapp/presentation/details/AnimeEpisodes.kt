package com.example.kotlinmovieapp.presentation.details

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.util.Constants
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeEpisodes(
   viewModel: DetailsViewModel,
   slug: String
) {
    viewModel.getAnimeEpisodes(slug)
    val state = viewModel.state.collectAsState().value
    val episodes = state.animeEpisodes
    var opened by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Column (
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        episodes?.data?.forEach { episode ->
            Card (
                onClick = {
                    viewModel.getAnimeEpisodeId(episode.slug)
                    opened = true
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
                        modifier = Modifier
                            .fillMaxSize()
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
    if (opened) {
        ModalBottomSheet(
            onDismissRequest = { opened = false },
            modifier = Modifier.padding(10.dp)
        ) {
            state.animeEpisodeId?.let { viewModel.getAnimeEpisodeSources(it) }

            val episode = state.animeEpisodeSources?.data
            Text(
                text = "SELECT QUALITY",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
                )
            episode?.sources?.forEach {

                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {
                        // This will probably change later on
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(it.file), "video/*")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Row  (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)

                    ){
                        Column {
                            Text(text = it.name.uppercase(Locale.ROOT))
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


