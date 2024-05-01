package com.example.dogetime.presentation.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dogetime.R
import com.example.dogetime.util.convertTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    onPlay: (Boolean) -> Unit,
    onPause: (Boolean) -> Unit,
    onSeek: (seekTo: Long) -> Unit,
) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                modifier = Modifier.padding(horizontal = 6.dp),
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
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = convertTime(currentPosition))
                Text(text = convertTime(totalDuration))
            }
        }

    }
}