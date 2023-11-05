package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.kotlinmovieapp.domain.model.Season
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Episodes(
    season: Season,
    viewModel: DetailsViewModel,
    id: Int,
    navController: NavController
) {
    val state = viewModel.state.collectAsState()
//    LaunchedEffect(key1 = season) {
        viewModel.getSeason(id, season.season_number)
//    }
    Column {
//        state.value.seasons.find { seasonDTO ->  season.id == seasonDTO.id
//        }?.episodes?.forEach { episode ->
//            Text(text = episode.name)
//        }
        state.value.seasons?.episodes?.forEach { episode ->
            Card (
                onClick = {navController.navigate("video_player/$id/$season/$episode")}
            ) {
                Text(text = episode.name)
            }

        }
    }

}