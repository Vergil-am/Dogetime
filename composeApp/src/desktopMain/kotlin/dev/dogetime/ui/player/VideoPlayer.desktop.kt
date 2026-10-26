package dev.dogetime.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import dev.dogetime.core.CapturedStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** The app window's state, provided by main() so the player can go fullscreen. */
val LocalWindowState = staticCompositionLocalOf<WindowState?> { null }

actual val playerUsesMouse: Boolean = true

@Composable
actual fun rememberVideoPlayerController(stream: CapturedStream, title: String): VideoPlayerController {
    val controller = remember(stream) { MpvVideoPlayerController(stream, title) }
    DisposableEffect(controller) {
        onDispose { controller.release() }
    }
    LaunchedEffect(controller) {
        controller.setAvailableQualities(fetchHlsQualities(stream))
    }
    return controller
}

@Composable
actual fun VideoSurface(controller: VideoPlayerController, fit: VideoFit, modifier: Modifier) {
    val mpv = controller as MpvVideoPlayerController
    Canvas(modifier.clipToBounds()) {
        // Reading the frame number makes each new frame redraw this.
        mpv.frameVersion
        val aspect = mpv.videoAspect
        mpv.withFrame { image, width, height ->
            val videoAspect = if (aspect > 0f) aspect else width.toFloat() / height
            val (drawWidth, drawHeight) = when (fit) {
                VideoFit.Stretch -> size.width to size.height
                VideoFit.Fit, VideoFit.Fill -> {
                    val scale = if (fit == VideoFit.Fit) {
                        min(size.width / videoAspect, size.height)
                    } else {
                        max(size.width / videoAspect, size.height)
                    }
                    scale * videoAspect to scale
                }
            }
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(width, height),
                dstOffset = IntOffset(((size.width - drawWidth) / 2).roundToInt(), ((size.height - drawHeight) / 2).roundToInt()),
                dstSize = IntSize(drawWidth.roundToInt(), drawHeight.roundToInt()),
                filterQuality = FilterQuality.Medium,
            )
        }
    }
}

@Composable
actual fun rememberFullscreenController(): FullscreenController? {
    val windowState = LocalWindowState.current ?: return null
    // Leaving the player leaves fullscreen too.
    DisposableEffect(windowState) {
        onDispose {
            if (windowState.placement == WindowPlacement.Fullscreen) windowState.placement = WindowPlacement.Floating
        }
    }
    val isFullscreen = windowState.placement == WindowPlacement.Fullscreen
    return FullscreenController(isFullscreen) {
        windowState.placement = if (isFullscreen) WindowPlacement.Floating else WindowPlacement.Fullscreen
    }
}

@Composable
actual fun PlayerSystemUiEffect() = Unit
