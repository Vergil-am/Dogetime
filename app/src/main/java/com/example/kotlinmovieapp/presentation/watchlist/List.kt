package com.example.kotlinmovieapp.presentation.watchlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WatchList(
    viewModel: ListViewModel,
    navController: NavController
) {

    val state = viewModel.state.collectAsState().value
    viewModel.getWatchList()
    val lists = Constants.lists.plus("All")
    val gridState = rememberLazyGridState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState {
        lists.count()
    }
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }
        Column (
            modifier = Modifier.fillMaxSize()
        ){
           ScrollableTabRow(
               selectedTabIndex = selectedTab,
               modifier = Modifier
                   .fillMaxWidth(),

               ) {
               lists.forEachIndexed { index, item ->
                  Tab(
                      selected = selectedTab == index,
                      onClick = { selectedTab = index  },
                      modifier = Modifier.padding(10.dp),
                      selectedContentColor = Color.Red,
                      unselectedContentColor = Color.White

                  ) {
                      Text(text = item)
                  }
               }
           }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                ,
                ) {page ->
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    state = gridState,
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(10.dp),
                ) {
                    state.media.filter {
                        if ( lists[page] == "All") {
                           true
                        } else {
                            it.list == Constants.lists[page]
                        }
                    }.forEach{
                        item {
                            Card(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .height(155.dp),
                                onClick = {
                                    if (it.type == "anime") {
                                        navController.navigate("anime/${it.slug}")
                                    } else {
                                        navController.navigate("${it.type}/${it.id}")
                                    }
                                }
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    painter = rememberAsyncImagePainter(
                                        it.poster
                                    ),
                                    contentDescription = it.title
                                )
                            }
                        }
                    }
                }

            }

        }

    }

