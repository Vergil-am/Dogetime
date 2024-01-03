package com.example.kotlinmovieapp.presentation.browse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinmovieapp.domain.use_case.anime4up.Anime4upUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.genres.GenresUseCase
import com.example.kotlinmovieapp.domain.use_case.movies.get_movies.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
   private val getMoviesUseCase: GetMoviesUseCase,
    private val genresUseCase: GenresUseCase,
    private val anime4up: Anime4upUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BrowseState())
    var state = _state

    init {
        getMovies(state.value.type.value ,state.value.catalog.value, _state.value.page)
        getGenres(state.value.type.value)
    }
    fun getMovies(type: String , catalog: String , page: Int) {
        when (type) {
           "movie" ->             getMoviesUseCase.getMovies(catalog, page).onEach {
               if (page == 1) {
                   _state.value = BrowseState(movies = it, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre, page = state.value.page)
               } else if (page > 1) {
                   _state.value = BrowseState(movies = state.value.movies.plus(it), type = state.value.type, catalog = state.value.catalog, genre = state.value.genre, page = state.value.page + 1)

               }
           }.launchIn(viewModelScope)
           "tv" ->             getMoviesUseCase.getShows(catalog, page).onEach {
               if (page == 1) {
                   _state.value = BrowseState(movies = it, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
               } else if (page > 1) {
                   _state.value = BrowseState(movies = state.value.movies.plus(it), type = state.value.type, catalog = state.value.catalog, genre = state.value.genre, page = state.value.page + 1)

               }
           }.launchIn(viewModelScope)
           "anime" ->
               anime4up.getAnime(page).onEach {
               if (page == 1) {
                   _state.value = BrowseState(movies = it, type = state.value.type, catalog = state.value.catalog, genre = state.value.genre)
               } else if (page > 1) {
                   _state.value = BrowseState(movies = state.value.movies.plus(it), type = state.value.type, catalog = state.value.catalog, genre = state.value.genre, page = state.value.page + 1)

               }
           }.launchIn(viewModelScope)
        }
    }

    fun getGenres(type: String) {
       genresUseCase.getGenres(type).onEach { genres ->
           _state.value = BrowseState(movies = state.value.movies , type = state.value.type, catalog = state.value.catalog, genre = state.value.genre, page = state.value.page,
               genres = genres
               )
       }.launchIn(viewModelScope)

    }


}