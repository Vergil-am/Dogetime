package com.example.kotlinmovieapp.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.watchlist.WatchListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val watchList: WatchListUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ListState())
    var state = _state.asStateFlow()

    init {
        getWatchList()

    }

    fun getWatchList() {
        watchList.getAll().onEach {
            _state.value = ListState(media = it)
        }.launchIn(viewModelScope)
    }

}

