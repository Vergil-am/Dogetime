package com.example.kotlinmovieapp.presentation.watchlist
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
//   private val list: ListUseCase,
    private val watchList : WatchListUseCase,
//    @ApplicationContext context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(ListState())
    var state = _state

//    private val dataStore = AccountStore(context)
    init {
        getWatchList()

//        getAccountFromDataStore()
    }

    fun getWatchList() {
        watchList.getAll().onEach {
            _state.value = ListState(media = it)
        }.launchIn(viewModelScope)
    }

//    fun getWatchList() {
//        if (state.value.sessionId != null && state.value.accountId != null) {
//            list.getWatchList(type = "movies",
//                state.value.sessionId!!, state.value.accountId!!
//            ).onEach { list ->
//                _state.value = ListState(
//                    movies = list.results,
//                    series = state.value.series,
//                    sessionId = state.value.sessionId,
//                    accountId = state.value.accountId
//                )
//            }.launchIn(viewModelScope)
//            list.getWatchList(type = "tv",
//                state.value.sessionId!!, state.value.accountId!!
//            ).onEach { list ->
//                _state.value = ListState(
//                    movies = state.value.movies,
//                    series = list.results,
//                    sessionId = state.value.sessionId,
//                    accountId = state.value.accountId
//                )
//            }.launchIn(viewModelScope)
//        }
//    }
//    private fun getAccountFromDataStore() {
//            dataStore.getSessionId.onEach {
//                if (it != null) {
//                    _state.value = ListState(movies = state.value.movies, sessionId = it, accountId = state.value.accountId)
//                }
//            }.launchIn(viewModelScope)
//            dataStore.getAccountID.onEach {
//                if (it != null) {
//                    _state.value = ListState(movies = state.value.movies, sessionId = state.value.sessionId, accountId = it.toInt())
//                }
//            }.launchIn(viewModelScope)
//        }
//
    }

