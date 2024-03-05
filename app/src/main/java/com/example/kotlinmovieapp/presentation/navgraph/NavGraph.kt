package com.example.kotlinmovieapp.presentation.navgraph

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinmovieapp.presentation.browse.Browse
import com.example.kotlinmovieapp.presentation.browse.BrowseViewModel
import com.example.kotlinmovieapp.presentation.details.AnimeEpisodes
import com.example.kotlinmovieapp.presentation.details.Details
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel
import com.example.kotlinmovieapp.presentation.details.ShowSeasons
import com.example.kotlinmovieapp.presentation.home.Home
import com.example.kotlinmovieapp.presentation.home.HomeViewModel
import com.example.kotlinmovieapp.presentation.layouts.HomeLayout
import com.example.kotlinmovieapp.presentation.search.Search
import com.example.kotlinmovieapp.presentation.search.SearchViewModel
import com.example.kotlinmovieapp.presentation.settings.Account
import com.example.kotlinmovieapp.presentation.settings.SettingsViewModel
import com.example.kotlinmovieapp.presentation.watchlist.ListViewModel
import com.example.kotlinmovieapp.presentation.watchlist.WatchList
import com.example.kotlinmovieapp.presentation.webView.WebView
import java.net.URLDecoder


class Item(val icon: ImageVector, val title: String)

val Items = listOf(
    Item(icon = Icons.Outlined.Home, Route.Home.route),
    Item(icon = Icons.Outlined.Menu, Route.Browse.route),
    Item(icon = Icons.Outlined.FavoriteBorder, Route.WatchList.route),
    Item(icon = Icons.Outlined.Settings, Route.Account.route),

    )

@SuppressLint("SourceLockedOrientationActivity")
@RequiresApi(34)
@Composable
fun NavGraph(
    startDestination: String,
    homeViewModel: HomeViewModel,
    detailsViewModel: DetailsViewModel,
    browseViewModel: BrowseViewModel,
    searchViewModel: SearchViewModel,
    accountViewModel: SettingsViewModel,
    listViewModel: ListViewModel,
    windowCompat: WindowInsetsControllerCompat
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = startDestination, modifier = Modifier
    ) {
        composable(route = Route.Home.route) {
            HomeLayout(navController = navController) {
                Home(navController = navController, viewModel = homeViewModel)
            }
        }
        composable(route = Route.Browse.route) {

            HomeLayout(navController = navController) {
                Browse(
                    navController, browseViewModel
                )
            }
        }
        composable(route = Route.Search.route) {

            Search(
                navController, searchViewModel
            )
        }
        composable(route = Route.WatchList.route) {

            HomeLayout(navController = navController) {
                WatchList(
                    listViewModel, navController
                )
            }
        }
        composable(route = Route.Account.route) {

            HomeLayout(navController = navController) {
                Account(
                    accountViewModel
                )
            }
        }

        composable(Route.MovieDetails.route) {
            val id = it.arguments?.getString("id")
            if (id != null) {
                Details(
                    navController = navController,
                    viewModel = detailsViewModel,
                    id = id,
                    type = "movie"
                )
            }
        }
        composable(Route.ShowDetails.route) {
            val id = it.arguments?.getString("id")
            if (id != null) {
                Details(
                    navController = navController,
                    viewModel = detailsViewModel,
                    id = id,
                    type = "show"

                )
            }
        }
        composable(Route.ShowSeasons.route) {
            val id = it.arguments?.getString("id")
            if (id != null) {
                ShowSeasons(
                    viewModel = detailsViewModel, navController = navController, id = id.toInt()
                )
            }
        }

        composable(Route.AnimeDetails.route) {
            val slug = it.arguments?.getString("slug")
            if (slug != null) {
                Details(
                    navController = navController,
                    viewModel = detailsViewModel,
                    id = slug,
                    type = "anime"

                )
            }

        }
        composable(Route.AnimeEpisodes.route) {
            val slug = it.arguments?.getString("slug")
            if (slug != null) {
                AnimeEpisodes(viewModel = detailsViewModel,  navController)
            }
        }

        composable(Route.WebView.route) {
            val url = it.arguments?.getString("url")
            if (url != null) {
                val decodedUrl = URLDecoder.decode(url)
                Log.e("URL", url)
                WebView(url = decodedUrl, windowCompat = windowCompat)
            }
        }

        composable(Route.Mediaplayer.route) {
        }

    }
}
