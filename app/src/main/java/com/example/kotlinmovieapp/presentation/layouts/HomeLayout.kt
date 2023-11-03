package com.example.kotlinmovieapp.presentation.layouts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlinmovieapp.presentation.navgraph.Items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLayout(
    navController: NavController,
    screen: @Composable() () -> Unit
) {
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
        paddingValues -> Surface (
            modifier = Modifier.padding(paddingValues)
        ) {
        screen()
    }
    }
}
