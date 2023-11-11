package com.example.kotlinmovieapp.presentation.browse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
//    viewModel.getMovies()
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
       Column (
           modifier = Modifier.padding(paddingValues)
       ) {
           Row {


               Box(modifier = Modifier) {
                   OutlinedButton(onClick = { opened = "type"}) {
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

               }

           }
           val movies = state.value.movies
           movies?.results?.forEach { movie ->
          Card (
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .height(200.dp)
                        .width(133.5.dp),
//                    onClick = {
//                        navController.navigate("$type/${movie.id}") {
//                            popUpTo(navController.graph.findStartDestination().id) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = rememberAsyncImagePainter(

                            "${Constants.IMAGE_BASE_URL}/w200${movie.poster_path}"
                        ),
                        contentDescription = movie.title
                    )
                }

           }

       }
   }



