package dev.dogetime.ui.watch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import dev.dogetime.core.CapturedStream
import dev.dogetime.core.ExternalPlayer
import dev.dogetime.core.Torrents
import dev.dogetime.data.SubdlApi
import dev.dogetime.data.TorrentioApi
import dev.dogetime.core.WebEngineGate
import dev.dogetime.domain.VideoSource
import dev.dogetime.domain.WatchTarget
import dev.dogetime.ui.player.VideoPlayerScreen
import kotlinx.coroutines.delay

/** How long the auto-play script gets to start the video before the page is shown. */
private const val AUTO_PLAY_TIMEOUT_MS = 20_000L
private const val MESSAGE_DURATION_MS = 4_000L

/**
 * Watches [target]: the user picks a [VideoSource] first, then that source's page is played with
 * [SourcePlayer], which can go back to the source menu if that source doesn't work. Torrents are
 * picked from [torrentio]'s list for [target] and played with [MagnetPlayer].
 */
@Composable
fun WatchScreen(
    target: WatchTarget,
    title: String,
    torrentio: TorrentioApi,
    subdl: SubdlApi? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    backdropUrl: String? = null,
) {
    var source by remember(target) { mutableStateOf<VideoSource?>(null) }
    var browsingTorrents by remember(target) { mutableStateOf(false) }
    var torrent by remember(target) { mutableStateOf<TorrentPick?>(null) }
    val pickedTorrent = torrent
    if (pickedTorrent != null) {
        key(pickedTorrent) {
            MagnetPlayer(
                pick = pickedTorrent,
                title = title,
                onBack = onBack,
                // Back to the torrent list.
                onChangeSource = { torrent = null },
                modifier = modifier,
                backdropUrl = backdropUrl,
                target = target,
                subdl = subdl,
            )
        }
        return
    }
    if (browsingTorrents) {
        TorrentPicker(
            target = target,
            torrentio = torrentio,
            title = title,
            backdropUrl = backdropUrl,
            onBack = onBack,
            onChangeSource = { browsingTorrents = false },
            onPick = { torrent = TorrentPick(it.magnet, it.fileName, it.fileIndex) },
            modifier = modifier,
        )
        return
    }
    val chosen = source
    if (chosen == null) {
        SourcePicker(
            title = title,
            backdropUrl = backdropUrl,
            onBack = onBack,
            onPick = { source = it },
            // Torrentio looks titles up by IMDb id, which a few TMDB titles don't have.
            onPickTorrents = if (Torrents.isSupported && target.imdbId != null) {
                { browsingTorrents = true }
            } else {
                null
            },
            modifier = modifier,
        )
        return
    }
    // Keyed so a newly picked source starts with a fresh page and no captured stream.
    key(chosen) {
        SourcePlayer(
            url = chosen.url(target),
            title = title,
            onBack = onBack,
            onChangeSource = { source = null },
            modifier = modifier,
            backdropUrl = backdropUrl,
            target = target,
            subdl = subdl,
        )
    }
}

/** The menu of [VideoSource]s and torrents, over the title's backdrop. */
@Composable
private fun SourcePicker(
    title: String,
    backdropUrl: String?,
    onBack: () -> Unit,
    onPick: (VideoSource) -> Unit,
    onPickTorrents: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (backdropUrl != null) BlurredBackdrop(backdropUrl, Modifier.fillMaxSize())
        WatchPage(title = title, pageShown = false, onBack = onBack) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            ) {
                Text("Choose a source", style = MaterialTheme.typography.titleMedium)
                VideoSource.entries.forEach { source ->
                    Button(
                        onClick = { onPick(source) },
                        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
                    ) {
                        Text(source.label)
                    }
                }
                onPickTorrents?.let {
                    Button(
                        onClick = it,
                        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth(),
                    ) {
                        Text("Torrent")
                    }
                }
            }
        }
    }
}

/**
 * Plays [url]: the embed page runs hidden behind a loading screen while its player is started,
 * and the stream it requests is played in the app's own player. Where streams can't be captured
 * (iOS), the page itself is shown and plays the video.
 */
