package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier
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

//    LaunchedEffect(key1 = season ) {

        if (state == null || state.season_number != season.season_number) {
            viewModel.getSeason(id, season.season_number)
        }
//    }


    Column {
        state?.episodes?.forEach { episode ->
            Card (
                onClick = {navController.navigate("video_player/$id/${season.season_number}/${episode.episode_number}")},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Row {
                    Image(painter = rememberAsyncImagePainter(model = "${Constants.IMAGE_BASE_URL}/w500${episode.still_path}" ), contentDescription = "Episode ${episode.episode_number}" )
                    Text(text = "Episode ${episode.episode_number}: ${episode.name}")
                }
            }

        }
    }

}

