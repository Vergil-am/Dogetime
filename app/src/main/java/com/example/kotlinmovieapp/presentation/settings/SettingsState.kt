package com.example.kotlinmovieapp.presentation.settings

data class SettingsState(
    val version: String = "v0.0.1",
    var theme: String? = null
)
