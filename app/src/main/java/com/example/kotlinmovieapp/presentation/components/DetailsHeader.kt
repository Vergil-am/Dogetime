package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.data.remote.dto.AddToWatchListDTO
import com.example.kotlinmovieapp.domain.model.Movie
import com.google.common.base.Ascii

@Composable
fun DetailsHeader(
    backDrop: String,
    title: String,
    poster: String,
    status: String,
    id: Int,
    type: String,
    tagline: String?,
    watchList: List<Movie>,
    addToWatchList: (AddToWatchListDTO) -> Unit

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)

    ) {

        Image(
            modifier = Modifier
                .fillMaxSize()
                .blur(5.dp),
            contentScale = ContentScale.FillWidth,
            painter = rememberAsyncImagePainter(
               backDrop
            ),
            contentDescription = title
        )
        Row (
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                painter = rememberAsyncImagePainter(
                   poster
                ),
                contentDescription = title
            )
            Column (
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = Ascii.toUpperCase(title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = status,
                    style =
                    MaterialTheme.typography.labelLarge
                )
                if (tagline != null) {
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                if (watchList.any { movie -> movie.id == id }) {
                    OutlinedIconButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        onClick = {
                            addToWatchList(
                                AddToWatchListDTO(
                                    media_id = id,
                                    media_type = type,
                                    watchlist = false
                                )
                            )
                        }
                    ) {

                        Text(text = "Remove from watchlist")
                    }

                } else {
                    OutlinedIconButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        onClick = {
                            addToWatchList(
                                AddToWatchListDTO(
                                    media_id = id,
                                    media_type = type,
                                    watchlist = true
                                )
                            )
                        }
                    ) {
                        Text(text = "ADD TO WATCHLIST")
                    }

                }
            }
        }
    }
}
