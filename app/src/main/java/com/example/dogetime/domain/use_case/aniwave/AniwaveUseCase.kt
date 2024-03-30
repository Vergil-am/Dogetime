package com.example.dogetime.domain.use_case.aniwave

import com.example.dogetime.domain.model.AnimeDetails
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.repository.AniwaveRepository
import com.example.dogetime.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class AniwaveUseCase @Inject constructor(
    private val repo: AniwaveRepository
) {
    fun getLatestEpisodes(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {

            val res = repo.getLatestEpisodes()
            if (res.code() != 200) {
                throw Exception("Aniwave error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val episodesContainers = doc.getElementsByClass("ani items").select("div.item")

            val episodes = mutableListOf<MovieHome>()
            episodesContainers.map {
                episodes.add(
                    MovieHome(
                        id = it.select("a").attr("href").split("/")[2],
                        title = it.select("img").attr("alt"),
                        poster = it.select("img").attr("src"),
                        type = "anime"
                    )
                )

            }

            emit(Resource.Success(episodes))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }
    
    
    fun getAnimeDetails(slug : String): Flow<Resource<AnimeDetails>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getAnimeDetails(slug)
            if (res.code() != 200) {
                throw Exception("Aniwave error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }
}