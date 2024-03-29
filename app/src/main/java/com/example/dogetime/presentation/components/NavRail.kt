package com.example.dogetime.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import com.example.dogetime.presentation.layouts.Item
import com.example.dogetime.presentation.navgraph.Route

@Composable
fun NavRail(
    items: List<Item>,
    currentDestination: String?,
    onNavigate: (String) -> Unit
) {
    NavigationRail(
        header = {
            IconButton(onClick = {
                onNavigate(Route.Search.route)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search, contentDescription = "Search icon"
                )
            }
        }
    ) {
        items.forEach {
            NavigationRailItem(selected = it.title == currentDestination, onClick = {
                onNavigate(it.title)
            }, icon = {
                Icon(imageVector = it.icon, contentDescription = it.title)
            })
        }
    }
}