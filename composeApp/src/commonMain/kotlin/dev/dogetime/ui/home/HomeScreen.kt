package dev.dogetime.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.dogetime.Mocha
import dev.dogetime.core.UiState
import dev.dogetime.domain.MediaItem
import dev.dogetime.ui.components.BackdropRow
import dev.dogetime.ui.components.ErrorBox
import dev.dogetime.ui.components.LoadingBox
import dev.dogetime.ui.components.MediaRow
import dev.dogetime.ui.components.RatingRing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpen: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    bottomInset: Dp = 0.dp,
) {
    val state by viewModel.state.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    when (val current = state) {
        UiState.Loading -> LoadingBox(modifier)
        is UiState.Error -> ErrorBox(current.message, onRetry = viewModel::load, modifier = modifier)
        is UiState.Success -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(bottom = 24.dp + bottomInset),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            // Featured hero banner — cycles through trending movies.
            val featured = current.data.trendingMovies.take(5)
            if (featured.isNotEmpty()) {
                item {
                    FeaturedBanner(featured, onOpen)
                }
            }

            if (favorites.isNotEmpty()) {
                item {
                    MediaRow("Your Favorites", favorites, onOpen)
                }
            }
            item {
                BackdropRow("Trending Movies This Week", current.data.trendingMovies, onOpen)
            }
            item {
                BackdropRow("Trending TV Shows This Week", current.data.trendingTv, onOpen)
            }
            item {
                MediaRow("Popular Movies", current.data.popularMovies, onOpen)
            }
            item {
                MediaRow("Popular TV Shows", current.data.popularTv, onOpen)
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

/**
 * Full-width hero banner that auto-pages through [items], showing the backdrop,
 * a gradient scrim, the title, rating ring, year, overview snippet, and dot indicators.
 * Supports mouse drag and scroll-wheel on desktop.
 */
@Composable
private fun FeaturedBanner(
    items: List<MediaItem>,
    onOpen: (MediaItem) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { items.size })
    val background = MaterialTheme.colorScheme.background
    val scope = rememberCoroutineScope()

    // Auto-advance every 6 seconds.
    LaunchedEffect(pagerState) {
        while (true) {
            delay(6_000)
            val next = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(next, animationSpec = tween(600))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                // Mouse click-and-drag → swipe between pages.
                .pointerInput(pagerState, items.size) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { totalDrag = 0f },
                        onDragEnd = {
                            val threshold = size.width * 0.15f
                            val target = when {
                                totalDrag < -threshold ->
                                    (pagerState.currentPage + 1).coerceAtMost(items.size - 1)
                                totalDrag > threshold ->
                                    (pagerState.currentPage - 1).coerceAtLeast(0)
                                else -> pagerState.currentPage
                            }
                            scope.launch {
                                pagerState.animateScrollToPage(target, animationSpec = tween(400))
                            }
                        },
                        onDragCancel = { totalDrag = 0f },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            totalDrag += dragAmount
                        },
                    )
                }
                // Mouse scroll-wheel → page forward / backward.
                .pointerInput(pagerState, items.size) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Scroll) {
                                val scrollY = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                                if (scrollY != 0f) {
                                    val target = if (scrollY > 0) {
                                        (pagerState.currentPage + 1).coerceAtMost(items.size - 1)
                                    } else {
                                        (pagerState.currentPage - 1).coerceAtLeast(0)
                                    }
                                    if (target != pagerState.currentPage) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                target,
                                                animationSpec = tween(400),
                                            )
                                        }
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        }
                    }
                },
        ) { page ->
            val item = items[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onOpen(item) },
            ) {
                // Backdrop image.
                val url = item.backdropUrl ?: item.posterUrl
                if (url != null) {
                    AsyncImage(
                        model = url,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // Cinematic gradient overlay.
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to background.copy(alpha = 0.1f),
                                0.4f to background.copy(alpha = 0.15f),
                                0.7f to background.copy(alpha = 0.7f),
                                1f to background,
                            ),
                        ),
                )

                // Content: title, rating, year, overview.
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, bottom = 40.dp),
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Mocha.Text,
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RatingRing(rating = item.rating, size = 40.dp, strokeWidth = 3.dp)
                        item.year?.let { year ->
                            Text(
                                text = year,
                                style = MaterialTheme.typography.labelLarge,
                                color = Mocha.Subtext0,
                            )
                        }
                    }

                    if (item.overview.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = item.overview,
                            style = MaterialTheme.typography.bodySmall,
                            color = Mocha.Subtext0,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        // Dot indicators.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(items.size) { index ->
                val isSelected = pagerState.currentPage == index
                val color by animateColorAsState(
                    targetValue = if (isSelected) Mocha.Mauve else Mocha.Surface2,
                    animationSpec = tween(300),
                )
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(color),
                )
            }
        }
    }
}
