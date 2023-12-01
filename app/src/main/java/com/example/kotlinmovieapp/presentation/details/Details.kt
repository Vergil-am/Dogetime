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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.presentation.components.DetailsHeader
import com.example.kotlinmovieapp.presentation.components.SeasonsTabs
import com.example.kotlinmovieapp.presentation.components.ShowInfo

@Composable
fun  Details(
    navController: NavController,
    viewModel: DetailsViewModel,
    id: Int,
    type: String

) {
    val state = viewModel.state.collectAsState()
    val addToWatchList: (AddToWatchListDTO) -> Unit = {
        viewModel.addToWatchlist(it)
    }
when (type ) {
    "movie" -> {
        viewModel.getMovie(id)
        viewModel.getWatchList("movies")

        val movie = state.value.movie
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ,
            verticalArrangement = Arrangement.Top


        ) {
            movie?.let {
                DetailsHeader(
                    backDrop = it.backdrop_path,
                    title = it.title,
                    poster = it.poster_path,
                    status = it.status,
                    id = it.id,
                    type = "movie",
                    tagline = it.tagline,
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
                    Text(text = it.release_date.split("-")[0])
                    Text(text = "${it.runtime} min")
                    Text(text = it.vote_average.toString().format("%.f"))
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
                        label = { Text(text = genre.name)}
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
                    navController.navigate("video_player/${it.imdb_id}/0/0")
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
    "show" -> {
        viewModel.getShow(id)
        viewModel.getWatchList("tv")
        var selected by remember {
            mutableStateOf("Info")
        }
        val show = state.value.show
        Scaffold (
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = selected == "Info",
                        onClick = {
                            selected = "Info"
                        },
                        icon = {
                            Icon(imageVector = Icons.Filled.Info, contentDescription = "Info" )
                        })
                    NavigationBarItem(selected = selected == "Watch",
                        onClick = {
                            selected = "Watch" },
                        icon = { Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Watch")})

                }
            }
        ) {
            paddingValues ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                show?.let {
                    DetailsHeader(
                        backDrop = it.backdrop_path,
                        title = it.name,
                        poster = it.poster_path,
                        status = it.status,
                        id = it.id,
                        type = "tv",
                        tagline = it.tagline,
                        watchList = state.value.watchList,
                        addToWatchList = addToWatchList
                    )
                    when (selected) {
                        "Info" -> ShowInfo(show = it)
                        "Watch" -> SeasonsTabs(
                            id = it.id,
                            seasons = it.seasons,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }


                }
            }
        }

    }
}

}

