package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.Anime4upAPI
import com.example.dogetime.domain.repository.Anime4upRepository
import retrofit2.Response
import javax.inject.Inject

class Anime4upRepoImplementation @Inject constructor(
    private val repo : Anime4upAPI
): Anime4upRepository {

    override suspend fun getLatestEpisodes(
//        page: Int
    ): Response<String> {
        return repo.getLatestEpisodes(
//            page
        )
    }

    override suspend fun getAnimeDetails(slug: String): Response<String> {
        return repo.getAnimeDetails(slug)
    }

    override suspend fun getEpisode(slug: String): Response<String> {
       return repo.getEpisode(slug)
    }

    override suspend fun getAnime(page: Int): Response<String> {
       return repo.getAnime(page)
    }

    override suspend fun searchAnime(query: String): Response<String> {
       return repo.searchAnime(param = "animes", query)
    }

    override suspend fun getAnimeByGenre(genre: String): Response<String> {
       return repo.getAnimeByGenre(genre)
    }

    override suspend fun getAnimeByType(catalog: String): Response<String> {
       return repo.getAnimeByType(catalog)
    }

}