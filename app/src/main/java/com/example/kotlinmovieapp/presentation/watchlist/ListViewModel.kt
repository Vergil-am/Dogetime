package com.example.kotlinmovieapp.presentation.watchlist
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.datastore.AccountStore
import com.example.kotlinmovieapp.domain.use_case.list.ListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
   private val list: ListUseCase,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(ListState())
    var state = _state

    private val dataStore = AccountStore(context)
    init {
        getAccountFromDataStore()
    }

    fun getWatchList(type: String) {
        if (state.value.sessionId != null && state.value.accountId != null) {
            list.getWatchList(type = type,
                state.value.sessionId!!, state.value.accountId!!
            ).onEach { list ->
                _state.value = ListState(
                    movies = list.results,
                    sessionId = state.value.sessionId,
                    accountId = state.value.accountId
                )
            }.launchIn(viewModelScope)
        }
    }
//    fun getFavorites(type: String) {
//        list.getFavorites(type).onEach {
//                list -> _state.value = ListState(movies = list )
//        }.launchIn(viewModelScope)
//    }

    private fun getAccountFromDataStore() {
            dataStore.getSessionId.onEach {
                if (it != null) {
                    _state.value = ListState(movies = state.value.movies, sessionId = it, accountId = state.value.accountId)
                }
            }.launchIn(viewModelScope)
            dataStore.getAccountID.onEach {
                if (it != null) {
                    _state.value = ListState(movies = state.value.movies, sessionId = state.value.sessionId, accountId = it.toInt())
                }
            }.launchIn(viewModelScope)
        }

    }

