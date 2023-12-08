package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.AnimeiatAPI
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO
import com.example.kotlinmovieapp.domain.repository.AnimeiatRepository
import javax.inject.Inject

class AnimeiatRepoImplementation @Inject constructor(
    private val API: AnimeiatAPI
): AnimeiatRepository {
    override suspend fun getPopularAnime(): AnimeiatDTO {
        return API.getPopularAnime()
    }

    override suspend fun getAnimeDetails(slug: String): AnimeiatDetailsDTO {
        return API.getAnimeDetails(slug)
    }

    override suspend fun getEpisodes(slug: String): AnimeiatEpisodesDTO {
        return API.getEpisodes(slug)
    }
}