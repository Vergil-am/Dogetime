package com.example.dogetime.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.dogetime.domain.model.MovieHome

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieRow(
    data: List<MovieHome>,
    navController: NavController
) {
    LazyRow(
        modifier = Modifier
            .padding(horizontal = 10.dp)
        ,
    ) {
        data.forEach { movie ->
            item {
                Card (
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .height(200.dp)
                        .width(133.5.dp),
                    onClick = {
                        navController.navigate(
                                "${movie.type}/${movie.id}"
                        ) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                        painter = rememberAsyncImagePainter(
                            movie.poster
                        ),
                        contentDescription = movie.title
                    )
                }

            }
        }

    }
}