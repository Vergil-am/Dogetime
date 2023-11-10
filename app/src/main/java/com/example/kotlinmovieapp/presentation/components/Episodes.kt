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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.domain.model.Season
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel
import com.example.kotlinmovieapp.util.Constants

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
    ) {
        state?.episodes?.forEach { episode ->
            Card (
                onClick = {navController.navigate("video_player/$id/${season.season_number}/${episode.episode_number}")},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(10.dp)
                ,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
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
                                .padding(horizontal = 2.dp)
                                .background(Color.Black)

                        ) {
                            Text(text = "EP ${episode.episode_number}")
                        }
                    }

                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = episode.name)
                        Text(text = episode.air_date)
                        Text(text = "${episode.runtime} min")
                        
                    }
                }
            }

        }
    }

}

