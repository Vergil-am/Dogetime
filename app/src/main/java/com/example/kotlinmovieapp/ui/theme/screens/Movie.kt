package com.example.kotlinmovieapp.ui.theme.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Movie(
    id: String?
) {
    Text(text = "Movie page $id ")
}