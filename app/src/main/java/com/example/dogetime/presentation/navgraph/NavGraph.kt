package com.example.dogetime.presentation.navgraph

import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dogetime.presentation.browse.Browse
import com.example.dogetime.presentation.browse.BrowseViewModel
import com.example.dogetime.presentation.details.AnimeEpisodes
import com.example.dogetime.presentation.details.Details
import com.example.dogetime.presentation.details.DetailsViewModel
import com.example.dogetime.presentation.details.ShowSeasons
import com.example.dogetime.presentation.home.Home
import com.example.dogetime.presentation.home.HomeViewModel
import com.example.dogetime.presentation.layouts.HomeLayout
import com.example.dogetime.presentation.player.MediaPlayer
import com.example.dogetime.presentation.player.PlayerSource
import com.example.dogetime.presentation.player.PlayerViewModel
import com.example.dogetime.presentation.search.Search
import com.example.dogetime.presentation.search.SearchViewModel
import com.example.dogetime.presentation.settings.Account
import com.example.dogetime.presentation.settings.SettingsViewModel
import com.example.dogetime.presentation.watchlist.ListViewModel
import com.example.dogetime.presentation.watchlist.WatchList
import com.example.dogetime.presentation.webView.WebView
import java.net.URLDecoder


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
    playerViewModel: PlayerViewModel,
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
            val type = it.arguments?.getString("type")
            if (slug != null && type != null) {
                Details(
                    navController = navController,
                    viewModel = detailsViewModel,
                    id = slug,
                    type = type

                )
            }

        }
        composable(Route.AnimeEpisodes.route) {
            val slug = it.arguments?.getString("slug")
            if (slug != null) {
                AnimeEpisodes(viewModel = detailsViewModel, navController)
            }
        }

        composable(Route.WebView.route) {
            val url = it.arguments?.getString("url")
            if (url != null) {
                val decodedUrl = URLDecoder.decode(url)
                WebView(url = decodedUrl, windowCompat = windowCompat)
            }
        }

        composable(Route.Mediaplayer.route) {
            val detailsState = detailsViewModel.state.collectAsState().value
            playerViewModel.setSource(
                PlayerSource(
                    source = detailsState.selectedSource,
                    subtitles = detailsState.subtitles
                )
            )

            playerViewModel.setSources(detailsState.movieSources)

            MediaPlayer(
                viewmodel = playerViewModel,
                windowCompat = windowCompat
            )
        }

    }
}
