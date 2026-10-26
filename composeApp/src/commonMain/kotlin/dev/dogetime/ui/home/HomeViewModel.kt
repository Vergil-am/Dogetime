package dev.dogetime.ui.home

import dev.dogetime.core.UiState
import dev.dogetime.core.ViewModel
import dev.dogetime.core.runForUiState
import dev.dogetime.data.FavoritesRepository
import dev.dogetime.data.MediaRepository
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.popularCategory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeContent(
    val popularMovies: List<MediaItem>,
    val popularTv: List<MediaItem>,
    val trendingMovies: List<MediaItem>,
    val trendingTv: List<MediaItem>,
)

class HomeViewModel(
    private val repository: MediaRepository,
    favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<HomeContent>>(UiState.Loading)
    val state: StateFlow<UiState<HomeContent>> = _state.asStateFlow()

    /** Saved favorites, newest first. Updates as soon as one is added or removed. */
    val favorites: StateFlow<List<MediaItem>> = favoritesRepository.observeFavorites()
        .stateIn(scope, SharingStarted.Eagerly, initialValue = emptyList())

    init {
        load()
    }

    fun load() {
        _state.value = UiState.Loading
        scope.launch {
            _state.value = runForUiState {
                coroutineScope {
                    val popularMovies = async { repository.browse(MediaType.Movie, popularCategory, 1).items }
                    val popularTv = async { repository.browse(MediaType.Tv, popularCategory, 1).items }
                    val trendingMovies = async { repository.trending(MediaType.Movie) }
                    val trendingTv = async { repository.trending(MediaType.Tv) }
                    HomeContent(
                        popularMovies = popularMovies.await(),
                        popularTv = popularTv.await(),
                        trendingMovies = trendingMovies.await(),
                        trendingTv = trendingTv.await(),
                    )
                }
            }
        }
    }
}
