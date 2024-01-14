package com.example.kotlinmovieapp.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.presentation.components.MovieRow


@Composable
fun Home(
    navController: NavController, viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()
    viewModel.getWatchlist()
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        if (state.watchList?.isNotEmpty() == true) {
            Text(
                text = "Continue watching",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            MovieRow(data = state.watchList!!, navController = navController)

        }

        when (state.movies.isLoading) {
            true -> Text(text = "Loading ...")
            false -> state.movies.data?.let {
                Text(
                    text = "Trending movies",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                MovieRow(data = it, navController = navController)
            }
        }





        when (state.shows.isLoading) {
            true -> Text(text = "Loading ...")
            false -> state.shows.data?.let {
                Text(
                    text = "Trending series",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                MovieRow(data = it, navController = navController)
            }
        }

        when (state.anime.isLoading) {
            true -> Text(text = "Loading ... ")
            false -> state.anime.data?.let {
                Text(
                    text = "Latest anime updates",
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
}



