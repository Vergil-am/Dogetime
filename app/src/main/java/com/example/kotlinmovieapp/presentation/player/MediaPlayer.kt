package com.example.kotlinmovieapp.presentation.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView


@Composable
fun MediaPlayer() {
    val uri =
        "https://pnagb.vid5e4da3.site/_v2p-wweo/12a3c523f8105800ed8c394685aeeb0bc52ea85c01bdbfec170c7baea93ece832257df1a4b6125fcfa38c35da05dee86aad28d46d73fc4e9d4e5a43a5372b485619916a75c0db85f4dc3f3e83b157c1461619123140c75d09ac4a34b93f42fcd7c45f64a4c76bb13ba/h/dcea4/caeafccaa;15a3873cfa10585daa846515d1b0ea069138af455cb8a8ee490165a0a7.m3u8"

    var player by remember {
        mutableStateOf<ExoPlayer?>(null)
    }
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val lifecycleOwner = LocalContext.current as LifecycleOwner

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
            when (event) {
                Lifecycle.Event.ON_START -> player?.play()
                Lifecycle.Event.ON_STOP -> player?.pause()
                Lifecycle.Event.ON_DESTROY -> player?.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player  = ExoPlayer.Builder(context)
                        .setMediaSourceFactory(DefaultMediaSourceFactory(context)).build().also { player = it }
                    player?.setMediaItem(MediaItem.fromUri(uri))
                    player?.prepare()
                }
            },
//            update = {
//                when (lifecycle) {
//                    Lifecycle.Event.ON_PAUSE -> {
//                        it.onPause()
//                    }
//                    Lifecycle.Event.ON_RESUME -> {
//                        it.onResume()
//                    }
//                    else -> Unit
//                }
//
//            },
            modifier = Modifier.fillMaxSize(),
        )


    }


}