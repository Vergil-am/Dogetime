package com.example.kotlinmovieapp.presentation.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Account (
    viewModel: SettingsViewModel
) {
    val state = viewModel.state.value

    Text(text = state.version)

}
