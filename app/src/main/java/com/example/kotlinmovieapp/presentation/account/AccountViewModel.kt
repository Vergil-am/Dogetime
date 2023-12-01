package com.example.kotlinmovieapp.presentation.account

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.data.remote.dto.RequestTokenDTO
import com.example.kotlinmovieapp.domain.use_case.auth.reqTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val reqTokenUseCase: reqTokenUseCase
) : ViewModel() {
    private val _state = mutableStateOf(accountState())
    val state = _state


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getReqToken(): Deferred<RequestTokenDTO> {
        return viewModelScope.async {
            reqTokenUseCase.generateReqToken().reduce { _, value -> value }
        }
    }

    fun getSessionId(token: String) {
        reqTokenUseCase.createSessionId(token).onEach {
           res -> state.value = accountState(token = state.value.token, sessionId = res.session_id)
        }.launchIn(viewModelScope)
    }

}