package dev.dogetime.ui.search

import dev.dogetime.core.UiState
import dev.dogetime.core.ViewModel
import dev.dogetime.core.runForUiState
import dev.dogetime.data.MediaRepository
import dev.dogetime.domain.MediaItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val results: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
)

class SearchViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var job: Job? = null

    /** Debounced: each keystroke cancels the pending search and restarts the timer. */
    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        job?.cancel()

        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            _state.update { it.copy(results = emptyList(), isLoading = false, error = null, hasSearched = false) }
            return
        }

        job = scope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _state.update { it.copy(isLoading = true, error = null) }
            val result = runForUiState {
                val tmdb = repository.search(trimmed, page = 1).items
                val anime = try {
                    repository.searchAnime(trimmed, page = 1, pageSize = 10).items
                } catch (_: Exception) {
                    emptyList()
                }
                tmdb + anime
            }
            _state.update { state ->
                when (result) {
                    is UiState.Success -> state.copy(
                        results = result.data,
                        isLoading = false,
                        hasSearched = true,
                    )
                    is UiState.Error -> state.copy(isLoading = false, error = result.message)
                    UiState.Loading -> state
                }
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
