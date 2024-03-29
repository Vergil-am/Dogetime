package com.example.dogetime.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import com.example.dogetime.presentation.layouts.Item

@Composable
fun BottomNavBar(
    items: List<Item>,
    currentDestination: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        items.onEach {
            NavigationBarItem(
                selected = currentDestination == it.title,
                onClick = { onNavigate(it.title) },
                label = { it.title },
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.title)
                }
            )
        }
    }
}