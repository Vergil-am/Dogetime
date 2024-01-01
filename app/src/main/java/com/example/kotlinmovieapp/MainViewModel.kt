package com.example.kotlinmovieapp

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.datastore.PrefrencesStore
import com.example.kotlinmovieapp.presentation.navgraph.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel  : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state
    var startDestination by mutableStateOf(Route.Home.route)
        private set

    fun getTheme(context: Context) {
        PrefrencesStore(context).getTheme.onEach {
           _state.value = MainState(theme = it)
        }.launchIn(viewModelScope)
    }

}