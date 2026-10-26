package dev.dogetime.ui.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.browseCategories
import dev.dogetime.ui.components.ErrorBox
import dev.dogetime.ui.components.MediaGrid
import dev.dogetime.ui.components.dragToScroll

@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onOpen: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    bottomInset: Dp = 0.dp,
) {
    val state by viewModel.state.collectAsState()
    val gridState = rememberLazyGridState()

    // Fetch the next page once the user scrolls within six rows of the end.
    val nearEnd by remember {
        derivedStateOf { gridState.isNearEnd() }
    }
    LaunchedEffect(nearEnd, state.items.size) {
        if (nearEnd) viewModel.loadMore()
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Browse",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            MediaType.entries.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = state.type == type,
                    onClick = { viewModel.selectType(type) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = MediaType.entries.size),
                ) {
                    Text(type.label)
                }
            }
        }

        val categoryRowState = rememberLazyListState()
        LazyRow(
            state = categoryRowState,
            modifier = Modifier.dragToScroll(categoryRowState),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(browseCategories(state.type), key = { it.path }) { category ->
                FilterChip(
                    selected = state.category == category,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category.label) },
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val error = state.error
            when {
                state.items.isNotEmpty() -> MediaGrid(
                    mediaItems = state.items,
                    onItemClick = onOpen,
                    gridState = gridState,
                    modifier = Modifier.fillMaxSize(),
                    bottomInset = bottomInset,
                    footer = {
                        if (state.isLoading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    },
                )
                error != null -> ErrorBox(error, onRetry = viewModel::reload)
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                else -> Text(
                    text = "Nothing to show here yet",
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

private fun LazyGridState.isNearEnd(): Boolean {
    val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    return lastVisible >= layoutInfo.totalItemsCount - 6
}
