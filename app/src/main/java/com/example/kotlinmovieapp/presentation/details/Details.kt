package com.example.kotlinmovieapp.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(
    viewModel: DetailsViewModel
) {
    val state = viewModel.state.collectAsState()
if (state.value.type == "movie") {
            state.value.id?.let { viewModel.getMovie(it) }
                Column(
                    modifier = Modifier
                ) {
                    state.value.movie?.let { Text(text = it.title)
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            painter = rememberAsyncImagePainter(

                                "${Constants.IMAGE_BASE_URL}/w200${it.backdrop_path}"
                            ),
                            contentDescription = it.title
                        )
                    }
                }
        }
    else if (state.value.type == "show") {

            state.value.id?.let { viewModel.getShow(it) }
                Column(
                    modifier = Modifier
                ) {
                    state.value.show?.let {
                        Text(text = it.name)
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            painter = rememberAsyncImagePainter(

                                "${Constants.IMAGE_BASE_URL}/w200${it.backdrop_path}"
                            ),
                            contentDescription = it.name
                        )
                    }
                }

        }
    }


