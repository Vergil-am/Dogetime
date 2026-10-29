package dev.dogetime.ui.watch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.dogetime.core.UiState
import dev.dogetime.core.runForUiState
import dev.dogetime.data.TorrentProvider
import dev.dogetime.data.TorrentioApi
import dev.dogetime.domain.TorrentOption
import dev.dogetime.domain.WatchTarget
import dev.dogetime.ui.components.dragToScroll

/** The quality groups the torrent list is filtered by. */
private enum class QualityGroup(val label: String) {
    UltraHd("4K"),
    FullHd("Full HD"),
    Hd("HD"),
    Sd("SD"),
    Unknown("Unknown"),
}

/** Groups quality labels: "4k HDR" is 4K, "720p" is HD, "480p" is SD, and "DVDRip" or no label is Unknown. */
private val TorrentOption.group: QualityGroup
    get() = when (quality.substringBefore(' ').lowercase()) {
        "4k", "2160p" -> QualityGroup.UltraHd
        "1080p" -> QualityGroup.FullHd
        "720p" -> QualityGroup.Hd
        "576p", "480p", "360p", "240p" -> QualityGroup.Sd
        else -> QualityGroup.Unknown
    }

/**
 * Torrents listed for [target], best quality first, filtered by [QualityGroup]
 * (Full HD at first); picking one plays it.
 */
@Composable
internal fun TorrentPicker(
    target: WatchTarget,
    torrentio: TorrentioApi,
    title: String,
    backdropUrl: String?,
    onBack: () -> Unit,
    onChangeSource: () -> Unit,
    onPick: (TorrentOption) -> Unit,
    modifier: Modifier = Modifier,
    initialProvider: TorrentProvider = TorrentProvider.Torrentio,
) {
    var provider by remember(target, initialProvider) { mutableStateOf(initialProvider) }
    var options by remember(target, provider) { mutableStateOf<UiState<List<TorrentOption>>>(UiState.Loading) }
    // "Try again" bumps this to fetch the list again.
    var attempt by remember(target, provider) { mutableIntStateOf(0) }
    var group by remember(target, provider) { mutableStateOf(QualityGroup.FullHd) }
    LaunchedEffect(target, attempt, provider) {
        options = UiState.Loading
        options = runForUiState { torrentio.streams(target, provider) }
    }

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (backdropUrl != null) BlurredBackdrop(backdropUrl, Modifier.fillMaxSize())
        WatchPage(title = title, pageShown = false, onBack = onBack, onChangeSource = onChangeSource) {
            Column(Modifier.fillMaxSize()) {
                ProviderChips(selected = provider, onSelect = { provider = it })
                when (val state = options) {
                    UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    is UiState.Error -> Message(state.message, onRetry = { attempt++ })
                    is UiState.Success -> if (state.data.isEmpty()) {
                        Message("No torrents found on ${provider.displayName} for this title.", onRetry = { attempt++ })
                    } else {
                        val shown = remember(state.data, group) { state.data.filter { it.group == group } }
                        QualityChips(selected = group, onSelect = { group = it })
                        if (shown.isEmpty()) {
                            Text(
                                text = "No ${group.label} torrents on ${provider.displayName} for this title.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                            )
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(shown) { option ->
                                TorrentRow(option, onClick = { onPick(option) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderChips(selected: TorrentProvider, onSelect: (TorrentProvider) -> Unit) {
    val rowState = rememberLazyListState()
    LazyRow(
        state = rowState,
        modifier = Modifier.fillMaxWidth().dragToScroll(rowState),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        items(TorrentProvider.entries) { provider ->
            FilterChip(
                selected = provider == selected,
                onClick = { onSelect(provider) },
                label = { Text(provider.displayName) },
            )
        }
    }
}

@Composable
private fun QualityChips(selected: QualityGroup, onSelect: (QualityGroup) -> Unit) {
    val rowState = rememberLazyListState()
    LazyRow(
        state = rowState,
        modifier = Modifier.fillMaxWidth().dragToScroll(rowState),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        items(QualityGroup.entries) { group ->
            FilterChip(
                selected = group == selected,
                onClick = { onSelect(group) },
                label = { Text(group.label) },
            )
        }
    }
}

@Composable
private fun TorrentRow(option: TorrentOption, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = option.quality,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
            Text(
                text = listOfNotNull(
                    option.seeders?.let { if (it == 1) "1 seeder" else "$it seeders" },
                    option.size,
                    option.site,
                ).joinToString(" · "),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = option.release,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        // A season pack: say which file in it is this episode.
        option.fileName?.takeIf { it.substringBeforeLast('.') != option.release.substringBeforeLast('.') }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun Message(text: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(text, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 360.dp))
        Button(onClick = onRetry) {
            Text("Try again")
        }
    }
}
