package com.example.dogetime.presentation.player

import androidx.lifecycle.ViewModel
import com.example.dogetime.data.local.entities.HistoryMedia
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.use_case.history.HistoryUseCase
import com.example.dogetime.util.extractors.vidplay.models.Subtitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val history: HistoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()


    fun updateIsPlaying(isPlaying: Boolean) {
        _state.value = _state.value.copy(isPlaying = isPlaying)
    }


    fun setSource(source: PlayerSource?) {
        _state.value = _state.value.copy(source = source)
    }

    fun updateDuration(
        currentTime: Long, totalDuration: Long,
    ) {
        _state.value = _state.value.copy(totalDuration = totalDuration, currentTime = currentTime)
    }

    fun setSources(
        sources: List<Source>
    ) {
        _state.value = _state.value.copy(sources = sources)
    }

    fun selectSubtitle(
        subtitle: Subtitle?
    ) {
        _state.value = _state.value.copy(selectedSubtitle = subtitle)
    }

    fun selectMedia(
        media: HistoryMedia
    ) {
        _state.value = _state.value.copy(media = media)
    }

    fun addToHistory() {

    }

}