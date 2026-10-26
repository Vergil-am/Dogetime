package dev.dogetime.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.dogetime.Mocha
import dev.dogetime.core.UiState
import dev.dogetime.data.FavoritesRepository
import dev.dogetime.data.MediaRepository
import dev.dogetime.domain.CastMember
import dev.dogetime.domain.Episode
import dev.dogetime.domain.MediaDetails
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.Season
import dev.dogetime.domain.WatchTarget
import dev.dogetime.domain.withImdbId
import dev.dogetime.ui.components.ErrorBox
import dev.dogetime.ui.components.MediaPoster
import dev.dogetime.ui.components.MediaRow
import dev.dogetime.ui.components.dragToScroll
import dev.dogetime.ui.components.oneDecimal
import dev.dogetime.ui.components.ratingText

@Composable
fun DetailsScreen(
    type: MediaType,
    id: Int,
    repository: MediaRepository,
    favorites: FavoritesRepository,
    onBack: () -> Unit,
    onWatch: (target: WatchTarget, title: String, backdropUrl: String?) -> Unit,
    onOpen: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Keyed by type and id, so opening a different title gets a fresh ViewModel.
    val viewModel = remember(type, id) { DetailsViewModel(repository, favorites, type, id) }
    DisposableEffect(viewModel) {
        onDispose { viewModel.clear() }
    }
    val state by viewModel.state.collectAsState()
    val episodes by viewModel.episodes.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val current = state) {
            UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is UiState.Error -> ErrorBox(current.message, onRetry = viewModel::load)
            is UiState.Success -> DetailsContent(
                details = current.data,
                episodes = episodes,
                onSeasonSelected = viewModel::selectSeason,
                // The watch screen shows the title's backdrop while the video loads.
                onWatch = { target, title -> onWatch(target.withImdbId(current.data.imdbId), title, current.data.item.backdropUrl) },
                isFavorite = isFavorite,
                onToggleFavorite = viewModel::toggleFavorite,
                onOpen = onOpen,
            )
        }

        // Floats over the backdrop; the scrim keeps it readable on bright images.
        IconButton(
            onClick = onBack,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Mocha.Crust.copy(alpha = 0.5f),
                contentColor = Mocha.Text,
            ),
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp),
        ) {
            Icon(BackArrow, contentDescription = "Back")
        }
    }
}

private val BackArrow = ImageVector.Builder(
    name = "BackArrow",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).path(
    stroke = SolidColor(Color.White),
    strokeLineWidth = 2.2f,
    strokeLineCap = StrokeCap.Round,
    strokeLineJoin = StrokeJoin.Round,
) {
    moveTo(19f, 12f)
    horizontalLineTo(5f)
    moveTo(11f, 6f)
    lineTo(5f, 12f)
    lineTo(11f, 18f)
}.build()

/** Width from which the episodes move next to the details instead of below them. */
private val TwoPaneMinWidth = 840.dp

/** Landscape phones get the side-by-side layout from this width. */
private val TwoPaneLandscapeMinWidth = 600.dp

@Composable
private fun DetailsContent(
    details: MediaDetails,
    episodes: EpisodesState,
    onSeasonSelected: (Int) -> Unit,
    onWatch: (target: WatchTarget, title: String) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpen: (MediaItem) -> Unit,
) {
    BoxWithConstraints {
        val wide = maxWidth >= TwoPaneMinWidth || (maxWidth > maxHeight && maxWidth >= TwoPaneLandscapeMinWidth)
        if (wide && details.seasons.isNotEmpty()) {
            WideDetails(details, episodes, onSeasonSelected, onWatch, isFavorite, onToggleFavorite, onOpen)
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                detailsSection(details, onWatch, isFavorite, onToggleFavorite)
                episodesSection(details, episodes, onSeasonSelected, onWatch)
                castSection(details)
                relatedSection(details, onOpen)
            }
        }
    }
}

private val EpisodesPanelShape = RoundedCornerShape(24.dp)

@OptIn(ExperimentalHazeApi::class)
private val EpisodesGlass = GlassStyle.regular.then {
    shape(EpisodesPanelShape)
    tint(Mocha.Base.copy(alpha = 0.35f))
}

