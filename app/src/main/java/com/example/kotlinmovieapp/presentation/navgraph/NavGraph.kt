package com.example.kotlinmovieapp.presentation.navgraph

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinmovieapp.presentation.browse.Browse
import com.example.kotlinmovieapp.presentation.browse.BrowseViewModel
import com.example.kotlinmovieapp.presentation.layouts.HomeLayout
import com.example.kotlinmovieapp.presentation.details.Details
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel
import com.example.kotlinmovieapp.ui.theme.screens.Account
import com.example.kotlinmovieapp.ui.theme.screens.Favorites
import com.example.kotlinmovieapp.presentation.home.Home
import com.example.kotlinmovieapp.presentation.home.HomeViewModel
import com.example.kotlinmovieapp.presentation.video_player.VideoPlayer
import com.example.kotlinmovieapp.presentation.search.Search
import com.example.kotlinmovieapp.presentation.search.SearchViewModel


class Item(val icon: ImageVector, val title: String)
val Items = listOf(
    Item(icon = Icons.Outlined.Home, Route.Home.route),
    Item(icon = Icons.Outlined.Menu , Route.Browse.route),
    Item(icon = Icons.Outlined.FavoriteBorder, Route.Favorites.route),
    Item(icon = Icons.Outlined.Person, Route.Account.route),

    )

@SuppressLint("SourceLockedOrientationActivity")
@RequiresApi(34)
@Composable
fun NavGraph (
    startDestination: String,
    homeViewModel: HomeViewModel,
    detailsViewModel: DetailsViewModel,
    browseViewModel: BrowseViewModel,
    searchViewModel: SearchViewModel,
    windowCompat: WindowInsetsControllerCompat
) {
    val navController = rememberNavController()
    val activity = LocalView.current.context as Activity
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
        composable(route = Route.Browse.route) {

            HomeLayout(navController = navController) {
                Browse(
                    navController,
                    browseViewModel
                )
            }
        }
            composable(route = Route.Search.route) {

                HomeLayout(navController = navController) {
                    Search(
                        navController,
                        searchViewModel
                    )
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
                    VideoPlayer(
                        id = id, season = season.toInt(),
                        episode = episode.toInt(),
                        windowCompat = windowCompat
                    )
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