package com.example.kotlinmovieapp.presentation.browse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlinmovieapp.presentation.navgraph.Route
import com.example.kotlinmovieapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Browse(
    navController: NavController,
    viewModel: BrowseViewModel
) {
    val state = viewModel.state.collectAsState()
    var opened by remember {
        mutableStateOf("")
    }
   Scaffold (
       topBar = {
           TopAppBar(
               title = { Text(text = "LOGO")},
               actions = {
                   IconButton(onClick = {
                       navController.navigate(Route.Search.route)
                   }) {
                       Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search icon")
                   }
               }
               )
       }
   ) { paddingValues ->
       Column(
           modifier = Modifier
               .padding(paddingValues)
       ) {
           Row {


               Box(modifier = Modifier) {
                   OutlinedButton(onClick = { opened = "type" }) {
                       Text(text = state.value.type)
                       Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                   }
                   DropdownMenu(
                       expanded = opened == "type",
                       onDismissRequest = {
                           opened = ""
                       }) {
                       DropdownMenuItem(text = {
                           Text(text = "Movies")
                       }, onClick = {
                           state.value.type = "Movies"
                           opened = ""
                       })
                       DropdownMenuItem(text = {
                           Text(text = "Series")
                       }, onClick = {
                           state.value.type = "Series"
                           opened = ""
                       })
                   }
               }
               Box(modifier = Modifier) {
                   OutlinedButton(onClick = { opened = "catalog"}) {
                       Text(text = state.value.catalog)
                       Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                   }
                   DropdownMenu(
                       expanded = opened == "catalog",
                       onDismissRequest = {
                           opened = ""
                       }) {
                       DropdownMenuItem(text = {
                           Text(text = "Popular")
                       }, onClick = {
                           state.value.type = "popular"
                           opened = ""
                           viewModel.getMovies("popular")
                       })
                       DropdownMenuItem(text = {
                           Text(text = "Top Rated")
                       }, onClick = {
                           state.value.type = "top_rated"
                           opened = ""
                           viewModel.getMovies("top_rated")
                       })
                       DropdownMenuItem(text = {
                           Text(text = "Now playing")
                       }, onClick = {
                           state.value.type = "now_playing"
                           opened = ""
                           viewModel.getMovies("now_playing")
                       })
                   }

               }

           }
           val movies = state.value.movies

           LazyVerticalGrid(
               columns = GridCells.Fixed(3),
               contentPadding = PaddingValues(10.dp)
           ) {
           movies?.results?.forEach { movie ->
               item {
                   Card(
                       modifier = Modifier
                           .padding(10.dp)
                           .height(155.dp),
                       onClick = {
                           navController.navigate("movie/${movie.id}")
                       }
                       ) {
                       Image(
                           modifier = Modifier
                               .fillMaxSize(),

                           painter = rememberAsyncImagePainter(
                               "${Constants.IMAGE_BASE_URL}/w200${movie.poster_path}"),
                           contentDescription = movie.title
                       )
                   }
               }
           }
           }
       }


       }
   }




