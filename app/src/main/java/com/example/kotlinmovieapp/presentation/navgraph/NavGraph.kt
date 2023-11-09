package com.example.kotlinmovieapp.presentation.navgraph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinmovieapp.presentation.layouts.HomeLayout
import com.example.kotlinmovieapp.presentation.details.Details
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel
import com.example.kotlinmovieapp.ui.theme.screens.Account
import com.example.kotlinmovieapp.ui.theme.screens.Favorites
import com.example.kotlinmovieapp.presentation.home.Home
import com.example.kotlinmovieapp.presentation.home.HomeViewModel
import com.example.kotlinmovieapp.presentation.video_player.VideoPlayer
import com.example.kotlinmovieapp.presentation.search.Search


class Item(val icon: ImageVector, val title: String)
val Items = listOf(
    Item(icon = Icons.Outlined.Home, Route.Home.route),
    Item(icon = Icons.Outlined.Search,Route.Search.route),
    Item(icon = Icons.Outlined.FavoriteBorder, Route.Favorites.route),
    Item(icon = Icons.Outlined.Person, Route.Account.route),

    )

@Composable
fun NavGraph (
    startDestination: String,
    homeViewModel: HomeViewModel,
    detailsViewModel: DetailsViewModel
) {
    val navController = rememberNavController()
    NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
        ) {
            composable(route = Route.Home.route) {
                HomeLayout(navController = navController) {
                    Home(navController = navController, viewModel = homeViewModel)
                }
            }
            composable(route = Route.Search.route) {

                HomeLayout(navController = navController) {
                    Search()
                }
            }
            composable(route = Route.Favorites.route) {

                HomeLayout(navController = navController) {
                    Favorites()
                }
            }
            composable(route = Route.Account.route) {

                HomeLayout(navController = navController) {
                    Account()
                }
            }

            composable(Route.VideoPlayer.route) {
                navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                val season = navBackStackEntry.arguments?.getString("season")
                val episode = navBackStackEntry.arguments?.getString("episode")
                if (id != null && episode != null && season != null) {
                    VideoPlayer(id = id, season = season.toInt(), episode = episode.toInt())
                }
            }


            composable(Route.MovieDetails.route) {
                    navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                if (id != null) {
                    Details(
                        navController = navController,
                        viewModel = detailsViewModel,
                        id = id.toInt(),
                        type = "movie"
                    )
                }
            }
            composable(Route.ShowDetails.route) {
                    navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                if (id != null) {
                    Details(
                        navController = navController,
                        viewModel = detailsViewModel,
                        id = id.toInt(),
                        type = "show"

                    )
                }
            }

    }

    }