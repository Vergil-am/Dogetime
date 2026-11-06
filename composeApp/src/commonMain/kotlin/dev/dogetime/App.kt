package dev.dogetime

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.dogetime.core.PlatformBackHandler
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import dev.dogetime.di.AppContainer
import dev.dogetime.domain.MediaItem
import dev.dogetime.navigation.Destination
import dev.dogetime.navigation.Navigator
import dev.dogetime.ui.browse.BrowseScreen
import dev.dogetime.ui.browse.BrowseViewModel
import dev.dogetime.ui.components.LocalNavAnimatedVisibilityScope
import dev.dogetime.ui.components.LocalSharedTransitionScope
import dev.dogetime.ui.details.DetailsScreen
import dev.dogetime.ui.home.HomeScreen
import dev.dogetime.ui.home.HomeViewModel
import dev.dogetime.ui.search.SearchScreen
import dev.dogetime.ui.search.SearchViewModel
import dev.dogetime.ui.settings.SettingsScreen
import dev.dogetime.ui.watch.WatchScreen

private data class Tab(val destination: Destination, val label: String, val icon: ImageVector)

/** Thin outline icons, tinted by the tab's selection color. */
private fun lineIcon(name: String, pathBuilder: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathBuilder = pathBuilder,
    ).build()

private val HomeIcon = lineIcon("Home") {
    // House outline with a door.
    moveTo(4f, 10.5f)
    lineTo(12f, 4f)
    lineTo(20f, 10.5f)
    verticalLineTo(19.5f)
    horizontalLineTo(14.5f)
    verticalLineTo(14.5f)
    horizontalLineTo(9.5f)
    verticalLineTo(19.5f)
    horizontalLineTo(4f)
    close()
}

private val BrowseIcon = lineIcon("Browse") {
    // Compass: a circle with a tilted needle.
    moveTo(21f, 12f)
    arcTo(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 3f, y1 = 12f)
    arcTo(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 21f, y1 = 12f)
    close()
    moveTo(15.5f, 8.5f)
    lineTo(13.5f, 13.5f)
    lineTo(8.5f, 15.5f)
    lineTo(10.5f, 10.5f)
    close()
}

private val SearchIcon = lineIcon("Search") {
    // Magnifying glass.
    moveTo(17.5f, 11f)
    arcTo(6.5f, 6.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 4.5f, y1 = 11f)
    arcTo(6.5f, 6.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 17.5f, y1 = 11f)
    close()
    moveTo(15.8f, 15.8f)
    lineTo(20f, 20f)
}

private val SettingsIcon = lineIcon("Settings") {
    // Sliders / settings icon
    moveTo(4f, 6f)
    lineTo(20f, 6f)
    moveTo(14f, 3.5f)
    verticalLineTo(8.5f)

    moveTo(4f, 12f)
    lineTo(20f, 12f)
    moveTo(8f, 9.5f)
    verticalLineTo(14.5f)

    moveTo(4f, 18f)
    lineTo(20f, 18f)
    moveTo(16f, 15.5f)
    verticalLineTo(20.5f)
}

private val tabs = listOf(
    Tab(Destination.Home, "Home", HomeIcon),
    Tab(Destination.Browse, "Browse", BrowseIcon),
    Tab(Destination.Search, "Search", SearchIcon),
    Tab(Destination.Settings, "Settings", SettingsIcon),
)

private val NavBarHeight = 64.dp
private val NavBarWidth = 68.dp
private val NavBarMargin = 16.dp
private val NavRailEndMargin = 32.dp
private val NavBarShape = RoundedCornerShape(NavBarHeight / 2)
private val NavRailShape = RoundedCornerShape(NavBarWidth / 2)

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun rememberNavBarGlass(): GlassStyle {
    val bg = MaterialTheme.colorScheme.surface
    return remember(bg) {
        GlassStyle.regular.then {
            shape(NavBarShape)
            tint(bg.copy(alpha = 0.35f))
        }
    }
}

