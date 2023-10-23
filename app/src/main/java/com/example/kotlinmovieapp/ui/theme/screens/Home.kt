package com.example.kotlinmovieapp.ui.theme.screens

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.domain.use_case.movies.MoviesUseCase
import com.example.kotlinmovieapp.ui.theme.components.Carousel
import com.example.kotlinmovieapp.ui.theme.components.Filters


class Movie(val imageURL: String, val title: String)
val Movies = listOf(
    Movie(imageURL = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/b16RAVwj2QN6RAs752UJNzQ9Of0.jpg",
   title = "Saw x"
    ),
    Movie(imageURL = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/5gzzkR7y3hnY8AD1wXjCnVlHba5.jpg",
        title = "The nun 2"
    ),
    Movie(imageURL ="https://www.themoviedb.org/t/p/w600_and_h900_bestv2/voHUmluYmKyleFkTu3lOXQG702u.jpg",
        title = "Loki"),
    Movie(imageURL ="https://www.themoviedb.org/t/p/w600_and_h900_bestv2/isb2Qow76GpqYmsSyfdMfsYAjts.jpg",
        title = "Corpse and bride"
        ),
)

val Shows = listOf(
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/gdIrmf2DdY5mgN6ycVP0XlzKzbE.jpg"
        , title = "Rick and morty"

    ),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/uKvVjHNqB5VmOrdxqAt2F7J78ED.jpg"
        , title = "The Last of us"
    ),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/x8ZQyxAFjz9jtCGivbOMYUC4Tp3.jpg"
        , title = "Goblin slayer"
    ),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/1X4h40fcB4WWUmIBK0auT4zRBAV.jpg"
        , title = "House of dragon"
    ),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/cZ0d3rtvXPVvuiX22sP79K3Hmjz.jpg"
        , title = "The witcher"
    )

    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController
) {
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
                .height(209.5.dp)
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
            Movies.forEach { movie ->
                Card (
                    modifier = Modifier

                        .padding(horizontal = 5.dp)
                        .height(200.dp)
                        .width(133.5.dp),
                    onClick = {
                        navController.navigate("Movie/${movie.title}") {

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
                            movie.imageURL
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
            Shows.forEach { show ->
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
                            show.imageURL
                        ),
                        contentDescription = show.title
                    )
                }
            }
        }

    }
}



