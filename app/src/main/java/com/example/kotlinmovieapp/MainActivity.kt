package com.example.kotlinmovieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinmovieapp.presentation.browse.BrowseViewModel
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel
import com.example.kotlinmovieapp.presentation.home.HomeViewModel
import com.example.kotlinmovieapp.presentation.navgraph.NavGraph
import com.example.kotlinmovieapp.presentation.search.SearchViewModel
import com.example.kotlinmovieapp.ui.theme.KotlinMovieAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel> ()
    private val homeViewModel : HomeViewModel by viewModels()
    private val detailsViewModel: DetailsViewModel by viewModels()
    private val browseViewModel: BrowseViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.systemBars())
        super.onCreate(savedInstanceState)
        setContent {
            KotlinMovieAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        startDestination = viewModel.startDestination,
                        homeViewModel = homeViewModel,
                        detailsViewModel = detailsViewModel,
                        browseViewModel = browseViewModel,
                        searchViewModel = searchViewModel
                    )
                }
            }
        }
    }

}



/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinMovieAppTheme {
        NavGraph(startDestination = "Home")
    }
}*/
