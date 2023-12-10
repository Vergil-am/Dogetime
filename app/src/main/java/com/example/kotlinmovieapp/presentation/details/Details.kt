package com.example.kotlinmovieapp.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.presentation.components.DetailsHeader

@Composable
fun  Details(
    navController: NavController,
    viewModel: DetailsViewModel,
    id: String,
    type: String

) {
    viewModel.getMedia(type = type ,id = id)
    val state = viewModel.state.collectAsState()
    val addToWatchList: (AddToWatchListDTO) -> Unit = {
        viewModel.addToWatchlist(it)
    }
    val media = state.value.media
            Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ,
            verticalArrangement = Arrangement.Top


        ) {
                media?.let {
                    DetailsHeader(
                        backDrop = media.backdrop ,
                        title = media.title ,
                        poster = media.poster,
                        status = media.status,
                        id = media.id,
                        type = media.type,
                        tagline = media.tagline,
                        watchList = state.value.watchList,
                        addToWatchList = addToWatchList
                    )
                    Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Movie")
                    Text(text = it.releaseDate.split("-")[0])
                    Text(text = "${it.runtime} min")
                    Text(text = it.rating.toString().format("%.f"))
                }
                    Text(
                    text = it.overview,
                    modifier = Modifier.padding(10.dp)
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    it.genres.forEach{
                        genre -> AssistChip(
                        onClick = {},
                        label = { Text(text = genre)}
                        )
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                    ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    onClick = {
                    navController.navigate("video_player/${it.id}/0/0")
                }
                ) {
                    Text(text = "Play")
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play"
                    )

                }


                }


            }


}

