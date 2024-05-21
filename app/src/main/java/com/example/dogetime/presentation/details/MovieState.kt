package com.example.dogetime.presentation.details

import com.example.dogetime.data.local.entities.WatchListMedia
import com.example.dogetime.data.remote.dto.SeasonDTO
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MyCimaSeason
import com.example.dogetime.domain.model.OkanimeEpisode
import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.extractors.vidplay.models.Subtitle

data class MovieState(
    val isLoading: Boolean = false,
    var media: Details? = null,
    var season: SeasonDTO? = null,
    var watchList: WatchListMedia? = null,
    var animeEpisodes: List<OkanimeEpisode> = listOf(),
    var animeEpisodeId: String? = null,
    var animeEpisodeSources: List<Source> = mutableListOf(),
    var episodeUrl: String? = null,
    val movieSources: List<Source> = emptyList(),
    val subtitles: List<Subtitle> = emptyList(),
    val selectedSource: Source? = null,
    val myCimaSeasons: List<MyCimaSeason>? = null
)
