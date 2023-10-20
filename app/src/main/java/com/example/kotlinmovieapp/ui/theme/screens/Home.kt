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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.ui.theme.KotlinMovieAppTheme
import com.example.kotlinmovieapp.ui.theme.components.Carousel
import com.example.kotlinmovieapp.ui.theme.components.Filters


class Movie(val imageURL: String)
val Movies = listOf(
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/b16RAVwj2QN6RAs752UJNzQ9Of0.jpg"),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/5gzzkR7y3hnY8AD1wXjCnVlHba5.jpg"),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/voHUmluYmKyleFkTu3lOXQG702u.jpg"),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/isb2Qow76GpqYmsSyfdMfsYAjts.jpg"),
)

val Shows = listOf(
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/gdIrmf2DdY5mgN6ycVP0XlzKzbE.jpg"),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/uKvVjHNqB5VmOrdxqAt2F7J78ED.jpg"),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/x8ZQyxAFjz9jtCGivbOMYUC4Tp3.jpg"),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/1X4h40fcB4WWUmIBK0auT4zRBAV.jpg"),
    Movie("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/cZ0d3rtvXPVvuiX22sP79K3Hmjz.jpg")

    )

@Composable
fun Home() {
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
                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = rememberAsyncImagePainter(
                            movie.imageURL
                        ),
                        contentDescription = "john wick"
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
                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = rememberAsyncImagePainter(
                            show.imageURL
                        ),
                        contentDescription = "john wick"
                    )
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinMovieAppTheme {
        Home()
    }
}
