package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.domain.model.Season
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeasonsTabs(
    seasons: List<Season>,
    id: Int,
    viewModel: DetailsViewModel,
    navController: NavController
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState (
        pageCount = { seasons.count() }
    )
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),

        ) {
        seasons.forEachIndexed { index, season ->
            Tab(
                selected = index == selectedTab,
                onClick = {
                    selectedTab = index
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = season.season_number.toString())
            }
        }

    }
        HorizontalPager(state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) {
                page ->
            Box(modifier = Modifier.fillMaxSize()
            ) {
                Episodes(
                    seasons[page],
                    viewModel,
                    id,
                    navController
                    )
            }
        }



}