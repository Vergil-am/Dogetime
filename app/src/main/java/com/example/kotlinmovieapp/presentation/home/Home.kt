package com.example.kotlinmovieapp.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.presentation.components.MovieRow


@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state = viewModel.state.value
    viewModel.getWatchlist()
//    viewModel.getLatestEpisodes()
    Column (
    modifier = Modifier
        .verticalScroll(rememberScrollState())
    ) {
        state.watchList?.let {
            Text(text = "Continue watching",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            MovieRow(data = it, navController = navController)
        }

        state.movies?.let {
            Text(text = "Trending movies",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                )
            MovieRow(data = it, navController = navController)
        }
        state.shows?.let {

            Text(text = "Trending series",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                )
            MovieRow(data = it, navController = navController)
        }

        state.anime?.let {
            Text(text = "Latest anime updates",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                )
            MovieRow(data = it, navController = navController)

        }

            


    }
}



