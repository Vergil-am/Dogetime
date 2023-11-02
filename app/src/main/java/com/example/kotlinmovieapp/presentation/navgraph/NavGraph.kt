package com.example.kotlinmovieapp.presentation.navgraph

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kotlinmovieapp.presentation.details.Details
import com.example.kotlinmovieapp.presentation.details.DetailsViewModel
import com.example.kotlinmovieapp.ui.theme.screens.Account
import com.example.kotlinmovieapp.ui.theme.screens.Favorites
import com.example.kotlinmovieapp.presentation.home.Home
import com.example.kotlinmovieapp.presentation.home.HomeViewModel
import com.example.kotlinmovieapp.ui.theme.screens.Search


class Item(val icon: ImageVector, val title: String)
val Items = listOf(
    Item(icon = Icons.Outlined.Home, Route.Home.route),
    Item(icon = Icons.Outlined.Search,Route.Search.route),
    Item(icon = Icons.Outlined.FavoriteBorder, Route.Favorites.route),
    Item(icon = Icons.Outlined.Person, Route.Account.route),

    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph (
    startDestination: String,
    homeViewModel: HomeViewModel,
    detailsViewModel: DetailsViewModel
) {
   val navController = rememberNavController()
    Scaffold (
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.title} == true ,
                        onClick = {
                            navController.navigate(route = item.title) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true

                            }
                        },
                        label = {item.title},
                        icon = { Icon(item.icon, item.title) })

                }
            }
        }
    ) {
        paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Route.Home.route) {
                Home(
                    navController,
                    viewModel = homeViewModel
                )
            }
            composable(route = Route.Search.route) {
                Search()
            }
            composable(route = Route.Favorites.route) {
                Favorites()
            }
            composable(route = Route.Account.route) {
                Account()
            }

            composable(Route.MovieDetails.route) {
                    navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                if (id != null) {
                    Details(
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
                        viewModel = detailsViewModel,
                        id = id.toInt(),
                        type = "show"

                    )
                }
            }

    }

    }

}