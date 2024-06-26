package com.example.dogetime.presentation.player

import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView


@OptIn(UnstableApi::class)
@Composable
fun MediaPlayer(
    viewmodel: PlayerViewModel,
    windowCompat: WindowInsetsControllerCompat,
    back: () -> Unit
) {
    val state = viewmodel.state.collectAsState().value
    val source = state.source
    windowCompat.hide(WindowInsetsCompat.Type.systemBars())
    windowCompat.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    var player by remember {
        mutableStateOf<ExoPlayer?>(null)
    }
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalContext.current as LifecycleOwner

    LaunchedEffect(key1 = state.source, key2 = state.selectedSubtitle) {
        val currentPosition = state.currentTime

        val mediaItem = MediaItem.Builder()
            .setUri(source?.source?.url)
            .setSubtitleConfigurations(
                if (state.selectedSubtitle != null) {
                    listOf(
                        SubtitleConfiguration.Builder(Uri.parse(state.selectedSubtitle.file))
                            .setMimeType(MimeTypes.TEXT_VTT)
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build()
                    )
                } else {
                    emptyList()
                }
            )
            .build()

        player?.setMediaItem(mediaItem)
        player?.seekTo(currentPosition)
    }


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
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                viewmodel.updateDuration(
                    currentTime = player.currentPosition.coerceAtLeast(0L),
                    totalDuration = player.duration.coerceAtLeast(0L)
                )
            }
        }

        player?.addListener(listener)
        onDispose {
            viewmodel.addToHistory()
            lifecycleOwner.lifecycle.removeObserver(observer)
            player?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        AndroidView(
            factory = { context ->

                PlayerView(context).apply {
                    setBackgroundColor(Color.Black.toArgb())
                    val dataSourceFactory = DefaultHttpDataSource.Factory()
                    source?.source?.header?.let {
                        dataSourceFactory.setDefaultRequestProperties(
                            mapOf(
                                "Referer" to it,
                                "Accept-Language" to "en-US,en;q=0.5",
                                "Accept" to "*/*"
                            )
                        )
                    }

                    val mediaSourceFactory =
                        DefaultMediaSourceFactory(context).setDataSourceFactory(dataSourceFactory)

                    val subtitles = state.source?.subtitles?.map {
                        SubtitleConfiguration.Builder(Uri.parse(it.file))
                            .setMimeType(MimeTypes.TEXT_VTT)
                            .setLabel(it.label)
                            .build()
                    }

                    Log.e("Source url", source?.source?.url.toString())
                    val mediaItem = MediaItem.Builder()
                        .setUri(source?.source?.url)
                        .setSubtitleConfigurations(
                            subtitles?.toMutableList()
                                ?: mutableListOf()
                        )
                        .build()
                    this.player = ExoPlayer.Builder(context)
                        .setMediaSourceFactory(mediaSourceFactory).build().also { player = it }
                    player?.setMediaItem(mediaItem)
                    player?.prepare()
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize(),

            )
        if (player != null) {
            PlayerControls(
                isPlaying = state.isPlaying,
                currentPosition = state.currentTime,
                totalDuration = state.totalDuration,
                onPlay = {
                    player!!.play()
                    viewmodel.updateIsPlaying(it)
                },
                onPause = {
                    player!!.pause()
                    viewmodel.updateIsPlaying(it)
                },
                onSeek = { player?.seekTo(it) },
                subtitles = source?.subtitles ?: emptyList(),
                sources = state.sources,
                source = source?.source,
                changeSource = {
                    viewmodel.setSource(
                        PlayerSource(
                            source = it,
                            subtitles = state.source?.subtitles ?: emptyList()
                        )
                    )
                },
                title = state.media?.title ?: "",
                back = back,
                selectedSubtitle = state.selectedSubtitle,
                selectSubtitle = { viewmodel.selectSubtitle(it) }
            )
        }
    }
}


