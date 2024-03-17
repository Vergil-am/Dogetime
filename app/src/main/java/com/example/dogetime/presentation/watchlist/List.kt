package com.example.dogetime.presentation.watchlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.dogetime.util.ListsClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchList(
    viewModel: ListViewModel, navController: NavController
) {
    val lists = ListsClass.lists.plus(ListsClass.All)
    Log.e("Lists", lists.toString())
    val state = viewModel.state.collectAsState().value
    viewModel.getWatchList()
    val gridState = rememberLazyGridState()
    var selectedTab by remember { mutableIntStateOf(0) }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            lists.onEachIndexed { index, item ->
                Log.e("LISt", item.name)
                ElevatedFilterChip(modifier = Modifier.padding(horizontal = 5.dp),
                    selected = index == selectedTab,
                    onClick = { selectedTab = index },
                    label = { Text(text = item.name) })
            }
        }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            state = gridState,
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(10.dp),
        ) {
            state.media.filter {
                if (lists[selectedTab].value == "all") {
                    true
                } else {
                    it.list == lists[selectedTab].value
                }
            }.forEach {
                item {
                    Card(modifier = Modifier
                        .padding(10.dp)
                        .height(155.dp), onClick = {
                        navController.navigate("${it.type}/${it.id}")
                    }) {
                        Image(
                            modifier = Modifier.fillMaxSize(), painter = rememberAsyncImagePainter(
                                it.poster
                            ), contentDescription = it.title
                        )
                    }
                }
            }
        }

    }

}


