package com.example.kotlinmovieapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.presentation.components.Carousel
import com.example.kotlinmovieapp.presentation.components.MovieRow


@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state = viewModel.state.value
    Column (
    modifier = Modifier
        .verticalScroll(rememberScrollState())
    ) {

        Text(text = "In cinemas", modifier = Modifier
            .padding(10.dp))
        Carousel(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(209.5.dp)
                .background(Color.Black),
            movies = state.trending?.results,
            navController
        )
        state.movies?.let { moviesDTO ->
            val movies = moviesDTO.results.map {
                MovieHome(
                    id = it.id,
                    title = it.title,
                    type = "movie",
                    poster = it.poster_path,
                    slug = null
                )
            }
            Text(text = "Trending movies")
            MovieRow(data = movies , navController = navController)
        }
        state.shows?.let { moviesDTO ->
            val shows = moviesDTO.results.map {
                MovieHome(
                    id = it.id,
                    title = "Title",
                    type = "show",
                    poster = it.poster_path,
                    slug = null
                )
            }
            Text(text = "Trending series")
            MovieRow(data = shows, navController = navController)
        }

        state.anime?.let { Anime ->
            val anime = Anime.data.map {
                MovieHome(
                    id = it.id,
                    title = it.anime_name,
                    type = "anime",
                    poster = it.poster_path,
                    slug = it.slug
                )
            }
            Text(text = "Popular anime")
            MovieRow(data = anime, navController = navController)

        }

            


    }
}



