package com.example.dogetime.presentation.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogetime.data.local.entities.HistoryMedia
import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.extractors.vidplay.models.Subtitle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
//    private val history: HistoryDao
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
        val media = state.value.media?.copy(
            progress = state.value.currentTime,
            duration = state.value.totalDuration,
        )
        Log.e("Media", media.toString())
        viewModelScope.launch {
//            if (media != null) {
//                history.addToHistory(media)
//            }
        }
    }

}