package dev.dogetime.ui.home

import dev.dogetime.core.ViewModel
import dev.dogetime.data.FavoritesRepository
import dev.dogetime.data.MediaRepository
import dev.dogetime.data.WatchHistoryRepository
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.WatchHistoryItem
import dev.dogetime.domain.popularCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeContent(
    val popularMovies: List<MediaItem> = emptyList(),
    val popularTv: List<MediaItem> = emptyList(),
    val trendingMovies: List<MediaItem> = emptyList(),
    val trendingTv: List<MediaItem> = emptyList(),
    val trendingAnime: List<MediaItem> = emptyList(),
    val popularAnime: List<MediaItem> = emptyList(),
    val isLoadingPopularMovies: Boolean = false,
    val isLoadingPopularTv: Boolean = false,
    val isLoadingTrendingMovies: Boolean = false,
    val isLoadingTrendingTv: Boolean = false,
    val isLoadingTrendingAnime: Boolean = false,
    val isLoadingPopularAnime: Boolean = false,
    val globalError: String? = null,
) {
    val isAnyLoading: Boolean
        get() = isLoadingPopularMovies || isLoadingPopularTv || isLoadingTrendingMovies ||
            isLoadingTrendingTv || isLoadingTrendingAnime || isLoadingPopularAnime

    val hasAnyContent: Boolean
        get() = popularMovies.isNotEmpty() || popularTv.isNotEmpty() || trendingMovies.isNotEmpty() ||
            trendingTv.isNotEmpty() || trendingAnime.isNotEmpty() || popularAnime.isNotEmpty()
}

class HomeViewModel(
    private val repository: MediaRepository,
    favoritesRepository: FavoritesRepository,
    watchHistoryRepository: WatchHistoryRepository? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContent())
    val state: StateFlow<HomeContent> = _state.asStateFlow()

    /** Saved favorites, newest first. Updates as soon as one is added or removed. */
    val favorites: StateFlow<List<MediaItem>> = favoritesRepository.observeFavorites()
        .stateIn(scope, SharingStarted.Eagerly, initialValue = emptyList())

    /** Recent watch history, newest first. Updates automatically as playback progresses. */
    val watchHistory: StateFlow<List<WatchHistoryItem>> = (watchHistoryRepository?.observeHistory()
        ?: kotlinx.coroutines.flow.flowOf(emptyList()))
        .stateIn(scope, SharingStarted.Eagerly, initialValue = emptyList())

    init {
        load()
    }

    fun load() {
        _state.value = HomeContent(
            isLoadingPopularMovies = true,
            isLoadingPopularTv = true,
            isLoadingTrendingMovies = true,
            isLoadingTrendingTv = true,
            isLoadingTrendingAnime = true,
            isLoadingPopularAnime = true,
        )

        // Launch each fetch in its own independent coroutine so it renders as soon as it arrives
        // without waiting for sibling requests or failing if another request throws an error.
        scope.launch {
            try {
                val items = repository.browse(MediaType.Movie, popularCategory, 1).items
                _state.update { it.copy(popularMovies = items, isLoadingPopularMovies = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Failed to load popular movies: ${e.message}")
                _state.update { it.copy(isLoadingPopularMovies = false) }
                checkAllFailed()
            }
        }

        scope.launch {
            try {
                val items = repository.browse(MediaType.Tv, popularCategory, 1).items
                _state.update { it.copy(popularTv = items, isLoadingPopularTv = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Failed to load popular TV: ${e.message}")
                _state.update { it.copy(isLoadingPopularTv = false) }
                checkAllFailed()
            }
        }

        scope.launch {
            try {
                val items = repository.trending(MediaType.Movie)
                _state.update { it.copy(trendingMovies = items, isLoadingTrendingMovies = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Failed to load trending movies: ${e.message}")
                _state.update { it.copy(isLoadingTrendingMovies = false) }
                checkAllFailed()
            }
        }

        scope.launch {
            try {
                val items = repository.trending(MediaType.Tv)
                _state.update { it.copy(trendingTv = items, isLoadingTrendingTv = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Failed to load trending TV: ${e.message}")
                _state.update { it.copy(isLoadingTrendingTv = false) }
                checkAllFailed()
            }
        }

        scope.launch {
            try {
                val items = repository.trending(MediaType.Anime)
                _state.update { it.copy(trendingAnime = items, isLoadingTrendingAnime = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Failed to load trending anime: ${e.message}")
                _state.update { it.copy(isLoadingTrendingAnime = false) }
                checkAllFailed()
            }
        }

        scope.launch {
            try {
                val items = repository.browse(MediaType.Anime, popularCategory, 1).items
                _state.update { it.copy(popularAnime = items, isLoadingPopularAnime = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Failed to load popular anime: ${e.message}")
                _state.update { it.copy(isLoadingPopularAnime = false) }
                checkAllFailed()
            }
        }
    }

    private fun checkAllFailed() {
        _state.update { current ->
            if (!current.isAnyLoading && !current.hasAnyContent) {
                current.copy(globalError = "Failed to load content. Please check your network connection.")
            } else {
                current
            }
        }
    }
}