@Composable
private fun SourcePlayer(
    url: String,
    title: String,
    onBack: () -> Unit,
    onChangeSource: () -> Unit,
    modifier: Modifier = Modifier,
    backdropUrl: String? = null,
    target: WatchTarget? = null,
    subdl: SubdlApi? = null,
) {
    val allowedHost = remember(url) { hostOf(url) }
    val webState = rememberWebViewState(url)
    // Block redirects and pop-unders that try to take the main page off the player's site.
    val navigator = rememberWebViewNavigator(
        requestInterceptor = remember(allowedHost) { PlayerRequestInterceptor(allowedHost) },
    )
    // Embedded players need JavaScript, DOM storage and autoplay to work.
    webState.webSettings.apply {
        isJavaScriptEnabled = true
        androidWebSettings.domStorageEnabled = true
        androidWebSettings.mediaPlaybackRequiresUserGesture = false
        iOSWebSettings.mediaPlaybackRequiresUserGesture = false
        desktopWebSettings.disablePopupWindows = true
    }

    // Desktop and Android: the first stream the page requests is played in the app's player.
    var stream by remember(url) { mutableStateOf<CapturedStream?>(null) }
    var message by remember(url) { mutableStateOf<String?>(null) }
    LaunchedEffect(message) {
        if (message != null) {
            delay(MESSAGE_DURATION_MS)
            message = null
        }
    }

    val found = stream
    if (found != null) {
        Box(modifier.fillMaxSize()) {
            VideoPlayerScreen(
                stream = found,
                title = title,
                onBack = onBack,
                onOpenExternal = ExternalPlayer.name?.let {
                    { message = ExternalPlayer.play(found, title) ?: "Opening in $it…" }
                },
                target = target,
                subdl = subdl,
            )
            message?.let { MessageChip(it, Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp)) }
        }
        return
    }

    // The page is kept hidden behind a loading screen while the auto-play script starts the video.
    // It is shown if that takes too long (or on request), so the user can press play or solve a
    // challenge themselves.
    var pageShown by remember(url) { mutableStateOf(!ExternalPlayer.isSupported) }
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (!pageShown && backdropUrl != null) {
            BlurredBackdrop(backdropUrl, Modifier.fillMaxSize())
        }
        WatchPage(
            title = title,
            pageShown = pageShown,
            onBack = onBack,
            onChangeSource = onChangeSource,
        ) {
            WebEngineGate {
                LaunchedEffect(Unit) {
                    delay(AUTO_PLAY_TIMEOUT_MS)
                    pageShown = true
                }
                // The page keeps running while hidden: removing it would stop the player from
                // loading. The desktop browser draws its own native window, which can't be made
                // transparent or covered, so the page is also shrunk to a single dot.
                PlayerWebView(
                    state = webState,
                    navigator = navigator,
                    allowedHost = allowedHost,
                    onStreamFound = { captured -> if (stream == null) stream = captured },
                    modifier = if (pageShown) Modifier.fillMaxSize() else Modifier.size(1.dp).alpha(0f),
                )
                if (!pageShown) {
                    LoadingScreen(onShowPage = { pageShown = true }, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

/**
 * Back button and title above [content], which fills the rest of the screen. With
 * [onChangeSource], there is also a button back to the source menu.
 */
@Composable
internal fun WatchPage(
    title: String,
    pageShown: Boolean,
    onBack: () -> Unit,
    onChangeSource: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) {
                Text("← Back")
            }
            onChangeSource?.let {
                TextButton(onClick = it) {
                    Text("Change source")
                }
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        if (pageShown && ExternalPlayer.isSupported) {
            Text(
                text = "Press play in the player below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            content()
        }
    }
}

/** The title's backdrop, blurred and darkened, behind the loading screen. */
@Composable
internal fun BlurredBackdrop(url: String, modifier: Modifier = Modifier) {
    val background = MaterialTheme.colorScheme.background
    Box(modifier) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(32.dp),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to background.copy(alpha = 0.55f),
                        0.5f to background.copy(alpha = 0.35f),
                        1f to background.copy(alpha = 0.8f),
                    ),
                ),
        )
    }
}

@Composable
private fun LoadingScreen(onShowPage: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator()
            Text("Finding the video…")
            TextButton(onClick = onShowPage) {
                Text("Show page")
            }
        }
    }
}

@Composable
internal fun MessageChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.inverseOnSurface,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}
