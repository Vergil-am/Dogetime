package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.kotlinmovieapp.domain.model.Season
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel

@Composable
fun Episodes(
    season: Season,
    viewModel: DetailsViewModel,
    id: Int
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
            Text(text = episode.name)

        }
    }

}