/**
 * Wide screens: details on the left and episodes on the right, each scrolling on its own.
 * The backdrop spans the full width behind both, moving with the details as they scroll, and
 * the episodes sit on a frosted-glass panel that blurs it.
 */
@OptIn(ExperimentalHazeApi::class)
@Composable
private fun WideDetails(
    details: MediaDetails,
    episodes: EpisodesState,
    onSeasonSelected: (Int) -> Unit,
    onWatch: (target: WatchTarget, title: String) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpen: (MediaItem) -> Unit,
) {
    val hazeState = rememberHazeState()
    val detailsListState = rememberLazyListState()
    var heroHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .hazeSource(hazeState),
        ) {
            details.item.backdropUrl?.let { url ->
                Backdrop(
                    url = url,
                    title = details.item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) { heroHeight.toDp() })
                        .graphicsLayer {
                            // Scrolls away with the hero above it.
                            translationY = if (detailsListState.firstVisibleItemIndex == 0) {
                                -detailsListState.firstVisibleItemScrollOffset.toFloat()
                            } else {
                                -heroHeight.toFloat()
                            }
                        },
                )
            }
        }

        Row(Modifier.fillMaxSize()) {
            LazyColumn(
                state = detailsListState,
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.weight(1f).fillMaxHeight(),
            ) {
                detailsSection(
                    details = details,
                    onWatch = onWatch,
                    isFavorite = isFavorite,
                    onToggleFavorite = onToggleFavorite,
                    drawBackdrop = false,
                    onHeroHeightChanged = { heroHeight = it },
                )
                castSection(details)
                relatedSection(details, onOpen)
            }
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.End))
                    .padding(16.dp)
                    .clip(EpisodesPanelShape)
                    .hazeGlass(input = HazeInput.Sources(hazeState), style = EpisodesGlass),
            ) {
                episodesSection(details, episodes, onSeasonSelected, onWatch)
            }
        }
    }
}

/** The title's backdrop image, darkened toward the bottom so text stays readable and it fades into the page. */
@Composable
private fun Backdrop(url: String, title: String, modifier: Modifier = Modifier) {
    val background = MaterialTheme.colorScheme.background
    Box(modifier) {
        AsyncImage(
            model = url,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to background.copy(alpha = 0.35f),
                        0.3f to Color.Transparent,
                        0.65f to background.copy(alpha = 0.75f),
                        1f to background,
                    ),
                ),
        )
    }
}

/**
 * Backdrop with poster and title, the action buttons, genres and the overview. With
 * [drawBackdrop] false the hero leaves the backdrop to the caller, and reports its height
 * through [onHeroHeightChanged] so the caller can size it.
 */
