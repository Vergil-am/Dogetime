package com.example.kotlinmovieapp.ui.theme.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Filter(val label: String, val value: String)

val filters = listOf(
    Filter("Trending", "trending") ,
    Filter("Top Rated", "top_rated") ,
    Filter("Popular", "popular"),
    Filter("Upcoming", "upcoming")

)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filters() {
    var selected by remember { mutableStateOf("trending") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        filters.forEach { filter ->
            FilterChip(
                modifier = Modifier
                    .padding(end = 5.dp),
                selected = filter.value == selected,
                onClick = {
                    selected = filter.value
                },
                label = {
                    Text(text = filter.label)
                })
        }
    }
}