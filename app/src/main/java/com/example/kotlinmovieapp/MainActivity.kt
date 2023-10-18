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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
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
    val selectedItem = remember { mutableStateOf(0) }

    Scaffold (

        bottomBar = {
            NavigationBar (
            ) {
                Items.forEachIndexed { index, item: Item ->
                    NavigationBarItem(
                        selected = selectedItem.value == index,
                        onClick = {
                            selectedItem.value = index
                        },
                        label = {item.title},
                        icon = { Icon(item.icon, item.title)})

                }
            }
        }
    )
    {innerPadding ->
        Text(
            text = "Test",
            modifier = Modifier
                .padding(innerPadding)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinMovieAppTheme {
        Main()
    }
}