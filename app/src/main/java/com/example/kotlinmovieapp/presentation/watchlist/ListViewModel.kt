package com.example.kotlinmovieapp.presentation.watchlist
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.list.ListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
   private val list: ListUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ListState())
    var state = _state
    init {
//       getWatchList("tv")
//        getFavorites("movies")
    }

    fun getWatchList(type: String) {
        list.getWatchList(type = type).onEach {
            list -> _state.value = ListState(movies = list)
        }.launchIn(viewModelScope)
    }
    fun getFavorites(type: String) {
        list.getFavorites(type).onEach {
                list -> _state.value = ListState(movies = list )
        }.launchIn(viewModelScope)
    }

}