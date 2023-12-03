package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.domain.model.Movie
import com.example.kotlinmovieapp.util.Constants.IMAGE_BASE_URL



@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Carousel (
    modifier: Modifier,
    movies: List<Movie>?,
    navController: NavController
) {
    val pagerState = rememberPagerState (
        pageCount = { movies?.count() ?: 0 }
    )

    Card(
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState

        ) {movie->
            Card (
                onClick = {
                    navController.navigate("movie/${movies?.get(movie)?.id}") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
               Image(painter =
               rememberAsyncImagePainter("${IMAGE_BASE_URL}/original${movies?.get(movie)?.backdrop_path}"),
                   contentDescription = movies?.get(movie)?.title,
                   contentScale = ContentScale.FillBounds
               )
            }
            
        }
    }
    
}


