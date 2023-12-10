package com.example.kotlinmovieapp.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.kotlinmovieapp.presentation.components.MovieRow


@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state = viewModel.state.value
    Column (
    modifier = Modifier
        .verticalScroll(rememberScrollState())
    ) {

//        Text(text = "In cinemas", modifier = Modifier
//            .padding(10.dp))
//        Carousel(
//            modifier = Modifier
//                .padding(10.dp)
//                .fillMaxWidth()
//                .height(209.5.dp)
//                .background(Color.Black),
//            movies = state.trending?.results,
//            navController
//        )
        state.movies?.let {
            Text(text = "Trending movies")
            MovieRow(data = it, navController = navController)
        }
        state.shows?.let {

            Text(text = "Trending series")
            MovieRow(data = it, navController = navController)
        }

        state.anime?.let {
            Text(text = "Popular anime")
            MovieRow(data = it, navController = navController)

        }

            


    }
}



