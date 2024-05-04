package com.example.dogetime.presentation.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dogetime.R
import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.convertTime
import com.example.dogetime.util.extractors.vidplay.models.Subtitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    onPlay: (Boolean) -> Unit,
    onPause: (Boolean) -> Unit,
    onSeek: (seekTo: Long) -> Unit,
    subtitles: List<Subtitle>,
    source: Source?,
    sources: List<Source>,
    changeSource: (Source) -> Unit
) {
    var opened by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                /*TODO*/
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Title", style = MaterialTheme.typography.headlineLarge
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onSeek(currentPosition - 15000)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.backward_15_seconds_svgrepo_com),
                    contentDescription = "seek backwards",
                    tint = Color.White
                )
            }

            if (isPlaying) {
                IconButton(onClick = {
                    onPause(false)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.pause_svgrepo_com),
                        contentDescription = "pause",
                        tint = Color.White
                    )
                }
            } else {
                IconButton(onClick = {
                    onPlay(true)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.play_svgrepo_com),
                        contentDescription = "play",
                        tint = Color.White
                    )
                }
            }

            IconButton(onClick = {
                onSeek(currentPosition + 15000)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.forward_15_seconds_svgrepo_com),
                    contentDescription = "seek forward",
                    tint = Color.White
                )
            }
        }
        Column(
            modifier = Modifier.padding(bottom = 25.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = convertTime(currentPosition))
                Slider(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    value = currentPosition.toFloat(),
                    valueRange = 0f..totalDuration.toFloat(),
                    onValueChange = {
                        onSeek(it.toLong())
                    },
                    colors = SliderDefaults.colors().copy(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.5f)
                    ),
                    thumb = {})
                Text(text = convertTime(totalDuration))

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /*TODO*/ }) {
                    Icon(
                        modifier = Modifier
                            .height(30.dp)
                            .padding(horizontal = 6.dp),
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.lock_svgrepo_com),
                        contentDescription = "Lock"
                    )
                    Text(
                        text = "Lock",
                        color = Color.White
                    )
                }

                TextButton(onClick = {
                    opened = "sources"
                }) {
                    Icon(
                        modifier = Modifier
                            .height(30.dp)
                            .padding(horizontal = 6.dp),
                        painter = painterResource(id = R.drawable.high_quality_svgrepo_com),
                        tint = Color.White,
                        contentDescription = "Source"
                    )
                    Text(
                        text = "Source",
                        color = Color.White
                    )
                }
                TextButton(onClick = { opened = "subtitles" }) {
                    Icon(
                        modifier = Modifier
                            .height(30.dp)
                            .padding(horizontal = 6.dp),
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.subtitles_svgrepo_com),
                        contentDescription = "Subtitles"
                    )
                    Text(
                        text = "Subtitles",
                        color = Color.White
                    )
                }
                TextButton(onClick = {
                    onSeek(currentPosition + 85000)
                }) {
                    Icon(
                        modifier = Modifier
                            .height(30.dp)
                            .padding(horizontal = 6.dp),
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.rewind_forward_svgrepo_com),
                        contentDescription = "Skip OP"
                    )
                    Text(
                        text = "Skip OP",
                        color = Color.White
                    )
                }
            }
        }

    }

    if (opened == "subtitles")
        ModalBottomSheet(onDismissRequest = {
            opened = ""
        }) {
            LazyColumn {
                subtitles.map {
                    item {
                        ListItem(headlineContent = {
                            Text(text = it.label)
                        })
                    }
                }
            }

        }
    if (opened == "sources")
        ModalBottomSheet(onDismissRequest = {
            opened = ""
        }) {
            LazyColumn {
                sources.map {
                    item {
                        ListItem(
                            modifier = Modifier.clickable {
                                changeSource(it)
                            },
                            headlineContent = {
                                Text(text = it.source)
                            },
                            supportingContent = {
                                Text(text = it.label)
                            },
                            trailingContent = {
                                if (source?.url == it.url) {
                                    Icon(imageVector = Icons.Filled.Check, contentDescription = "")
                                }

                            }
                        )
                    }
                }
            }

        }
}