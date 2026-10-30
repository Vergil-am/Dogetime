package dev.dogetime.ui.details

import dev.dogetime.core.UiState
import dev.dogetime.core.ViewModel
import dev.dogetime.core.runForUiState
import dev.dogetime.data.FavoritesRepository
import dev.dogetime.data.MediaRepository
import dev.dogetime.domain.Episode
import dev.dogetime.domain.MediaDetails
import dev.dogetime.domain.MediaType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** The season the user picked and the state of its episode list. */
data class EpisodesState(
    val seasonNumber: Int? = null,
    val content: UiState<List<Episode>> = UiState.Loading,
)

class DetailsViewModel(
    private val repository: MediaRepository,
    private val favorites: FavoritesRepository,
    private val type: MediaType,
    private val id: Int,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<MediaDetails>>(UiState.Loading)
    val state: StateFlow<UiState<MediaDetails>> = _state.asStateFlow()

    private val _episodes = MutableStateFlow(EpisodesState())
    val episodes: StateFlow<EpisodesState> = _episodes.asStateFlow()

    private var episodesJob: Job? = null

    val isFavorite: StateFlow<Boolean> = favorites.observeIsFavorite(type, id)
        .stateIn(scope, SharingStarted.Eagerly, initialValue = false)

    init {
        load()
    }

    fun load() {
        _state.value = UiState.Loading
        _episodes.value = EpisodesState()
        scope.launch {
            val result = runForUiState { repository.details(type, id) }
            _state.value = result
            // TV shows open on their first season so the episode list isn't empty.
            val firstSeason = (result as? UiState.Success)?.data?.seasons?.firstOrNull()
            if (firstSeason != null) selectSeason(firstSeason.number)
        }
    }

    fun toggleFavorite() {
        val item = (_state.value as? UiState.Success)?.data?.item ?: return
        scope.launch { favorites.toggle(item) }
    }

    fun selectSeason(seasonNumber: Int) {
        if (type != MediaType.Tv && type != MediaType.Anime) return
        val current = _episodes.value
        if (current.seasonNumber == seasonNumber && current.content is UiState.Success) return

        episodesJob?.cancel()
        _episodes.value = EpisodesState(seasonNumber, UiState.Loading)
        episodesJob = scope.launch {
            val content = runForUiState { repository.episodes(id, seasonNumber, type) }
            _episodes.value = EpisodesState(seasonNumber, content)
        }
    }
}
