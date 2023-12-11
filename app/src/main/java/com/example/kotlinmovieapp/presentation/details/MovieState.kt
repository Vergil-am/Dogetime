package com.example.kotlinmovieapp.presentation.details

import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeSourcesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.Movie

data class MovieState(
    val isLoading: Boolean = false,
    val media: Details? = null,
    val season: SeasonDTO? = null,
    var watchList: List<Movie> = listOf(),
    var animeEpisodes: AnimeiatEpisodesDTO? = null,
    var animeEpisodeId: String? = null,
    var animeEpisodeSources: AnimeiatEpisodeSourcesDTO? = null,
    var episodeUrl : String? = null
)
