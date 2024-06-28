package com.example.dogetime.domain.use_case.animecat

import com.example.dogetime.data.remote.dto.AnimeCatDTO
import com.example.dogetime.domain.model.AnimeDetails
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.model.OkanimeEpisode
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.repository.AnimeCatRepository
import com.example.dogetime.util.Constants
import com.example.dogetime.util.Resource
import com.example.dogetime.util.extractors.FuseVideo
import com.example.dogetime.util.extractors.StreamTape
import com.google.gson.Gson
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
                        poster = "${Constants.ANIMECAT_URL}${it.select("img")[1].attr("src")}",
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

    fun getAnime(query: String?): Flow<Resource<List<MovieHome>>> = flow {

        emit(Resource.Loading())
        try {
            val res = repo.getAnime()
            if (res.code() != 200) {
                throw Exception("Anime cat error ${res.code()}")
            }
            val gson = Gson()
            val anime = gson.fromJson(res.body()!!, AnimeCatDTO::class.java).map {
                MovieHome(
                    id = it.url.split("/").reversed()[0],
                    title = it.title,
                    poster = "${Constants.ANIMECAT_URL}${it.url_image}",
                    type = "animeFR"
                )
            }
            if (query == null) {
                emit(Resource.Success(anime))
            } else {
                emit(Resource.Success(anime.filter {
                    it.title.lowercase().contains(query.lowercase())
                }))
            }
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
            val animeInfo = doc.select("div#anime-info-list").select("div.item")
            val cover = doc.select("div.cover").select("img").attr("src")
            val details = Details(
                id = slug,
                imdbId = null,
                title = doc.select("h1").first()?.ownText() ?: "",
                backdrop = cover,
                poster = cover,
                homepage = "",
                genres = emptyList(),
                overview = doc.select("div.synopsis").text(),
                releaseDate = animeInfo[4].text().replace("Diffusion", ""),
                runtime = 23,
                status = animeInfo[2].text(),
                tagline = "",
                rating = animeInfo[0].text().toDoubleOrNull(),
                type = "animeFR",
                seasons = null,
                lastAirDate = "",
                episodes = animeInfo[3].text().split(" ")[1]
            )

            val episodesContainer =
                doc.select("div.episodes")
                    .select("div.col-lg-4.col-sm-6.col-xs-6")

            val episodes = mutableListOf<OkanimeEpisode>()

            if (episodesContainer.isNotEmpty()) {
                episodesContainer.map {
                    episodes.add(
                        OkanimeEpisode(
                            title = it.select("div.limit").text(),
                            slug = it.select("a").attr("href").split("/").last(),
                            poster = cover,
                            episodeNumber = it.select("span.episode").text().split(" ").last()
                        )
                    )
                }

            } else {
                val altEpisodesContainer = doc.select("div.episodes")
                    .select("div.col-lg-12.col-sm-12.col-xs-12")
                altEpisodesContainer.map {
                    episodes.add(
                        OkanimeEpisode(
                            title = it.text(),
                            slug = it.select("a").attr("href").split("/").last(),
                            poster = cover,
                            episodeNumber = it.text().split("-").last()
                        )
                    )
                }
            }
            emit(Resource.Success(AnimeDetails(details, episodes)))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }

//    fun getEpisode(slug: String): Flow<Resource<List<MovieHome>>> = flow {
//
//        emit(Resource.Loading())
//        try {
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emit(Resource.Error(e.message.toString()))
//        }
//
//    }


    fun getSources(slug: String): Flow<List<Source>> = flow {
//        emit(Resource.Loading())
        try {
            val res = repo.getEpisode(slug)
            if (res.code() != 200) {
                throw Exception("Anime-cat sources Error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val script = doc.selectFirst("script:containsData(var video = [];)")!!.data()
            val playersRegex = Regex("video\\s*\\[\\d*]\\s*=\\s*'(.*?)'")

            val links = mutableListOf<Source>()
            playersRegex.findAll(script).map {
                it.groupValues[1]
            }.toList().forEach {
                when {
                    it.contains("fusevideo") -> links.addAll(FuseVideo().getVideo(it))

                    it.contains("streamtape") -> StreamTape().getVideo(it)
                }
            }




            emit(links)
//            emit(Resource.Success(links))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
//            emit(Resource.Error(e.message.toString()))
        }

    }
}