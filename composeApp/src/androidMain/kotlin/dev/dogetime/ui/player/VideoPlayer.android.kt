package dev.dogetime.ui.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dev.dogetime.core.CapturedStream
import kotlinx.coroutines.delay

private const val POSITION_UPDATE_MS = 250L

actual val playerUsesMouse: Boolean = false

/** Plays HLS streams with ExoPlayer, sending the headers the stream's server expects. */
@OptIn(UnstableApi::class)
internal class ExoVideoPlayerController(context: Context, stream: CapturedStream) : VideoPlayerController() {

    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    init {
        val dataSource = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(stream.headers.filterKeys { it != "User-Agent" })
        stream.headers["User-Agent"]?.let { dataSource.setUserAgent(it) }
        val mediaItem = MediaItem.Builder()
            .setUri(stream.url)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        player.setMediaSource(HlsMediaSource.Factory(dataSource).createMediaSource(mediaItem))
        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) = updateState()

            override fun onTracksChanged(tracks: Tracks) = updateQualities(tracks)

            override fun onPlayerError(playbackError: PlaybackException) {
                error = "The stream couldn't be played (${playbackError.errorCodeName})."
                isBuffering = false
            }
        })
        player.playWhenReady = true
        player.prepare()
        isPlaying = true
    }

    fun updateState() {
        isPlaying = player.playWhenReady
        isBuffering = error == null && (player.playbackState == Player.STATE_BUFFERING || player.playbackState == Player.STATE_IDLE)
        isEnded = player.playbackState == Player.STATE_ENDED
        positionMs = player.currentPosition
        bufferedMs = player.bufferedPosition
        durationMs = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L
        speed = player.playbackParameters.speed
        isMuted = player.volume == 0f
        videoHeight = player.videoSize.height
    }

    /** ExoPlayer's video tracks for each quality key, used to pin a quality. */
    private var trackOverrides = emptyMap<String, TrackSelectionOverride>()

    /** Lists the stream's video variants as qualities: one per height, best bitrate first. */
    private fun updateQualities(tracks: Tracks) {
        val overrides = mutableMapOf<String, TrackSelectionOverride>()
        val available = mutableListOf<VideoQuality>()
        tracks.groups.forEachIndexed { groupIndex, group ->
            if (group.type != C.TRACK_TYPE_VIDEO) return@forEachIndexed
            for (trackIndex in 0 until group.length) {
                if (!group.isTrackSupported(trackIndex)) continue
                val format = group.getTrackFormat(trackIndex)
                val key = "$groupIndex:$trackIndex"
                overrides[key] = TrackSelectionOverride(group.mediaTrackGroup, trackIndex)
                available += VideoQuality(height = format.height.coerceAtLeast(0), bitrate = format.bitrate.toLong(), key = key)
            }
        }
        trackOverrides = overrides
        qualities = available.bestPerHeight()
    }

    override fun selectQuality(quality: VideoQuality?) {
        val override = quality?.let { trackOverrides[it.key] }
        val parameters = player.trackSelectionParameters.buildUpon()
        if (override == null) {
            parameters.clearOverridesOfType(C.TRACK_TYPE_VIDEO)
        } else {
            parameters.setOverrideForType(override)
        }
        player.trackSelectionParameters = parameters.build()
        selectedQuality = if (override == null) null else quality
    }

    override fun play() {
        if (player.playbackState == Player.STATE_ENDED) player.seekTo(0)
        player.play()
    }

    override fun pause() = player.pause()

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        this.positionMs = positionMs
    }

    override fun setPlaybackSpeed(speed: Float) {
        player.playbackParameters = PlaybackParameters(speed)
    }

    override fun setVolumeMuted(muted: Boolean) {
        player.volume = if (muted) 0f else 1f
        isMuted = muted
    }
}

@Composable
actual fun rememberVideoPlayerController(stream: CapturedStream, title: String): VideoPlayerController {
    val context = LocalContext.current
    val controller = remember(stream) { ExoVideoPlayerController(context.applicationContext, stream) }
    DisposableEffect(controller) {
        onDispose { controller.player.release() }
    }
    // ExoPlayer has no position callback, so the position is read while playing.
    LaunchedEffect(controller) {
        while (true) {
            controller.updateState()
            delay(POSITION_UPDATE_MS)
        }
    }
    return controller
}

@OptIn(UnstableApi::class)
@Composable
actual fun VideoSurface(controller: VideoPlayerController, fit: VideoFit, modifier: Modifier) {
    val exo = controller as ExoVideoPlayerController
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                setShutterBackgroundColor(Color.BLACK)
                setBackgroundColor(Color.BLACK)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                player = exo.player
            }
        },
        update = { view ->
            view.player = exo.player
            view.resizeMode = when (fit) {
                VideoFit.Fit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                VideoFit.Fill -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                VideoFit.Stretch -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        },
        onRelease = { view -> view.player = null },
        modifier = modifier,
    )
}

/** The player always fills the screen on phones. */
@Composable
actual fun rememberFullscreenController(): FullscreenController? = null

@Composable
actual fun PlayerSystemUiEffect() {
    val view = LocalView.current
    val activity = LocalContext.current.findActivity() ?: return
    DisposableEffect(activity) {
        val insets = WindowCompat.getInsetsController(activity.window, view)
        val previousOrientation = activity.requestedOrientation
        insets.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insets.hide(WindowInsetsCompat.Type.systemBars())
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        view.keepScreenOn = true
        onDispose {
            insets.show(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = previousOrientation
            view.keepScreenOn = false
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
