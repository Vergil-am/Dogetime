package com.example.dogetime.presentation.player

import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.extractors.vidplay.models.Subtitle

data class PlayerState(
    val isPlaying: Boolean = true,
    val source: PlayerSource? = null,
    val sources: List<Source> = emptyList(),
    val currentTime: Long = 0,
    val totalDuration: Long = 0
)


data class PlayerSource(
    val source: Source? = null,
    val subtitles: List<Subtitle> = emptyList()
)