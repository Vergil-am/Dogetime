package dev.dogetime.ui.browse

import dev.dogetime.core.UiState
import dev.dogetime.core.ViewModel
import dev.dogetime.core.runForUiState
import dev.dogetime.data.MediaRepository
import dev.dogetime.domain.BrowseCategory
import dev.dogetime.domain.MediaItem
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.browseCategories
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrowseState(
    val type: MediaType = MediaType.Movie,
    val category: BrowseCategory = browseCategories(MediaType.Movie).first(),
    val items: List<MediaItem> = emptyList(),
    val page: Int = 0,
    val totalPages: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val canLoadMore: Boolean
        get() = !isLoading && error == null && page in 1 until totalPages
}

class BrowseViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _state = MutableStateFlow(BrowseState())
    val state: StateFlow<BrowseState> = _state.asStateFlow()

    private var job: Job? = null

    init {
        reload()
    }

    fun selectType(type: MediaType) {
        if (type == _state.value.type) return
        _state.update { it.copy(type = type, category = browseCategories(type).first()) }
        reload()
    }

    fun selectCategory(category: BrowseCategory) {
        if (category == _state.value.category) return
        _state.update { it.copy(category = category) }
        reload()
    }

    fun loadMore() {
        val current = _state.value
        if (current.canLoadMore) fetch(page = current.page + 1, append = true)
    }

    fun reload() {
        fetch(page = 1, append = false)
    }

    private fun fetch(page: Int, append: Boolean) {
        val current = _state.value
        job?.cancel()
        _state.update {
            if (append) {
                it.copy(isLoading = true)
            } else {
                it.copy(items = emptyList(), page = 0, totalPages = 1, isLoading = true, error = null)
            }
        }
        job = scope.launch {
            val result = runForUiState { repository.browse(current.type, current.category, page) }
            _state.update { state ->
                when (result) {
                    is UiState.Success -> state.copy(
                        items = if (append) state.items + result.data.items else result.data.items,
                        page = result.data.page,
                        totalPages = result.data.totalPages,
                        isLoading = false,
                    )
                    is UiState.Error -> state.copy(isLoading = false, error = result.message)
                    UiState.Loading -> state
                }
            }
        }
    }
}