@Composable
fun App() {
    var currentTheme by remember { mutableStateOf(AppTheme.Midnight) }

    ChofoTheme(
        theme = currentTheme,
        onThemeChange = { currentTheme = it },
    ) {
        // Coil needs a network fetcher before the first poster loads.
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory()) }
                .build()
        }

        val container = remember { AppContainer() }
        val navigator = remember { Navigator() }

        // Tab ViewModels live here so switching tabs keeps their state.
        val homeViewModel = remember { HomeViewModel(container.repository, container.favorites, container.watchHistory) }
        val browseViewModel = remember { BrowseViewModel(container.repository) }
        val searchViewModel = remember { SearchViewModel(container.repository) }
        DisposableEffect(Unit) {
            onDispose {
                homeViewModel.clear()
                browseViewModel.clear()
                searchViewModel.clear()
            }
        }

        PlatformBackHandler(enabled = navigator.canGoBack) {
            navigator.back()
        }

        val current = navigator.current
        val showTabs = current !is Destination.Details && current !is Destination.Watch
        val hazeState = rememberHazeState()

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val isWide = maxWidth > 839.dp
            val startInset = if (isWide && showTabs) NavBarMargin + NavBarWidth + NavRailEndMargin else 0.dp

            Scaffold { innerPadding ->
                val bottomInset = if (isWide || !showTabs) 0.dp else innerPadding.calculateBottomPadding() + NavBarHeight + NavBarMargin * 2
                fun screenModifier(dest: Destination): Modifier = when {
                    dest !is Destination.Details && dest !is Destination.Watch -> Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(start = startInset)
                    dest is Destination.Details -> Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    dest is Destination.Watch -> Modifier
                    else -> Modifier.padding(innerPadding)
                }
                val openDetails = { item: MediaItem ->
                    navigator.open(Destination.Details(item.type, item.id, item))
                }

                Box(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().hazeSource(hazeState)) {
                        @OptIn(ExperimentalSharedTransitionApi::class)
                        SharedTransitionLayout {
                            AnimatedContent(
                                targetState = current,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                                },
                                label = "ScreenTransition",
                            ) { targetDestination ->
                                CompositionLocalProvider(
                                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                                    LocalNavAnimatedVisibilityScope provides this@AnimatedContent,
                                ) {
                                    val destModifier = screenModifier(targetDestination)
                                    when (targetDestination) {
                                        Destination.Home -> HomeScreen(
                                            viewModel = homeViewModel,
                                            onOpen = openDetails,
                                            modifier = destModifier,
                                            bottomInset = bottomInset,
                                            isWide = isWide,
                                            onNavigateToBrowse = { type, category ->
                                                browseViewModel.selectFilter(type, category)
                                                navigator.selectTab(Destination.Browse)
                                            },
                                        )
                                        Destination.Browse -> BrowseScreen(
                                            viewModel = browseViewModel,
                                            onOpen = openDetails,
                                            modifier = destModifier,
                                            bottomInset = bottomInset,
                                        )
                                        Destination.Search -> SearchScreen(
                                            viewModel = searchViewModel,
                                            onOpen = openDetails,
                                            modifier = destModifier,
                                            bottomInset = bottomInset,
                                        )
                                        Destination.Settings -> SettingsScreen(
                                            currentTheme = currentTheme,
                                            onThemeSelect = { currentTheme = it },
                                            modifier = destModifier,
                                            bottomInset = bottomInset,
                                        )
                                        is Destination.Details -> DetailsScreen(
                                            type = targetDestination.type,
                                            id = targetDestination.id,
                                            initialItem = targetDestination.initialItem,
                                            repository = container.repository,
                                            favorites = container.favorites,
                                            watchHistory = container.watchHistory,
                                            onBack = { navigator.back() },
                                            onWatch = { target, title, backdropUrl, mediaItem ->
                                                navigator.open(Destination.Watch(target, title, backdropUrl, mediaItem))
                                            },
                                            onOpen = openDetails,
                                            modifier = destModifier,
                                        )
                                        is Destination.Watch -> WatchScreen(
                                            target = targetDestination.target,
                                            title = targetDestination.title,
                                            torrentio = container.torrentio,
                                            subdl = container.subdl,
                                            openSubtitles = container.openSubtitles,
                                            ytsSubs = container.ytsSubs,
                                            stremioSubtitles = container.stremioSubtitles,
                                            kitsuMapping = container.kitsuMapping,
                                            watchHistory = container.watchHistory,
                                            backdropUrl = targetDestination.backdropUrl,
                                            mediaItem = targetDestination.mediaItem,
                                            onBack = { navigator.back() },
                                            modifier = destModifier,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showTabs) {
                        if (isWide) {
                            FloatingNavBar(
                                root = navigator.root,
                                onSelect = navigator::selectTab,
                                hazeState = hazeState,
                                isVertical = true,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = NavBarMargin),
                            )
                        } else {
                            FloatingNavBar(
                                root = navigator.root,
                                onSelect = navigator::selectTab,
                                hazeState = hazeState,
                                isVertical = false,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = innerPadding.calculateBottomPadding() + NavBarMargin),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun FloatingNavBar(
    root: Destination,
    onSelect: (Destination) -> Unit,
    hazeState: HazeState,
    isVertical: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isVertical) {
        Column(
            modifier = modifier
                .width(NavBarWidth)
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            tabs.forEach { tab ->
                NavBarTabItem(
                    tab = tab,
                    selected = root == tab.destination,
                    onSelect = { onSelect(tab.destination) },
                    isVertical = true,
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .height(NavBarHeight)
                .hazeGlass(input = HazeInput.Sources(hazeState), style = rememberNavBarGlass())
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                NavBarTabItem(
                    tab = tab,
                    selected = root == tab.destination,
                    onSelect = { onSelect(tab.destination) },
                    isVertical = false,
                )
            }
        }
    }
}

@Composable
private fun NavBarTabItem(
    tab: Tab,
    selected: Boolean,
    onSelect: () -> Unit,
    isVertical: Boolean,
) {
    val activeTheme = LocalActiveTheme.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val ghostWhite = Color(0xFFFBF9FF)
    val lavenderGrey = Color(0xFFA2A3BB)

    val iconColor by animateColorAsState(
        when {
            activeTheme == AppTheme.Midnight -> when {
                isHovered || selected -> ghostWhite
                else -> lavenderGrey
            }
            else -> when {
                isHovered || selected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        }
    )

    val pillColor by animateColorAsState(
        when {
            selected -> (if (activeTheme == AppTheme.Midnight) ghostWhite else MaterialTheme.colorScheme.primary).copy(alpha = 0.22f)
            isPressed -> (if (activeTheme == AppTheme.Midnight) ghostWhite else MaterialTheme.colorScheme.primary).copy(alpha = 0.16f)
            isHovered -> (if (activeTheme == AppTheme.Midnight) ghostWhite else MaterialTheme.colorScheme.primary).copy(alpha = 0.10f)
            else -> Color.Transparent
        }
    )

    val shape = if (isVertical) NavRailShape else NavBarShape

    val itemModifier = if (isVertical) {
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(pillColor)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect,
            )
            .padding(vertical = 14.dp, horizontal = 4.dp)
    } else {
        Modifier
            .fillMaxHeight()
            .clip(shape)
            .background(pillColor)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect,
            )
            .padding(horizontal = 14.dp)
    }

    Column(
        modifier = itemModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(tab.icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        if (isVertical) {
            Spacer(Modifier.height(4.dp))
        }
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = iconColor,
        )
    }
}
