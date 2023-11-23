package com.example.kotlinmovieapp.presentation.account

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.auth.reqTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val reqTokenUseCase: reqTokenUseCase
) : ViewModel() {
    private val _state = mutableStateOf(accountState())
    val state = _state
    fun getReqToken() {
        reqTokenUseCase.generateReqToken().onEach {
            res -> state.value = accountState(res.request_token)
        }.launchIn(viewModelScope)
    }

    fun getSessionId(token: String) {
        reqTokenUseCase.createSessionId(token).onEach {
           res -> Log.e("Response is: ",res.toString())
        }.launchIn(viewModelScope)
    }

}