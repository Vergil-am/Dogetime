package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.domain.model.Movie
import com.example.kotlinmovieapp.util.Constants.IMAGE_BASE_URL



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel (
    modifier: Modifier,
    movies: List<Movie>?
) {
    val pagerState = rememberPagerState (
        pageCount = { movies?.count() ?: 0 }
    )

    Card(
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState

        ) {page ->
            Card () {
               Image(painter =
               rememberAsyncImagePainter("${IMAGE_BASE_URL}/original${movies?.get(page)?.backdrop_path}"),
                   contentDescription = movies?.get(page)?.title,
                   contentScale = ContentScale.FillBounds
               )
            }
            
        }
    }
    
}


