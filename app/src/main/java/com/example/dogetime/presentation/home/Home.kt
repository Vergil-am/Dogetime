package com.example.dogetime.presentation.home

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
import com.example.dogetime.presentation.components.MovieRow


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
                    text = "Trending movies multi subs",
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
                    text = "Trending series multi subs",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                MovieRow(data = it, navController = navController)
            }
        }

        when (state.myCimaMovies.isLoading) {
            true -> Text(text = "Loading ...")
            false -> state.myCimaMovies.data?.let {
                Text(
                    text = "اخر الافلام العربية والمترجمة",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                MovieRow(data = it, navController = navController)
            }
        }
        when (state.myCimaShows.isLoading) {
            true -> Text(text = "Loading ...")
            false -> state.myCimaShows.data?.let {
                Text(
                    text = "اخر الحلقات العربية والمترجمة",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                MovieRow(data = it, navController = navController)
            }
        }

        when (state.animeAR.isLoading) {
            true -> Text(text = "Loading ... ")
            false -> state.animeAR.data?.let {
                Text(
                    text = "اخر حلقات الانمي العربية",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                MovieRow(data = it, navController = navController)
            }
        }
        when (state.animeEN.isLoading) {
            true -> Text(text = "Loading ... ")
            false -> state.animeEN.data?.let {
                Text(
                    text = "Latest anime episodes english ",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                MovieRow(data = it, navController = navController)
            }
        }
        when (state.animeFR.isLoading) {
            true -> Text(text = "Loading ... ")
            false -> state.animeFR.data?.let {
                Text(
                    text = "Derniers épisodes d'anime français",
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



