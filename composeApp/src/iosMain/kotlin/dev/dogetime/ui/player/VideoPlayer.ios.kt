package dev.dogetime.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import dev.dogetime.core.CapturedStream
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFoundation.AVLayerVideoGravityResize
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusPaused
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.CMTimeRangeValue
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.loadedTimeRanges
import platform.AVFoundation.muted
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.preferredMaximumResolution
import platform.AVFoundation.preferredPeakBitRate
import platform.AVFoundation.presentationSize
import platform.AVFoundation.rate
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.setMuted
import platform.AVFoundation.setRate
import platform.AVFoundation.timeControlStatus
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.CoreMedia.CMTimeRangeGetEnd
import platform.Foundation.NSURL
import platform.Foundation.NSValue
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIView

actual val playerUsesMouse: Boolean = false

/** Plays HLS streams with AVPlayer, sending the headers the stream's server expects. */
@OptIn(ExperimentalForeignApi::class)
internal class AvVideoPlayerController(stream: CapturedStream) : VideoPlayerController() {

    val player: AVPlayer
    private val timeObserver: Any?
    private var wantedSpeed = 1f

    init {
        // Plays sound even when the silent switch is on, like other video apps.
        AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback, null)
        val url = requireNotNull(NSURL.URLWithString(stream.url)) { "Invalid stream URL" }
        // Not a public constant, but the documented way AVFoundation accepts request headers.
        val asset = AVURLAsset.URLAssetWithURL(url, mapOf<Any?, Any?>("AVURLAssetHTTPHeaderFieldsKey" to stream.headers))
        player = AVPlayer.playerWithPlayerItem(AVPlayerItem.playerItemWithAsset(asset))
        timeObserver = player.addPeriodicTimeObserverForInterval(CMTimeMakeWithSeconds(0.25, 600), null) { _ ->
            updateState()
        }
        player.play()
        isPlaying = true
    }

    private fun updateState() {
        val item = player.currentItem
        if (item?.status == AVPlayerItemStatusFailed) {
            error = "The stream couldn't be played (${item.error?.localizedDescription})."
        }
        val status = player.timeControlStatus
        isPlaying = status != AVPlayerTimeControlStatusPaused
        isBuffering = error == null && status == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
        positionMs = player.currentTime().toMs()
        durationMs = item?.duration?.toMs() ?: 0L
        bufferedMs = (item?.loadedTimeRanges?.lastOrNull() as? NSValue)
            ?.CMTimeRangeValue
            ?.let { CMTimeRangeGetEnd(it).toMs() } ?: 0L
        isEnded = durationMs > 0 && positionMs >= durationMs - 500 && status == AVPlayerTimeControlStatusPaused
        isMuted = player.muted
        videoHeight = item?.presentationSize?.useContents { height.toInt() } ?: 0
    }

    /**
     * AVPlayer picks variants itself, so a quality is applied as a ceiling on resolution and
     * bitrate, which makes it play that variant (or a lower one if the network can't keep up).
     */
    override fun selectQuality(quality: VideoQuality?) {
        val item = player.currentItem ?: return
        if (quality == null) {
            item.preferredMaximumResolution = CGSizeMake(0.0, 0.0)
            item.preferredPeakBitRate = 0.0
        } else {
            item.preferredMaximumResolution = CGSizeMake(Double.MAX_VALUE, quality.height.toDouble())
            item.preferredPeakBitRate = quality.bitrate.toDouble()
        }
        selectedQuality = quality
    }

    fun setAvailableQualities(available: List<VideoQuality>) {
        qualities = available
    }

    override fun play() {
        if (isEnded) seekTo(0)
        player.play()
        player.setRate(wantedSpeed)
    }

    override fun pause() = player.pause()

    override fun seekTo(positionMs: Long) {
        player.seekToTime(CMTimeMakeWithSeconds(positionMs / 1000.0, 600))
        this.positionMs = positionMs
    }

    override fun setPlaybackSpeed(speed: Float) {
        wantedSpeed = speed
        this.speed = speed
        if (player.rate != 0f) player.setRate(speed)
    }

    override fun setVolumeMuted(muted: Boolean) {
        player.setMuted(muted)
        isMuted = muted
    }

    fun release() {
        timeObserver?.let(player::removeTimeObserver)
        player.pause()
        player.replaceCurrentItemWithPlayerItem(null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun kotlinx.cinterop.CValue<platform.CoreMedia.CMTime>.toMs(): Long {
    val seconds = CMTimeGetSeconds(this)
    return if (seconds.isNaN() || seconds.isInfinite()) 0L else (seconds * 1000).toLong()
}

@Composable
actual fun rememberVideoPlayerController(stream: CapturedStream, title: String): VideoPlayerController {
    val controller = remember(stream) { AvVideoPlayerController(stream) }
    DisposableEffect(controller) {
        onDispose { controller.release() }
    }
    LaunchedEffect(controller) {
        controller.setAvailableQualities(fetchHlsQualities(stream))
    }
    return controller
}

/** Hosts an AVPlayerLayer, resized with the view. */
@OptIn(ExperimentalForeignApi::class)
private class PlayerLayerView(player: AVPlayer) : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    val playerLayer: AVPlayerLayer = AVPlayerLayer.playerLayerWithPlayer(player)

    init {
        backgroundColor = UIColor.blackColor
        layer.addSublayer(playerLayer)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        playerLayer.frame = bounds
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoSurface(controller: VideoPlayerController, fit: VideoFit, modifier: Modifier) {
    val av = controller as AvVideoPlayerController
    UIKitView(
        factory = { PlayerLayerView(av.player) },
        update = { view ->
            view.playerLayer.videoGravity = when (fit) {
                VideoFit.Fit -> AVLayerVideoGravityResizeAspect
                VideoFit.Fill -> AVLayerVideoGravityResizeAspectFill
                VideoFit.Stretch -> AVLayerVideoGravityResize
            }
        },
        modifier = modifier,
    )
}

/** The player always fills the screen on phones. */
@Composable
actual fun rememberFullscreenController(): FullscreenController? = null

@Composable
actual fun PlayerSystemUiEffect() {
    // Keeps the screen on while the player is shown.
    DisposableEffect(Unit) {
        UIApplication.sharedApplication.idleTimerDisabled = true
        onDispose { UIApplication.sharedApplication.idleTimerDisabled = false }
    }
}
