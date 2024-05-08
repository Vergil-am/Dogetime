package com.example.dogetime.domain.use_case.goganime

import android.util.Log
import com.example.dogetime.domain.model.AnimeDetails
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.model.OkanimeEpisode
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.repository.GogoAnimeRepository
import com.example.dogetime.util.Constants.GOGOANIME_AJAX_URL
import com.example.dogetime.util.ExtractorProp
import com.example.dogetime.util.Resource
import com.example.dogetime.util.extractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class GogoAnimeUseCase @Inject constructor(
    private val repo: GogoAnimeRepository
) {
    fun getLatestEpisodes(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {

            val res = repo.getLatestEpisodes()
            if (res.code() != 200) {
                throw Exception("Gogo anime error code ${res.code()}")
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
                throw Exception("Gogo anime error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val infoContainer = doc.select("div.anime_info_body")
            val poster = infoContainer.select("img").attr("src")
            val info = infoContainer.select("p.type")
            val episodesContainer = doc.select("div.anime_video_body").select("li")
            val id = infoContainer.select("div.anime_info_episodes_next")
                .select("input")[0].attr("value")
            val endEpisode =
                episodesContainer.select("ul.episode_page").select("li").last()?.text()?.split("-")
                    ?.last() ?: ""
            Log.e("Last episode", endEpisode)
            val episodes = getEpisodes(
                id = id,
                poster = poster,
                endEpisode = endEpisode
            )
            val details = Details(
                id = slug,
                imdbId = null,
                title = infoContainer.select("h1").text(),
                backdrop = poster,
                poster = poster,
                homepage = "",
                genres = info[2].text().substringAfter("Genre:").split(","),
                overview = infoContainer.select("div.description").text(),
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

    private suspend fun getEpisodes(
        poster: String,
        endEpisode: String,
        id: String
    ): List<OkanimeEpisode> {
        val res = repo.getEpisodes(
            "$GOGOANIME_AJAX_URL/load-list-episode?ep_start=0&ep_end=$endEpisode&id=$id"
        )
        Log.e("Response", res.body().toString())
        val episodes = emptyList<OkanimeEpisode>()
//            elements.map {
//            OkanimeEpisode(
//                title = it.select("div.name").text(),
//                slug = it.select("a").attr("href").substringAfter("/"),
//                poster = poster,
//                episodeNumber = it.select("div.name").text().substringAfter(" "),
//            )
//        }
        Log.e("Episodes", episodes.toString())
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