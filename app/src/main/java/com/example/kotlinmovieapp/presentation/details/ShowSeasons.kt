package com.example.kotlinmovieapp.presentation.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.presentation.components.Episodes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowSeasons(
    viewModel: DetailsViewModel,
    navController: NavController,
    id: Int
) {
    val seasons = viewModel.state.collectAsState().value.media?.seasons
    var selectedTab by remember { mutableIntStateOf(0) }
    if (seasons != null) {
        val pagerState = rememberPagerState(
            pageCount = { seasons.count() }
        )
        LaunchedEffect(selectedTab) {
            pagerState.animateScrollToPage(selectedTab)
        }
        LaunchedEffect(pagerState.currentPage) {
            selectedTab = pagerState.currentPage
        }
        Column (
            modifier = Modifier.fillMaxSize()
        ) {

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                ,

                ) {
                seasons.forEachIndexed { index, season ->
                    Tab(
                        selected = index == selectedTab,
                        onClick = {
                            selectedTab = index
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(text = "Season ${season.season_number}")
                    }
                }

            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Episodes(
                        season = seasons[page].season_number,
                        viewModel,
                        id,
                        navController
                    )

                }
            }
        }
    }




}