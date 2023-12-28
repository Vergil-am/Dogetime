package com.example.kotlinmovieapp.presentation.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.domain.model.Season
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel
import com.example.kotlinmovieapp.util.Constants
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Episodes(
    season: Season,
    viewModel: DetailsViewModel,
    id: Int,
    navController: NavController
) {
    val state  = viewModel.state.collectAsState().value.season

    LaunchedEffect(key1 = season, key2 = id ) {
            viewModel.getSeason(id, season.season_number)
    }


    Column (
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        state?.episodes?.forEach { episode ->
            val url = URLEncoder.encode("${Constants.VIDEO_URL}/tv/$id/${episode.season_number}/${episode.episode_number}")
            Card (
                onClick = {navController.navigate("web-view/$url")},
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
                                model = "${Constants.IMAGE_BASE_URL}/w200${episode.still_path}" ),
                            contentDescription = "Episode ${episode.episode_number}" )
                        Box (
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(Color.Black)

                        ) {
                            Text(text = "EP ${episode.episode_number}")
                        }
                    }

                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        episode.name?.let { Text(text = it) }
                        episode.air_date?.let { Text(text = it,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End

                            ) }
                        Text(text = "${episode.runtime} min",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                        
                    }
                }
            }

        }
    }

}

