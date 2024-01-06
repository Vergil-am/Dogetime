package com.example.kotlinmovieapp.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.datastore.PrefrencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()


    fun setTheme(context: Context, theme: String) {
        viewModelScope.launch {
            PrefrencesStore(context).storeTheme(theme)
        }
    }

    fun getTheme(context: Context) {
        PrefrencesStore(context).getTheme.onEach {
            state.value.theme = it
        }.launchIn(viewModelScope)
    }
}