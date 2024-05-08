package com.example.dogetime.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogetime.domain.use_case.anime4up.Anime4upUseCase
import com.example.dogetime.domain.use_case.animecat.AnimeCatUseCase
import com.example.dogetime.domain.use_case.goganime.GogoAnimeUseCase
import com.example.dogetime.domain.use_case.movies.search.Search
import com.example.dogetime.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val search: Search,
    private val anime4up: Anime4upUseCase,
    private val animeCat: AnimeCatUseCase,
//    private val gogoAnime: GogoAnimeUseCase

) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    fun getSearch(query: String) {
        search.searchMovies(query).onEach {
            _state.value = _state.value.copy(movies = it)

        }.launchIn(viewModelScope)
        search.searchShows(query).onEach {
            _state.value = _state.value.copy(shows = it)
        }.launchIn(viewModelScope)
        anime4up.searchAnime(query).onEach {
            _state.value = _state.value.copy(animeAR = it)
        }.launchIn(viewModelScope)

        animeCat.getAnime(query).onEach {
            Log.e("animeCat", it.data.toString())
            when (it) {
                is Resource.Success ->
                    _state.value = _state.value.copy(animeFR = it.data)

                else -> {}
            }
        }.launchIn(viewModelScope)


    }

}