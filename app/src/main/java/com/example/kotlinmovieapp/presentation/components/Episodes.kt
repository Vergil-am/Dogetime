package com.example.kotlinmovieapp.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val state by viewModel.state.collectAsState()
    Log.e("STATE", state.toString())

    LaunchedEffect(key1 = season.season_number, key2 = state.season?.season_number) {
        if (state.season?.season_number != season.season_number) {
            viewModel.getSeason(id, season.season_number)
        }
    }


    Column {
        state.season?.episodes?.forEach { episode ->
            Card (
                onClick = {navController.navigate("video_player/$id/${season.season_number}/${episode.episode_number}")},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Text(text = episode.name)
                Text(text = "${season.season_number} ${episode.episode_number}")
            }

        }
    }

}

