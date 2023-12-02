package com.example.kotlinmovieapp.presentation.account

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.data.remote.dto.RequestTokenDTO
import com.example.kotlinmovieapp.datastore.AccountStore
import com.example.kotlinmovieapp.domain.use_case.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val auth : AuthUseCase,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _state = mutableStateOf(accountState())
    val state = _state


    private val dataStore = AccountStore(context)
   init {
       getAccountFromDataStore()
   }

    fun getReqToken(): Deferred<RequestTokenDTO> {
        return viewModelScope.async {
            auth.generateReqToken().reduce { _, value -> value }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSessionId(token: String, dataStore: AccountStore) {
        val session = viewModelScope.async {
            auth.createSessionId(token).onEach {
                    res -> state.value = accountState(account = state.value.account ,token = state.value.token, sessionId = res.session_id)
            }.reduce { _ , value ->  value}
        }
        session.invokeOnCompletion {
            if (it == null) {
                Log.e("SESSION", session.getCompleted().session_id)
                getAccount(session.getCompleted().session_id, dataStore)
            }
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getAccount(sessionId: String, dataStore: AccountStore) {
        val account = viewModelScope.async {
            auth.getAccount(sessionId).onEach {
                    res -> state.value =  accountState(account = res , token = state.value.token, sessionId = state.value.sessionId)
                Log.e("ACCOUNT", res.toString())
            }.reduce { _ , value ->  value }
        }

        account.invokeOnCompletion {
            if (it == null) {
                viewModelScope.launch {
                    dataStore.storeSessionId(sessionId)
                    dataStore.storeAccountId(account.getCompleted().id)

                }
            }
        }

    }

    private fun getAccountFromDataStore() {
        dataStore.getSessionId.onEach {
            if (it != null) {
                state.value.sessionId = it
            }
        }.launchIn(viewModelScope)

       dataStore.getAccountID.onEach {
           if (it != null) {
               state.value.accountId = it.toInt()
           }
       }.launchIn(viewModelScope)

    }

}