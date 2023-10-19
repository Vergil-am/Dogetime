package com.example.kotlinmovieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kotlinmovieapp.ui.theme.KotlinMovieAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinMovieAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main()
                }
            }
        }
    }
}

class Item(val icon: ImageVector, val title: String)
val Items = listOf(
    Item(icon = Icons.Outlined.Home, "Home"),
    Item(icon = Icons.Outlined.Search, "Search"),
    Item(icon = Icons.Outlined.FavoriteBorder, "Favorites"),
    Item(icon = Icons.Outlined.Person, "Account"),

)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    val navController = rememberNavController()
    Scaffold (
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Items.forEach {item: Item ->
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
                        icon = { Icon(item.icon, item.title)})

                }
            }
        }
    )
    {innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(innerPadding)  ) {
            composable("Home") {
                Screen("Home")
            }
            composable("Search") {
                Screen(title = "Search")
            }
            composable("Favorites") {
                Screen(title = "Favorites")
            }
            composable("Account") {
                Screen(title = "Account")
            }


        }
    }
}

@Composable
fun Screen(title: String) {
    Text(text = title)
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinMovieAppTheme {
        Main()
    }
}