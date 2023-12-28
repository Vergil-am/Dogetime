package com.example.kotlinmovieapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.animeiat.AnimeiatUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.search.Search
import com.example.kotlinmovieapp.domain.use_case.okanime.OKanimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val search: Search,
    private val animeiat: AnimeiatUseCase,
    private val oKanime: OKanimeUseCase

) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state : StateFlow<SearchState> = _state

    fun getSearch(query: String) {
        search.searchMovies(query).onEach {
            _state.value = SearchState(search = state.value.search , movies = it, shows = state.value.shows, anime = state.value.anime)

        }.launchIn(viewModelScope)
        search.searchShows(query).onEach {
            _state.value = SearchState(search = state.value.search , movies = state.value.movies , shows = it, anime = state.value.anime)
        }.launchIn(viewModelScope)
        oKanime.searchAnime(query ).onEach {
            _state.value = SearchState(search = state.value.search , movies = state.value.movies , shows = state.value.shows, anime = it)
        }.launchIn(viewModelScope)

    }

}