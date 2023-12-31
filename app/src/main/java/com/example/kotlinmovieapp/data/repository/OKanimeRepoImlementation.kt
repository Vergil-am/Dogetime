package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.OkanimeAPI
import com.example.kotlinmovieapp.domain.repository.OKanimeRepository
import retrofit2.Response
import javax.inject.Inject

class OKanimeRepoImlementation @Inject constructor(
    private val okanime: OkanimeAPI
): OKanimeRepository {

    override suspend fun getLatestEpisodes(
//        page: Int
    ): Response<String> {
        return okanime.getLatestEpisodes(
//            page
        )
    }

    override suspend fun getAnimeDetails(slug: String): Response<String> {
        return okanime.getAnimeDetails(slug)
    }

    override suspend fun getEpisode(slug: String): Response<String> {
       return okanime.getEpisode(slug)
    }

    override suspend fun getAnime(page: Int): Response<String> {
       return okanime.getAnime(page)
    }

    override suspend fun searchAnime(query: String): Response<String> {
       return okanime.searchAnime(param = "animes", query,)
    }

}