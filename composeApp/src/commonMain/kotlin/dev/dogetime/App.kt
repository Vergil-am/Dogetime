package dev.dogetime

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import dev.dogetime.navigation.Destination
import dev.dogetime.navigation.Navigator
import dev.dogetime.ui.browse.BrowseScreen
import dev.dogetime.ui.browse.BrowseViewModel
import dev.dogetime.ui.details.DetailsScreen
import dev.dogetime.ui.home.HomeScreen
import dev.dogetime.ui.home.HomeViewModel
import dev.dogetime.ui.search.SearchScreen
import dev.dogetime.ui.search.SearchViewModel
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

private val tabs = listOf(
    Tab(Destination.Home, "Home", HomeIcon),
    Tab(Destination.Browse, "Browse", BrowseIcon),
    Tab(Destination.Search, "Search", SearchIcon),
)

private val NavBarHeight = 64.dp
private val NavBarMargin = 16.dp
private val NavBarShape = RoundedCornerShape(NavBarHeight / 2)

@OptIn(ExperimentalHazeApi::class)
private val NavBarGlass = GlassStyle.regular.then {
    shape(NavBarShape)
    tint(Mocha.Base.copy(alpha = 0.35f))
}

@Composable
fun App() {
    DogeTimeTheme {
        // Coil needs a network fetcher before the first poster loads.
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory()) }
                .build()
        }

        val container = remember { AppContainer() }
        val navigator = remember { Navigator() }

        // Tab ViewModels live here so switching tabs keeps their state.
        val homeViewModel = remember { HomeViewModel(container.repository, container.favorites) }
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

        Scaffold { innerPadding ->
            // Tab screens draw under the floating bar (so the glass has something to blur)
            // and pad their scroll content instead.
            // Details draws its backdrop under the status bar and places the back arrow itself.
            // The watch screen's player fills the whole screen, so it handles insets itself.
            val modifier = when {
                showTabs -> Modifier.padding(top = innerPadding.calculateTopPadding())
                current is Destination.Details -> Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                current is Destination.Watch -> Modifier
                else -> Modifier.padding(innerPadding)
            }
            val bottomInset = innerPadding.calculateBottomPadding() + NavBarHeight + NavBarMargin * 2
            val openDetails = { type: dev.dogetime.domain.MediaType, id: Int ->
                navigator.open(Destination.Details(type, id))
            }

            Box(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().hazeSource(hazeState)) {
                    when (current) {
                        Destination.Home -> HomeScreen(
                            viewModel = homeViewModel,
                            onOpen = { openDetails(it.type, it.id) },
                            modifier = modifier,
                            bottomInset = bottomInset,
                        )
                        Destination.Browse -> BrowseScreen(
                            viewModel = browseViewModel,
                            onOpen = { openDetails(it.type, it.id) },
                            modifier = modifier,
                            bottomInset = bottomInset,
                        )
                        Destination.Search -> SearchScreen(
                            viewModel = searchViewModel,
                            onOpen = { openDetails(it.type, it.id) },
                            modifier = modifier,
                            bottomInset = bottomInset,
                        )
                        is Destination.Details -> DetailsScreen(
                            type = current.type,
                            id = current.id,
                            repository = container.repository,
                            favorites = container.favorites,
                            onBack = { navigator.back() },
                            onWatch = { target, title, backdropUrl -> navigator.open(Destination.Watch(target, title, backdropUrl)) },
                            onOpen = { openDetails(it.type, it.id) },
                            modifier = modifier,
                        )
                        is Destination.Watch -> WatchScreen(
                            target = current.target,
                            title = current.title,
                            torrentio = container.torrentio,
                            subdl = container.subdl,
                            backdropUrl = current.backdropUrl,
                            onBack = { navigator.back() },
                            modifier = modifier,
                        )
                    }
                }

                if (showTabs) {
                    FloatingNavBar(
                        root = navigator.root,
                        onSelect = navigator::selectTab,
                        hazeState = hazeState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = innerPadding.calculateBottomPadding() + NavBarMargin),
                    )
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(NavBarHeight)
            .hazeGlass(input = HazeInput.Sources(hazeState), style = NavBarGlass)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            val selected = root == tab.destination
            val pill by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent,
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(NavBarShape)
                    .background(pill)
                    .clickable { onSelect(tab.destination) }
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(tab.icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                )
            }
        }
    }
}
