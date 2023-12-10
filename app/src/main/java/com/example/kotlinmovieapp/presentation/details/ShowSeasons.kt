package com.example.kotlinmovieapp.presentation.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ShowSeasons(
    viewModel: DetailsViewModel,
    navController: NavController,
    id: Int
) {
    Text(text = id.toString())
    
}