private fun LazyListScope.detailsSection(
    details: MediaDetails,
    onWatch: (target: WatchTarget, title: String) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    drawBackdrop: Boolean = true,
    onHeroHeightChanged: (Int) -> Unit = {},
) {
    val media = details.item
    item {
        // Hero: the backdrop fills everything from the top of the screen down to the buttons.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { onHeroHeightChanged(it.height) },
        ) {
            if (drawBackdrop) {
                media.backdropUrl?.let { url -> Backdrop(url, media.title, Modifier.matchParentSize()) }
            }

            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    // Room for the back arrow, plus some open backdrop when there is one.
                    .padding(top = if (media.backdropUrl != null) 160.dp else 56.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    MediaPoster(
                        url = media.posterUrl,
                        title = media.title,
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(2f / 3f),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = media.title,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        val meta = listOfNotNull(media.year, details.runtime, details.status).joinToString(" · ")
                        if (meta.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = meta,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        media.ratingText()?.let { rating ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "$rating (${details.voteCount} votes)",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        details.tagline?.let { tagline ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (media.type == MediaType.Movie) {
                        Button(onClick = { onWatch(WatchTarget.Movie(media.id), media.title) }) {
                            Text("▶ Watch now")
                        }
                    }
                    OutlinedButton(onClick = onToggleFavorite) {
                        Text(if (isFavorite) "♥ Favorited" else "♡ Favorite")
                    }
                }
            }
        }
    }

    if (details.genres.isNotEmpty()) {
        item {
            Text(
                text = details.genres.joinToString("  •  "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }

    if (details.seasonCount != null && details.episodeCount != null) {
        item {
            Text(
                text = "${details.seasonCount} seasons · ${details.episodeCount} episodes",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }

    item {
        SectionTitle("Overview")
        Text(
            text = media.overview.ifBlank { "No overview available." },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

/** Season picker and the selected season's episodes. Nothing for movies. */
private fun LazyListScope.episodesSection(
    details: MediaDetails,
    episodes: EpisodesState,
    onSeasonSelected: (Int) -> Unit,
    onWatch: (target: WatchTarget, title: String) -> Unit,
) {
    if (details.seasons.isEmpty()) return
    item {
        SectionTitle("Seasons")
        SeasonChips(
            seasons = details.seasons,
            selectedSeason = episodes.seasonNumber,
            onSelect = onSeasonSelected,
        )
    }
    item {
        EpisodeList(
            showTitle = details.item.title,
            showId = details.item.id,
            episodes = episodes,
            onRetry = { episodes.seasonNumber?.let(onSeasonSelected) },
            onWatch = onWatch,
        )
    }
}

private fun LazyListScope.castSection(details: MediaDetails) {
    if (details.cast.isNotEmpty()) {
        item {
            SectionTitle("Cast")
            val castRowState = rememberLazyListState()
            LazyRow(
                state = castRowState,
                modifier = Modifier.dragToScroll(castRowState),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(details.cast, key = { it.name + it.character }) { member ->
                    CastCard(member)
                }
            }
        }
    }
}

/** Recommended and similar titles, as poster rows. Empty lists are left out. */
private fun LazyListScope.relatedSection(details: MediaDetails, onOpen: (MediaItem) -> Unit) {
    if (details.recommendations.isNotEmpty()) {
        item {
            MediaRow("You might also like", details.recommendations, onOpen, Modifier.padding(top = 24.dp))
        }
    }
    if (details.similar.isNotEmpty()) {
        item {
            MediaRow("Similar titles", details.similar, onOpen, Modifier.padding(top = 24.dp))
        }
    }
}

@Composable
private fun SeasonChips(
    seasons: List<Season>,
    selectedSeason: Int?,
    onSelect: (Int) -> Unit,
) {
    val rowState = rememberLazyListState()
    LazyRow(
        state = rowState,
        modifier = Modifier.dragToScroll(rowState),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(seasons, key = { it.number }) { season ->
            FilterChip(
                selected = selectedSeason == season.number,
                onClick = { onSelect(season.number) },
                label = { Text("${season.name} · ${season.episodeCount}") },
            )
        }
    }
}

@Composable
private fun EpisodeList(
    showTitle: String,
    showId: Int,
    episodes: EpisodesState,
    onRetry: () -> Unit,
    onWatch: (target: WatchTarget, title: String) -> Unit,
) {
    when (val content = episodes.content) {
        UiState.Loading -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        is UiState.Error -> Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(content.message, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        }
        // Rows carry 8dp of padding themselves, so their press highlight has room around the content.
        is UiState.Success -> Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            content.data.forEach { episode ->
                EpisodeRow(
                    episode = episode,
                    onWatch = {
                        val label = "$showTitle S${episode.seasonNumber}E${episode.number}"
                        onWatch(WatchTarget.Episode(showId, episode.seasonNumber, episode.number), label)
                    },
                )
            }
        }
    }
}

@Composable
private fun EpisodeRow(episode: Episode, onWatch: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onWatch)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MediaPoster(
            url = episode.stillUrl,
            title = episode.name,
            modifier = Modifier
                .width(140.dp)
                .aspectRatio(16f / 9f),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${episode.number}. ${episode.name}",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val meta = listOfNotNull(
                episode.airDate,
                episode.runtime,
                episode.rating.takeIf { it > 0 }?.let { "★ ${oneDecimal(it)}" },
            ).joinToString(" · ")
            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (episode.overview.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = episode.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "▶ Watch",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun CastCard(member: CastMember) {
    Column(modifier = Modifier.width(96.dp)) {
        MediaPoster(
            url = member.photoUrl,
            title = member.name,
            modifier = Modifier.size(96.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (member.character.isNotBlank()) {
            Text(
                text = member.character,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
