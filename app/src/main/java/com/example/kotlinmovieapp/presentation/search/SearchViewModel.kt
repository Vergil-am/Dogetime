package com.example.kotlinmovieapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.movies.search.Search
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val search: Search
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state : StateFlow<SearchState> = _state

    fun getSearch(query: String) {
        search.getSearch(query).onEach {searchDTO ->
            _state.value = SearchState(search = state.value.search , result = searchDTO)

        }.launchIn(viewModelScope)
    }

}