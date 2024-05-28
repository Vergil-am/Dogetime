package com.example.dogetime.presentation.player

import com.example.dogetime.data.local.entities.HistoryMedia
import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.extractors.vidplay.models.Subtitle

data class PlayerState(
    val isPlaying: Boolean = true,
    val media: HistoryMedia? = null,
    val source: PlayerSource? = null,
    val sources: List<Source> = emptyList(),
    val selectedSubtitle: Subtitle? = null,
    val currentTime: Long = 0,
    val totalDuration: Long = 0
)


data class PlayerSource(
    val source: Source? = null,
    val subtitles: List<Subtitle> = emptyList()
)