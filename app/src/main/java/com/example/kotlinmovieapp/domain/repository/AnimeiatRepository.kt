package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeSourcesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO

interface AnimeiatRepository {

    suspend fun getPopularAnime(query: String?, page: Int?) : AnimeiatDTO

    suspend fun getAnimeDetails(slug: String) : AnimeiatDetailsDTO

    suspend fun getEpisodes(slug: String) : AnimeiatEpisodesDTO

    suspend fun getEpisode(slug: String) : AnimeiatEpisodeDTO

    suspend fun getEpisodeSources(slug: String) : AnimeiatEpisodeSourcesDTO
}