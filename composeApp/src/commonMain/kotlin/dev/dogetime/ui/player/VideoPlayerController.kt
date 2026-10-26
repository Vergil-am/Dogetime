package dev.dogetime.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.dogetime.core.CapturedStream

/** How the video fills the player when their aspect ratios differ. */
enum class VideoFit(val label: String) {
    /** Whole video visible, with black bars. */
    Fit("Fit"),

    /** Fills the player, cropping the edges. */
    Fill("Fill"),

    /** Fills the player, distorting the picture. */
    Stretch("Stretch"),
}

/**
 * One of the stream's qualities. [key] identifies it to the platform player
 * (a variant playlist URL, or ExoPlayer's track).
 */
data class VideoQuality(val height: Int, val bitrate: Long, val key: String) {
    val label: String
        get() = if (height > 0) "${height}p" else "${bitrate / 1000} kbps"
}

/**
 * Playback of one stream by the platform's player (ExoPlayer, AVPlayer or libmpv).
 * Its state is Compose state, so the controls update on their own.
 */
abstract class VideoPlayerController {
    /** True when playback is wanted, even while waiting for data. */
    var isPlaying by mutableStateOf(false)
        protected set
    var isBuffering by mutableStateOf(true)
        protected set
    var positionMs by mutableLongStateOf(0L)
        protected set

    /** 0 until the duration is known. */
    var durationMs by mutableLongStateOf(0L)
        protected set

    /** How far ahead of the start the player has downloaded. */
    var bufferedMs by mutableLongStateOf(0L)
        protected set
    var speed by mutableFloatStateOf(1f)
        protected set
    var isMuted by mutableStateOf(false)
        protected set
    var isEnded by mutableStateOf(false)
        protected set

    /** A message for the user when playback failed. */
    var error by mutableStateOf<String?>(null)
        protected set

    /** The stream's qualities, best first. Empty when there is only one. */
    var qualities by mutableStateOf<List<VideoQuality>>(emptyList())
        protected set

    /** The quality the user picked, or null for automatic. */
    var selectedQuality by mutableStateOf<VideoQuality?>(null)
        protected set

    /** Height of the video currently playing, or 0 if unknown. */
    var videoHeight by mutableIntStateOf(0)
        protected set

    abstract fun play()
    abstract fun pause()
    abstract fun seekTo(positionMs: Long)
    abstract fun setPlaybackSpeed(speed: Float)
    abstract fun setVolumeMuted(muted: Boolean)

    /** Plays [quality], or picks automatically when it is null. */
    abstract fun selectQuality(quality: VideoQuality?)

    fun togglePlay() = if (isPlaying && !isEnded) pause() else play()

    fun seekBy(deltaMs: Long) {
        val max = if (durationMs > 0) durationMs else Long.MAX_VALUE
        seekTo((positionMs + deltaMs).coerceIn(0L, max))
    }
}

/** Creates a player for [stream] that starts playing right away and is released with the composition. */
@Composable
expect fun rememberVideoPlayerController(stream: CapturedStream, title: String): VideoPlayerController

/** Draws the video of [controller], scaled according to [fit]. */
@Composable
expect fun VideoSurface(controller: VideoPlayerController, fit: VideoFit, modifier: Modifier = Modifier)

/** Window fullscreen, on platforms where the app runs in a resizable window. */
class FullscreenController(val isFullscreen: Boolean, val toggle: () -> Unit)

/** Null on platforms where the player is always fullscreen. */
@Composable
expect fun rememberFullscreenController(): FullscreenController?

/** Phone-style fullscreen while the player is shown: system bars hidden, landscape, screen kept on. */
@Composable
expect fun PlayerSystemUiEffect()

/** True where the player is used with a mouse and keyboard rather than touch. */
expect val playerUsesMouse: Boolean
