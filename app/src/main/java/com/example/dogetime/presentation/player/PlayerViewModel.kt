package com.example.dogetime.presentation.player

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()


    fun updateIsPlaying(isPlaying: Boolean) {
        _state.value = _state.value.copy(isPlaying = isPlaying)
    }


    fun setSource(source: PlayerSource?) {
        _state.value = _state.value.copy(source = source)
    }

    fun updateDuration(
        currentTime: Long, totalDuration: Long,
    ) {
        _state.value = _state.value.copy(totalDuration = totalDuration, currentTime = currentTime)
    }

}