package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.AnimeiatAPI
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeSourcesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatLatestEpisodesDTO
import com.example.kotlinmovieapp.domain.repository.AnimeiatRepository
import javax.inject.Inject

class AnimeiatRepoImplementation @Inject constructor(
    private val api: AnimeiatAPI
): AnimeiatRepository {
    override suspend fun getPopularAnime(query: String?, page: Int?): AnimeiatDTO {
        return api.getPopularAnime(query, page)
    }

    override suspend fun getAnimeDetails(slug: String): AnimeiatDetailsDTO {
        return api.getAnimeDetails(slug)
    }

    override suspend fun getEpisodes(slug: String): AnimeiatEpisodesDTO {
        return api.getEpisodes(slug)
    }

    override suspend fun getEpisode(slug: String): AnimeiatEpisodeDTO {
        return api.getEpisode(slug)
    }

    override suspend fun getEpisodeSources(slug: String): AnimeiatEpisodeSourcesDTO {
        return api.getEpisodeSources(slug)
    }

    override suspend fun getLatestEpisodes(): AnimeiatLatestEpisodesDTO {
        return api.getLatestEpisodes()
    }
}