package com.example.kotlinmovieapp.presentation.webView

import android.os.Bundle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(): ViewModel() {
    private val _state = MutableStateFlow<Bundle?>(null)
    val state = _state.asStateFlow()

    fun updateState (bundle: Bundle?) {
        _state.value = bundle
    }
}