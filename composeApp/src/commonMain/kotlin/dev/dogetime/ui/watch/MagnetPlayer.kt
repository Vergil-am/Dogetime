package dev.dogetime.ui.watch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.dogetime.core.ExternalPlayer
import dev.dogetime.core.TorrentProgress
import dev.dogetime.core.TorrentStream
import dev.dogetime.core.Torrents
import dev.dogetime.ui.player.VideoPlayerScreen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

private const val MESSAGE_DURATION_MS = 4_000L

/** A torrent to play: its magnet link, and which of its files to play (see [Torrents.open]). */
internal data class TorrentPick(val magnet: String, val fileName: String?, val fileIndex: Int?)

/**
 * Plays [pick]: the torrent's video file is downloaded in order while it plays, and streamed to
 * the app's player from a local server. Leaving the screen stops the download and deletes it.
 */
@Composable
internal fun MagnetPlayer(
    pick: TorrentPick,
    title: String,
    onBack: () -> Unit,
    onChangeSource: () -> Unit,
    modifier: Modifier = Modifier,
    backdropUrl: String? = null,
) {
    var torrent by remember { mutableStateOf<TorrentStream?>(null) }
    var progress by remember { mutableStateOf(TorrentProgress()) }
    var error by remember { mutableStateOf<String?>(null) }
    // "Try again" bumps this to open the magnet link again.
    var attempt by remember { mutableIntStateOf(0) }

    LaunchedEffect(attempt) {
        error = null
        progress = TorrentProgress()
        torrent = try {
            Torrents.open(pick.magnet, pick.fileName, pick.fileIndex) { progress = it }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.message ?: "The magnet link couldn't be opened."
            null
        }
    }
    val opened = torrent
    DisposableEffect(opened) {
        onDispose { opened?.close() }
    }

    var message by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(message) {
        if (message != null) {
            delay(MESSAGE_DURATION_MS)
            message = null
        }
    }

    if (opened != null) {
        Box(modifier.fillMaxSize()) {
            VideoPlayerScreen(
                stream = opened.stream,
                title = title,
                onBack = onBack,
                onOpenExternal = ExternalPlayer.name?.let {
                    { message = ExternalPlayer.play(opened.stream, title) ?: "Opening in $it…" }
                },
            )
            message?.let { MessageChip(it, Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp)) }
        }
        return
    }

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (backdropUrl != null) BlurredBackdrop(backdropUrl, Modifier.fillMaxSize())
        WatchPage(title = title, pageShown = false, onBack = onBack, onChangeSource = onChangeSource) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            ) {
                val failed = error
                if (failed != null) {
                    Text(failed, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 360.dp))
                    Button(onClick = { attempt++ }) {
                        Text("Try again")
                    }
                } else {
                    CircularProgressIndicator()
                    Text(
                        when {
                            progress.peers == 0 -> "Looking for peers…"
                            progress.peers == 1 -> "Connected to 1 peer, getting the file list…"
                            else -> "Connected to ${progress.peers} peers, getting the file list…"
                        },
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
