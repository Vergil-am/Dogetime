package com.example.kotlinmovieapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.ui.theme.components.Carousel
import com.example.kotlinmovieapp.ui.theme.components.Filters
import com.example.kotlinmovieapp.util.Constants




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state = viewModel.state.value
    Column (
    modifier = Modifier
        .padding(vertical = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {

        Text(text = "In cinemas", modifier = Modifier
            .padding(10.dp))
        Carousel(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(209.5.dp),
            movies = state.trending?.results
        )
        Text(text = "Movies", modifier = Modifier
            .padding(horizontal = 10.dp))
        Filters()
        Row (
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)
            ,
        ) {
            state.movies?.results?.forEach { movie ->
                Card (
                    modifier = Modifier

                        .padding(horizontal = 5.dp)
                        .height(200.dp)
                        .width(133.5.dp),
                    onClick = {
                        navController.navigate("Movie/${movie.id}") {

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = rememberAsyncImagePainter(
                            "${Constants.IMAGE_BASE_URL}/w200${movie.poster_path}"
                        ),
                        contentDescription = movie.title
                    )
                }
            }
        }
        Text(text = "TV Shows", modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp)
        )

        Filters()
        Row (
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)
            ,
        ) {
            state.shows?.results?.forEach { show ->
                Card (
                    modifier = Modifier

                        .padding(horizontal = 5.dp)
                        .height(200.dp)
                        .width(133.5.dp),
                    onClick = {
                        navController.navigate("Movie/${show.title}") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = rememberAsyncImagePainter(

                            "${Constants.IMAGE_BASE_URL}/w200${show.poster_path}"
                        ),
                        contentDescription = show.title
                    )
                }
            }
        }

    }
}



