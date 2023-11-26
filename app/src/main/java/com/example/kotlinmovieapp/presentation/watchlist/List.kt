package com.example.kotlinmovieapp.presentation.watchlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchList(
    viewModel: ListViewModel,
    navController: NavController
) {
    viewModel.getWatchList("movies")
    val state = viewModel.state.collectAsState().value
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp)
    ) {

        state.movies?.results?.forEachIndexed { index, movie ->
            item {
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(155.dp),
                    onClick = {
                        if (
                            state.type == "movies"
                        ) {
                            navController.navigate("movie/${movie.id}")
                        } else {
                            navController.navigate("show/${movie.id}")
                        }
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),

                        painter = rememberAsyncImagePainter(
                            "${Constants.IMAGE_BASE_URL}/w200${movie.poster_path}"),
                        contentDescription = "test"
                    )
                }
            }
        }
    }
}

