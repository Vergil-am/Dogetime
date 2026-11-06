package dev.dogetime.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.dogetime.Mocha
import dev.dogetime.domain.MediaItem
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MediaPoster(
    url: String?,
    title: String,
    modifier: Modifier = Modifier,
    sharedKey: String? = null,
) {
    val sharedScope = LocalSharedTransitionScope.current
    val animScope = LocalNavAnimatedVisibilityScope.current

    val sharedModifier = if (sharedScope != null && animScope != null && sharedKey != null) {
        with(sharedScope) {
            Modifier.sharedElement(
                rememberSharedContentState(key = sharedKey),
                animatedVisibilityScope = animScope,
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(12.dp)),
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(sharedModifier)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
fun MediaCard(item: MediaItem, onClick: (MediaItem) -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.06f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
    )

    Column(
        modifier = modifier
            .width(140.dp)
            .zIndex(if (isHovered) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick(item) },
    ) {
        MediaPoster(
            url = item.posterUrl,
            title = item.title,
            sharedKey = "poster-${item.type}-${item.id}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        val meta = listOfNotNull(item.ratingText(), item.year).joinToString(" · ")
        if (meta.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = meta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** A landscape 16:9 card showing the backdrop with a title overlay at the bottom. */
@Composable
fun BackdropCard(item: MediaItem, onClick: (MediaItem) -> Unit, modifier: Modifier = Modifier) {
    val background = MaterialTheme.colorScheme.background
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.06f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
    )

    Box(
        modifier = modifier
            .width(260.dp)
            .aspectRatio(16f / 9f)
            .zIndex(if (isHovered) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick(item) },
    ) {
        val url = item.backdropUrl ?: item.posterUrl
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        // Bottom gradient scrim for text readability.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color.Transparent,
                        1f to background.copy(alpha = 0.9f),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Mocha.Text,
            )
            val meta = listOfNotNull(item.ratingText(), item.year).joinToString(" · ")
            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = Mocha.Subtext0,
                )
            }
        }
    }
}

/** A pill-shaped genre tag. */
@Composable
fun GenrePill(genre: String, modifier: Modifier = Modifier) {
    Text(
        text = genre,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

/**
 * A circular progress ring that fills to represent the rating (0-10 scale),
 * with the numeric score displayed in the centre.
 */
@Composable
fun RatingRing(
    rating: Double,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
) {
    if (rating <= 0) return

    val progress = (rating / 10.0).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
    )
    val ringColor = when {
        rating >= 7.0 -> Mocha.Green
        rating >= 5.0 -> Mocha.Peach
        else -> Mocha.Red
    }
    val trackColor = ringColor.copy(alpha = 0.2f)
    val score = oneDecimal(rating)

    Box(
        modifier = modifier
            .size(size)
            .background(Mocha.Crust.copy(alpha = 0.7f), CircleShape)
            .drawBehind {
                val stroke = strokeWidth.toPx()
                val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
                val topLeft = Offset(stroke / 2, stroke / 2)
                // Track
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                // Progress
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = score,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (size >= 48.dp) 14.sp else 10.sp,
            ),
            color = Color.White,
        )
    }
}

private val ChevronRightIcon = ImageVector.Builder(
    name = "ChevronRight",
    defaultWidth = 16.dp,
    defaultHeight = 16.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).path(
    stroke = SolidColor(Color.White),
    strokeLineWidth = 2.4f,
    strokeLineCap = StrokeCap.Round,
    strokeLineJoin = StrokeJoin.Round,
) {
    moveTo(9f, 18f)
    lineTo(15f, 12f)
    lineTo(9f, 6f)
}.build()

@Composable
fun MediaRow(
    title: String,
    mediaItems: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    onViewAll: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            if (onViewAll != null) {
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .hoverable(interactionSource)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onViewAll,
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        imageVector = ChevronRightIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        val rowState = rememberLazyListState()
        LazyRow(
            state = rowState,
            modifier = Modifier.dragToScroll(rowState),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(mediaItems, key = { "${it.type}-${it.id}" }) { item ->
                MediaCard(item = item, onClick = onItemClick)
            }
        }
    }
}

/** A row of landscape backdrop cards. */
@Composable
fun BackdropRow(
    title: String,
    mediaItems: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(8.dp))
        val rowState = rememberLazyListState()
        LazyRow(
            state = rowState,
            modifier = Modifier.dragToScroll(rowState),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(mediaItems, key = { "${it.type}-${it.id}" }) { item ->
                BackdropCard(item = item, onClick = onItemClick)
            }
        }
    }
}

/** Row of recently watched titles with episode coordinates and progress indicators. */
@Composable
fun ContinueWatchingRow(
    title: String,
    historyItems: List<dev.dogetime.domain.WatchHistoryItem>,
    onItemClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(8.dp))
        val rowState = rememberLazyListState()
        LazyRow(
            state = rowState,
            modifier = Modifier.dragToScroll(rowState),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(historyItems, key = { "${it.mediaItem.type}-${it.mediaItem.id}" }) { item ->
                ContinueWatchingCard(item = item, onClick = { onItemClick(item.mediaItem) })
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    item: dev.dogetime.domain.WatchHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.06f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
    )

    Column(
        modifier = modifier
            .width(140.dp)
            .zIndex(if (isHovered) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
        ) {
            MediaPoster(
                url = item.mediaItem.posterUrl ?: item.mediaItem.backdropUrl,
                title = item.mediaItem.title,
                sharedKey = "poster-${item.mediaItem.type}-${item.mediaItem.id}",
                modifier = Modifier.fillMaxSize(),
            )
            // Episode badge for series / anime
            if (item.season != null && item.episode != null) {
                Text(
                    text = "S${item.season} E${item.episode}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Mocha.Crust,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Mocha.Mauve)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
            // Progress bar at the bottom of the card
            if (item.progress > 0f) {
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
                    color = Mocha.Mauve,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.mediaItem.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Mocha.Text,
        )
        val subtitle = if (item.season != null && item.episode != null) {
            "Episode ${item.episode}"
        } else {
            val mins = (item.positionMs / 60_000L).coerceAtLeast(1)
            "$mins min watched"
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Mocha.Subtext0,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Adaptive poster grid. [footer] adds extra cells after the items, e.g. a loading spinner. */
@Composable
fun MediaGrid(
    mediaItems: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    bottomInset: Dp = 0.dp,
    footer: LazyGridScope.() -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        state = gridState,
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp + bottomInset),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(mediaItems, key = { "${it.type}-${it.id}" }) { item ->
            MediaCard(item = item, onClick = onItemClick)
        }
        footer()
    }
}

@Composable
fun ErrorBox(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

/** "★ 7.8" when the title has a rating, otherwise null. */
fun MediaItem.ratingText(): String? =
    if (rating > 0) "★ ${oneDecimal(rating)}" else null

fun oneDecimal(value: Double): String =
    ((value * 10).roundToInt() / 10.0).toString()
