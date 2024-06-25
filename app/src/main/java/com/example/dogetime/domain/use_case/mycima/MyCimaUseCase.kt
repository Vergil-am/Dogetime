package com.example.dogetime.domain.use_case.mycima

import android.util.Log
import com.example.dogetime.data.remote.dto.SeasonDTO
import com.example.dogetime.data.remote.dto.mycima.MycimaSearchDTO
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.Episode
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.repository.MyCimaRepository
import com.example.dogetime.util.Constants
import com.example.dogetime.util.Resource
import com.example.dogetime.util.extractors.Mycima
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import javax.inject.Inject

class MyCimaUseCase @Inject constructor(
    private val repo: MyCimaRepository
) {
    fun getLatest(): Flow<Resource<List<MovieHome>>> = flow {

        emit(Resource.Loading())
        try {
            val res = repo.getLatest()
            if (res.code() != 200) {
                throw Exception("My cima error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val slider =
                doc.select("div.home-slider").select("div.glide-track").select("div.content-box")


            val movies = mutableListOf<MovieHome>()

            slider.map {
                val id = it.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1)
                    ?: throw Exception("My cima can't get Id")

                val title = it.selectFirst("a")?.attr("title")
                movies.add(
                    MovieHome(
                        id = id,
                        poster = it.select("img").attr("src"),
                        title = title ?: "",
                        type = if (title?.contains("فيلم") == true) "mycima - movie" else "mycima - show"
                    )
                )
            }
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("My cima error ${e.message}"))
        }

    }

    fun getLatestMovies(): Flow<Resource<List<MovieHome>>> = flow {

        emit(Resource.Loading())
        try {
            val res = repo.getLatestMovies()
            if (res.code() != 200) {
                throw Exception("My cima error code ${res.code()}")
            }

            val doc = Jsoup.parse(res.body()!!)

            val container = doc.select("div.Grid--WecimaPosts").select("div.GridItem")

            val movies = movieListExtractor(container, "mycima - movie")



            emit(Resource.Success(movies))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("My cima error ${e.message}"))
        }

    }

    fun getLatestEpisodes(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getLatestEpisodes()
            if (res.code() != 200) {
                throw Exception("My cima error code ${res.code()}")
            }

            val doc = Jsoup.parse(res.body()!!)
            val container = doc.select("div.Grid--WecimaPosts").select("div.GridItem")


            val episodes = movieListExtractor(container, "mycima - show")


            emit(Resource.Success(episodes))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("My cima error ${e.message}"))
        }

    }


    fun getMovieDetails(id: String, type: String): Flow<Resource<MyCimaDetails>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getMovieDetails(id)
            Log.e("res", res.toString())
            if (res.code() != 200) {
                throw Exception("My cima details error code ${res.code()}")
            }

            val doc = Jsoup.parse(res.body()!!)
            val infoContainer = doc.select("div.Single-begin")
            val description = doc.select("div.StoryMovieContent").text()
            val poster =
                doc.select("wecima.separated--top").attr("data-lazy-style").substringAfter("url(")
                    .substringBefore(")")
            val seasons = getSeasons(doc, poster)
            val details = Details(
                id = id,
                title = infoContainer.select("div.Title--Content--Single-begin").select("h1")
                    .text(),
                backdrop = poster,
                poster = poster,
                genres = emptyList(),
                overview = description,
                releaseDate = "",
                status = "",
                type = type,
                episodes = null,
                tagline = "",
                homepage = "",
                lastAirDate = null,
                imdbId = "",
                rating = 7.1,
                runtime = 45,
                seasons = seasons,
            )

            val sources = extractSources(doc.select("singlesections"))

            emit(Resource.Success(MyCimaDetails(details = details, sources = sources)))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
        }

    }


    private suspend fun getSeasons(
        doc: Document, poster: String
    ): List<SeasonDTO> {
        try {
            val seasonsContainer = doc.select("div.List--Seasons--Episodes").select("a")
            val episodesContainer = doc.select("div.Episodes--Seasons--Episodes").select("a")
            var seasons = seasonsContainer.map {
                SeasonDTO(
                    _id = it.text(),
                    air_date = "",
                    episodes = getEpisodes(it.select("a").attr("href"), poster),
                    id = 1,
                    name = it.text(),
                    overview = "",
                    poster_path = poster,
                    season_number = it.text().substringAfter(" ").toInt(),
                    vote_average = 0.0
                )
            }

            if (seasons.isEmpty()) {
                seasons = listOf(
                    SeasonDTO(
                        _id = "موسم 1",
                        air_date = "",
                        episodes = episodesContainer.map { episode ->
                            Episode(
                                air_date = null,
                                crew = emptyList(),
                                episode_number = episode.text().substringAfter(" ").toInt(),
                                episode_type = "",
                                guest_stars = emptyList(),
                                name = episode.text(),
                                overview = "",
                                production_code = "",
                                season_number = 1,
                                id = episode.select("a").attr("href").substringAfter("/watch/")
                                    .substringBefore("/"),
                                runtime = null,
                                show_id = 0,
                                still_path = poster,
                                vote_average = 0.0,
                                vote_count = 0

                            )
                        },
                        id = 1,
                        name = "موسم 1",
                        overview = "",
                        poster_path = poster,
                        season_number = 1,
                        vote_average = 0.0
                    )
                )
            }
            return seasons

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

    }

    private suspend fun getEpisodes(url: String, poster: String): List<Episode> {
        try {
            val res = repo.getSeasons(url)
            if (res.code() != 200) {
                throw Exception("My cima episodes error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val episodesContainer = doc.select("div.Episodes--Seasons--Episodes").select("a")
            val episodes = episodesContainer.map {
                Episode(
                    air_date = null,
                    crew = emptyList(),
                    episode_number = it.text().substringAfter(" ").toInt(),
                    episode_type = "",
                    guest_stars = emptyList(),
                    name = it.text(),
                    overview = "",
                    production_code = "",
                    season_number = it.text().substringAfter(" ").toInt(),
                    id = it.select("a").attr("href").substringAfter("/watch/")
                        .substringBefore("/"),
                    runtime = null,
                    show_id = 0,
                    still_path = poster,
                    vote_average = 0.0,
                    vote_count = 0

                )
            }
            return episodes
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    private suspend fun extractSources(container: Elements): List<Source> {
        val sources = mutableListOf<Source>()
        val downloadContainer = container.select("div.Download--Wecima--Single").select("li")
        downloadContainer.map {
            sources.add(
                Source(
                    url = it.select("a").attr("href"),
                    source = it.select("quality").text(),
                    quality = it.select("resolution").text().split(" ")[0],
                    label = it.select("resolution").text().split(" ")[1],
                    header = Constants.WE_CIMA_URL
                )
            )
        }

        val watchContainer = container.select("div.WatchServers").select("li")

        watchContainer.map {
            val url = it.select("btn").attr("data-url")
            val source = it.text()
            Mycima().extractSource(url = url, header = Constants.WE_CIMA_URL, source = source)
                ?.let { it1 ->
                    sources.add(
                        it1
                    )
                }
        }
        return sources
    }

    fun getSources(url: String): Flow<List<Source>> = flow {
        try {
            val res = repo.getSources(url)
            if (res.code() != 200) {
                throw Exception("My cima sources error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val links = doc.select("ul.tabContainer").select("li")
            val sources = mutableListOf<Source>()
            links.map {
                val link = it.select("a").attr("data-embed")
                val referer = Constants.CIMALEK_URL
                val source = it.text()
                Mycima().extractSource(
                    url = link, header = referer, source = source
                )?.let { it1 ->
                    sources.add(
                        it1
                    )
                }
            }
            emit(sources)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    fun getEpisodeSources(id: String): Flow<List<Source>> = flow {
        try {
            val res = repo.getMovieDetails(id)
            if (res.code() != 200) {
                throw Exception("Get mycima episode sources error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val sources = extractSources(doc.select("singlesections"))
            emit(sources)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun movieListExtractor(container: Elements, type: String): List<MovieHome> {
        val movies = mutableListOf<MovieHome>()
        container.map {
            val poster = it.select("span").attr("data-lazy-style").substringAfter("url(")
                .substringBefore(")")
            movies.add(
                MovieHome(
                    id = it.select("a").attr("href").substringAfter("/watch/").substringBefore("/"),
                    title = it.select("a").attr("title"),
                    poster = poster,
                    type = type
                )
            )
        }
        return movies
    }


    fun search(query: String): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.search(query)
            if (res.code() != 200) {
                throw Exception("My cima search error ${res.code()}")
            }
            val gson = Gson()
            val json = gson.fromJson(res.body()!!, MycimaSearchDTO::class.java)

            val doc = Jsoup.parse(json.output)
            val container = doc.select("div.GridItem")
            val movies = container.map {
                MovieHome(
                    id = it.select("a").attr("href").substringAfter("/watch/").substringBefore("/"),
                    title = it.select("strong").text(),
                    poster = it.select("span.BG--GridItem").attr("data-lazy-style").substringAfter("url(").substringBefore(")"),
                    type = ""
                )
            }
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("My cima search error ${e.message}"))
        }
    }

}
