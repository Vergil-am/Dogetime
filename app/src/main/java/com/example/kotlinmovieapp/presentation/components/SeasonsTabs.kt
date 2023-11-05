package com.example.kotlinmovieapp.presentation.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.kotlinmovieapp.domain.model.Season
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel

@Composable
fun SeasonsTabs(
    seasons: List<Season>,
    id: Int,
    viewModel: DetailsViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val state = viewModel.state.collectAsState()
    ScrollableTabRow(
        selectedTabIndex = selectedTab,

        ) {
        seasons.forEachIndexed { index, season ->
            Tab(
                selected = index == selectedTab,
                onClick = {
                    selectedTab = index
                    viewModel.getSeason(id, season.season_number)
                }) {
                Text(text = season.season_number.toString())

            }
        }

    }

}