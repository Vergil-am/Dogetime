package com.example.kotlinmovieapp.presentation.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
fun Search(
    navController: NavController,
    viewModel: SearchViewModel
) {
    var search by remember {
        mutableStateOf("")
    }
    val state = viewModel.state.collectAsState().value

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "")},
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Route.Browse.route)
                    }) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Search" )
                    }
                    OutlinedTextField(
                        value = search,
                        placeholder = { Text(text = "Search")},
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = {
                            viewModel.getSearch(search)
                            Log.d("DONE", search)
                        }),
                        trailingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search Icon" )},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                        ,
                        onValueChange = {value -> search = value })


                }
            )
        }

    ) { paddingValues ->
        Surface (
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(10.dp)
            ){
                state.result?.results?.forEach { movie ->
                    item {
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(155.dp),
                            onClick = {
                                if (
                                    movie.media_type == "movie"
                                ) {

                                    navController.navigate("movie/${movie.id}")
                                } else if (movie.media_type == "tv") {
                                    navController.navigate("show/${movie.id}")
                                }
                            }
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

        }
    }
}


