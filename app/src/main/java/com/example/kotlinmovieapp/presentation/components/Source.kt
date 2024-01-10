package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Source(
    source: String, info: String, link: String, navController: NavController, onClick: () -> Unit
) {

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp), onClick = {
        val url = URLEncoder.encode(link)
        navController.navigate("web-view/${url}")
        onClick()
    }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)

        ) {
            Text(text = source)
            Column {
                Text(text = info)
            }
            Icon(
                imageVector = Icons.Filled.PlayArrow, contentDescription = "play"
            )
        }
    }
}