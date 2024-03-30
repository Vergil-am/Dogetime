package com.example.dogetime.domain.use_case.aniwave

import com.example.dogetime.domain.model.AnimeDetails
import com.example.dogetime.domain.model.Details
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
                        type = "animeEN"
                    )
                )

            }

            emit(Resource.Success(episodes))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }


    fun getAnimeDetails(slug: String): Flow<Resource<AnimeDetails>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getAnimeDetails(slug)
            if (res.code() != 200) {
                throw Exception("Aniwave error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val infoContainer = doc.select("div#w-info")
            val animeInfo = doc.select("div.bmeta").select("div.meta")
            val poster = infoContainer.select("img").attr("src")

            val details = Details(
                id = slug,
                imdbId = null,
                title = infoContainer.select("h1.title.d-title").text(),
                backdrop = poster,
                poster = poster,
                homepage = "",
                genres = emptyList(),
                overview = infoContainer.select("div.content").text(),
                releaseDate = animeInfo[0].select("div")[3].text().split(":")[1],
                runtime = animeInfo[1].select("div")[2].text()
                    .split(":")[1].split(" ")[1].toIntOrNull(),

                status = animeInfo[0].select("div")[6].select("span").text(),
                tagline = doc.select("div.names font-italic mb-2").text(),
                rating = animeInfo[1].select("div")[0].select("span").text()
                    .split(" ")[0].toDoubleOrNull(),
                type = "animeEN",
                seasons = null,
                lastAirDate = animeInfo[0].select("div")[5].select("span").text(),
                episodes = animeInfo[1].select("div")[2].select("span").text()
            )

            val dataId = doc.selectFirst("div[data-id]")!!.attr("data-id")

            emit(Resource.Success(AnimeDetails(details, emptyList())))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }

}