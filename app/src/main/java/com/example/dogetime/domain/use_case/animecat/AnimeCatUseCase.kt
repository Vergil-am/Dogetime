package com.example.dogetime.domain.use_case.animecat

import com.example.dogetime.domain.model.AnimeDetails
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.repository.AnimeCatRepository
import com.example.dogetime.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class AnimeCatUseCase @Inject constructor(
    private val repo: AnimeCatRepository
) {

    fun getLatestEpisodes(): Flow<Resource<List<MovieHome>>> = flow {

        emit(Resource.Loading())
        try {
            val res = repo.getLatestEpisodes()
            if (res.code() != 200) {
                throw Exception("Error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)

            val episodesContainer =
                doc.getElementsByClass("row no-gutters js-last-episode-container")
                    .select("div.col-lg-3.col-md-4.col-xs-6")
            val episodes = mutableListOf<MovieHome>()
            episodesContainer.map {
                episodes.add(
                    MovieHome(
                        id = it.select("a.title").attr("href").split("/")[3],
                        title = it.select("div.limit").text(),
                        poster = it.select("img").attr("src"),
                        type = "animeFR"
                    )
                )
            }

            emit(Resource.Success(episodes))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }

    fun getAnime(): Flow<Resource<List<MovieHome>>> = flow {

        emit(Resource.Loading())
        try {

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }

    fun getAnimeDetails(slug: String): Flow<Resource<AnimeDetails>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getDetails(slug)
            if (res.code() != 200) {
                throw Exception("Anime cat error code ${res.code()}")
            }

            val doc = Jsoup.parse(res.body()!!)

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }

    fun getEpisode(slug: String): Flow<Resource<List<MovieHome>>> = flow {

        emit(Resource.Loading())
        try {

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }
}