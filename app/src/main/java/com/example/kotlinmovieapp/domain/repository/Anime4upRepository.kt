package com.example.kotlinmovieapp.domain.repository

import retrofit2.Response

interface Anime4upRepository {

    suspend fun getLatestEpisodes(
//        page: Int
    ) : Response<String>

    suspend fun getAnimeDetails(slug: String) : Response<String>

    suspend fun getEpisode(slug: String) : Response<String>

    suspend fun getAnime(page: Int) : Response<String>

    suspend fun searchAnime(query: String) : Response<String>

    suspend fun getAnimeByGenre(genre: String) : Response<String>

    suspend fun getAnimeByType(catalog: String) : Response<String>
}