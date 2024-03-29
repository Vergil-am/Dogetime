package com.example.dogetime.presentation.layouts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.dogetime.presentation.components.BottomNavBar
import com.example.dogetime.presentation.components.NavRail
import com.example.dogetime.presentation.navgraph.Route


class Item(val icon: ImageVector, val title: String)

val Items = listOf(
    Item(icon = Icons.Outlined.Home, Route.Home.route),
    Item(icon = Icons.Outlined.Menu, Route.Browse.route),
    Item(icon = Icons.Outlined.FavoriteBorder, Route.WatchList.route),
    Item(icon = Icons.Outlined.Settings, Route.Account.route),

    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLayout(
    navController: NavController, screen: @Composable() () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val currentDestination = navController.currentDestination?.route


    fun onNavigate(route: String) {
        navController.navigate(route = route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

    }
    Scaffold(topBar = {
        if(screenWidth < 600) {
            TopAppBar(title = { Text(text = "LOGO") }, actions = {
                IconButton(onClick = {
                    navController.navigate(Route.Search.route)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Search, contentDescription = "Search icon"
                    )
                }
            })
        }
    }, bottomBar = {
        if (screenWidth < 600) {
            BottomNavBar(items = Items,
                currentDestination = currentDestination,
                onNavigate = { onNavigate(it) })
        }
    }) { paddingValues ->
        if (screenWidth < 600) {
            Surface(
                modifier = Modifier.padding(paddingValues)
            ) {
                screen()
            }
        } else {
            Row(
                Modifier.fillMaxSize()
            ) {
                NavRail(items = Items,
                    currentDestination = currentDestination,
                    onNavigate = { onNavigate(it) })
                screen()
            }
        }
    }

}

