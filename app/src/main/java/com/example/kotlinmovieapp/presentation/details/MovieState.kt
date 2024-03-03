package com.example.kotlinmovieapp.presentation.details

import com.example.kotlinmovieapp.data.local.entities.WatchListMedia
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.OkanimeEpisode
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.util.extractors.vidplay.models.Subtitle

data class MovieState(
    val isLoading: Boolean = false,
    var media: Details? = null,
    var season: SeasonDTO? = null,
    var watchList: WatchListMedia?  = null,
    var animeEpisodes: List<OkanimeEpisode> = listOf(),
    var animeEpisodeId: String? = null,
    var animeEpisodeSources: List<Source> = mutableListOf(),
    var episodeUrl : String? = null,
    val movieSources: List<Source> = emptyList(),
    val subtitles : List<Subtitle> = emptyList()
)
