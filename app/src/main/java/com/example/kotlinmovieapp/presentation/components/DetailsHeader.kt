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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.data.local.entities.WatchListMedia
import com.example.kotlinmovieapp.util.Constants
import com.google.common.base.Ascii

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsHeader(
    backDrop: String,
    title: String,
    poster: String,
    status: String,
    id: String,
    type: String,
    tagline: String?,
    watchList: WatchListMedia?,
    addToWatchList: (WatchListMedia) -> Unit,

) {
   var expanded by remember {
       mutableStateOf(false)
   }
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
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        onClick = {
                           expanded = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.White
                        )
                    ) {
                        if (watchList != null) {
                            Text(text = watchList.list)
                        } else {
                            Text(text = "ADD TO WATCHLIST")
                        }
                    }

                }
            }
        }

    if (expanded) {
        ModalBottomSheet(onDismissRequest = {
            expanded = false
        }) {
            Constants.lists.forEach {
                DropdownMenuItem(
                    text = {
                                Row (modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "check",
                                        modifier = Modifier.alpha(if (watchList?.list == it) {1f} else {0.0f})
                                    )
                                    Text(text = it, modifier = Modifier.padding(horizontal = 10.dp))
                                }
                           },
                    onClick = {
                    addToWatchList(
                        WatchListMedia(
                            id = id,
                            title = title,
                            poster = poster,
                            type = type,
                            list = it
                        )
                    )
                    expanded = false
                })
            }
            if (watchList != null) {
                DropdownMenuItem(text = {
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "check",
                            modifier = Modifier.alpha(0.0f)
                        )
                        Text(text = "Remove", modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }, onClick = { /*TODO*/ })
            }

        }
    }



}
