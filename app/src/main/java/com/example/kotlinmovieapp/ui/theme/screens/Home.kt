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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.ui.theme.KotlinMovieAppTheme

@Composable
fun Home() {
    Column (
    modifier = Modifier.padding(vertical = 20.dp)
    ) {

        Text(text = "Latest", modifier = Modifier.padding(bottom = 10.dp))
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Image(painter = rememberAsyncImagePainter(
                "https://image.tmdb.org/t/p/original/mRGmNnh6pBAGGp6fMBMwI8iTBUO.jpg"),
                contentDescription = "The nun")
        }
        Row (
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)
            ,
        ) {
            Card (
                modifier = Modifier

                    .padding(horizontal = 5.dp)
                    .height(300.dp)
                    .width(200.dp),
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        "https://imgs.search.brave.com/c0953Mh3IVTIfB-SomSQbRg7xsGICQNr9m3SUG-RL-o/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9pLnBp/bmltZy5jb20vb3Jp/Z2luYWxzLzUwL2Rl/LzE3LzUwZGUxNzJi/MjM4YTg1Nzg1ZDE1/NTlkM2ZmMDA4ZjZi/LmpwZw"
                    ),
                    contentDescription = "john wick"
                )
            }
            Card (
                modifier = Modifier

                    .padding(horizontal = 5.dp)
                    .height(300.dp)
                    .width(200.dp),
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        "https://imgs.search.brave.com/c0953Mh3IVTIfB-SomSQbRg7xsGICQNr9m3SUG-RL-o/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9pLnBp/bmltZy5jb20vb3Jp/Z2luYWxzLzUwL2Rl/LzE3LzUwZGUxNzJi/MjM4YTg1Nzg1ZDE1/NTlkM2ZmMDA4ZjZi/LmpwZw"
                    ),
                    contentDescription = "john wick"
                )
            }
            Card (
                modifier = Modifier

                    .padding(horizontal = 5.dp)
                    .height(300.dp)
                    .width(200.dp),
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        "https://imgs.search.brave.com/c0953Mh3IVTIfB-SomSQbRg7xsGICQNr9m3SUG-RL-o/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9pLnBp/bmltZy5jb20vb3Jp/Z2luYWxzLzUwL2Rl/LzE3LzUwZGUxNzJi/MjM4YTg1Nzg1ZDE1/NTlkM2ZmMDA4ZjZi/LmpwZw"
                    ),
                    contentDescription = "john wick"
                )
            }
            Card (
                modifier = Modifier

                    .padding(horizontal = 5.dp)
                    .height(300.dp)
                    .width(200.dp),
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        "https://imgs.search.brave.com/c0953Mh3IVTIfB-SomSQbRg7xsGICQNr9m3SUG-RL-o/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9pLnBp/bmltZy5jb20vb3Jp/Z2luYWxzLzUwL2Rl/LzE3LzUwZGUxNzJi/MjM4YTg1Nzg1ZDE1/NTlkM2ZmMDA4ZjZi/LmpwZw"
                    ),
                    contentDescription = "john wick"
                )
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
