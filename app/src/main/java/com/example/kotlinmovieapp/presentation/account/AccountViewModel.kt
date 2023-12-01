package com.example.kotlinmovieapp.presentation.account

import android.util.Log
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


    fun getReqToken(): Deferred<RequestTokenDTO> {
        return viewModelScope.async {
            reqTokenUseCase.generateReqToken().reduce { _, value -> value }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSessionId(token: String) {
        val session = viewModelScope.async {
            reqTokenUseCase.createSessionId(token).onEach {
                    res -> state.value = accountState(account = state.value.account ,token = state.value.token, sessionId = res.session_id)
            }.reduce { _ , value ->  value}
        }
        session.invokeOnCompletion {
            if (it == null) {
                Log.e("SESSION", session.getCompleted().session_id)
                getAccount(session.getCompleted().session_id)
            }
        }

    }

    fun getAccount(sessionId: String) {
       reqTokenUseCase.getAccount(sessionId).onEach {
           res -> state.value =  accountState(account = res , token = state.value.token, sessionId = state.value.sessionId)
           Log.e("ACCOUNT", res.toString())
       }.launchIn(viewModelScope)
    }

}