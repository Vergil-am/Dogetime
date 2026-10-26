package dev.dogetime.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.dogetime.core.UiState
import dev.dogetime.domain.MediaItem
import dev.dogetime.ui.components.ErrorBox
import dev.dogetime.ui.components.LoadingBox
import dev.dogetime.ui.components.MediaRow

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
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp + bottomInset),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            item {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            if (favorites.isNotEmpty()) {
                item {
                    MediaRow("Your Favorites", favorites, onOpen)
                }
            }
            item {
                MediaRow("Popular Movies", current.data.popularMovies, onOpen)
            }
            item {
                MediaRow("Popular TV Shows", current.data.popularTv, onOpen)
            }
            item {
                MediaRow("Trending Movies This Week", current.data.trendingMovies, onOpen)
            }
            item {
                MediaRow("Trending TV Shows This Week", current.data.trendingTv, onOpen)
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
