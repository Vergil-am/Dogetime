package com.example.kotlinmovieapp.ui.theme.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter


val Images = listOf(
    "https://image.tmdb.org/t/p/original/mRGmNnh6pBAGGp6fMBMwI8iTBUO.jpg",
    "https://image.tmdb.org/t/p/original/jP3FatCTHc460ZGW57q9ypTdBqu.jpg",
    "https://image.tmdb.org/t/p/original/ctMserH8g2SeOAnCw5gFjdQF8mo.jpg"
)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel (
    modifier: Modifier
) {
    val pagerState = rememberPagerState (
        pageCount = {Images.count()}
    )

    Card(
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState

        ) {page ->
            Card () {
               Image(painter = rememberAsyncImagePainter(Images[page]),
                   contentDescription = "The nun",
                   contentScale = ContentScale.FillBounds
               )
            }
            
        }
    }
    
}


