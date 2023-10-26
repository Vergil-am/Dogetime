package com.example.kotlinmovieapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kotlinmovieapp.presentation.navgraph.Route

class MainViewModel  : ViewModel() {
    var startDestination by mutableStateOf(Route.Home.route)
        private set


}