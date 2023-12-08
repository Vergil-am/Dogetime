package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO

interface AnimeiatRepository {

    suspend fun getPopularAnime() : AnimeiatDTO

    suspend fun getAnimeDetails(slug: String) : AnimeiatDetailsDTO

    suspend fun getEpisodes(slug: String) : AnimeiatEpisodesDTO
}