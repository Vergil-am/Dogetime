package com.example.dogetime.domain.use_case.goganime

import com.example.dogetime.domain.model.AnimeDetails
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.model.OkanimeEpisode
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.repository.GogoAnimeRepository
import com.example.dogetime.util.Constants.GOGOANIME_URL
import com.example.dogetime.util.ExtractorProp
import com.example.dogetime.util.Resource
import com.example.dogetime.util.extractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import javax.inject.Inject

class GogoAnimeUseCase @Inject constructor(
    private val repo: GogoAnimeRepository
) {
    fun getLatestEpisodes(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {

            val res = repo.getLatestEpisodes()
            if (res.code() != 200) {
                throw Exception("Aniwave error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val episodesContainers = doc.select("div.last_episodes.loaddub").select("li")

            val episodes = mutableListOf<MovieHome>()
            episodesContainers.map {
                episodes.add(
                    MovieHome(
                        id = it.select("a").attr("href").substringBefore("-episode")
                            .substringAfter("/"),
                        title = it.select("p.name").text(),
                        poster = "$GOGOANIME_URL${it.select("img").attr("src")}",
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
            val infoContainer = doc.select("div.anime_info_body")
            val poster = infoContainer.select("img").attr("src")
            val info = infoContainer.select("p.type")

            val episodesContainer = doc.select("div.anime_video_body").select("li")

            val episodes = getEpisodes(episodesContainer, poster)
            val details = Details(
                id = slug,
                imdbId = null,
                title = infoContainer.select("h1").text(),
                backdrop = "$GOGOANIME_URL$poster",
                poster = "$GOGOANIME_URL$poster",
                homepage = "",
                genres = emptyList(),
                overview = info[1].text().substringAfter(":"),
                releaseDate = info[3].text().substringAfter(":"),
                runtime = 23,
                status = info[3].text().substringAfter(":"),
                tagline = "",
                rating = null,
                type = "animeEN",
                seasons = null,
                lastAirDate = "",
                episodes = "",
            )


            emit(Resource.Success(AnimeDetails(details, episodes)))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }
    }

    private fun getEpisodes(elements: Elements, poster: String): List<OkanimeEpisode> {

        val episodes = elements.map {
            OkanimeEpisode(
                title = it.select("div.name").text(),
                slug = it.select("a").attr("href").substringAfter("/"),
                poster = "$GOGOANIME_URL$poster",
                episodeNumber = it.select("div.name").text().substringAfter(" "),
            )
        }
        return episodes.reversed()
    }


    fun getSources(slug: String): Flow<List<Source>> = flow {
        try {
            val res = repo.getSources(slug)
            if (res.code() != 200) {
                throw Exception("Gogoanime error code ${res.code()}")
            }

            val doc = Jsoup.parse(res.body()!!)
            val links = doc.select("div.anime_muti_link").select("li").map {
                ExtractorProp(
                    link = it.select("a").attr("data-video"),
                    quality = null
                )
            }
            val sources = extractor(links)
            emit(sources)

        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }

    }


}