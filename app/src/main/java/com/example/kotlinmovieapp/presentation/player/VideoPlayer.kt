package com.example.kotlinmovieapp.presentation.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


@Composable
fun VideoPlayer() {
    val url = "videos2.sendvid.com/01/13/vavl3rvv.mp4?validfrom=1709624430&validto=1709638830&rate=250k&ip=129.45.33.220&hash=jwqurJhOkBicmZ1BpPgBwLcyyMQ%3D"
    val context = LocalContext.current


    val player = ExoPlayer.Builder(context).build()




}