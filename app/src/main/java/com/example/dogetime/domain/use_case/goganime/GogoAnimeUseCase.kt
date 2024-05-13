package com.example.dogetime.domain.use_case.goganime

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
            val id = infoContainer.select("div.anime_info_episodes_next")
                .select("input")[0].attr("value")
            val episodes = getEpisodes(
                id = id,
                poster = poster,
                // This is not the best solution but it works i guess
                endEpisode = 10000.toString()
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
        if (res.code() != 200) {
            throw Exception("Gogo anime failed to get episodes error code ${res.code()}")
        }
        val listItems = Jsoup.parse(res.body()!!).select("li")

        val episodes = mutableListOf<OkanimeEpisode>()
        listItems.map {
            episodes.add(
                OkanimeEpisode(
                    title = it.select("div.name").text(),
                    slug = it.select("a").attr("href").substringAfter("/"),
                    poster = poster,
                    episodeNumber = it.select("div.name").text().substringAfter(" "),
                )
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

    fun search(
        query: String,
        page: Int
    ): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.searchAnime(query, page)
            if (res.code() != 200) {
                throw Exception("Gogoanime error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val animeContainer = doc.select("ul.items").select("li")
            val anime = mutableListOf<MovieHome>()
            animeContainer.map {
                anime.add(
                    MovieHome(
                        id = it.select("a").attr("href").split("/").last(),
                        poster = it.select("img").attr("src"),
                        title = it.select("a").text(),
                        type = "animeEN"
                    )
                )

            }
            emit(Resource.Success(anime))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("gogoanime error ${e.message}"))
        }

    }

    fun getPopular(page: Int): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getPopular(page)
            if (res.code() != 200) {
                throw Exception("Gogoanime error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val animeContainer = doc.select("div.last_episodes").select("li")
            val anime = animeContainer.map {
                MovieHome(
                    id = it.select("a").attr("href").split("/").last(),
                    poster = it.select("img").attr("src"),
                    type = "animeEN",
                    title = it.select("p.name").text()
                )
            }
            emit(Resource.Success(anime))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("gogoanime error ${e.message}"))
        }
    }
}