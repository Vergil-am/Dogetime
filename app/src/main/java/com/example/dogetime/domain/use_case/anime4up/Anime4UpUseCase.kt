package com.example.dogetime.domain.use_case.anime4up

import android.util.Base64
import android.util.Log
import com.example.dogetime.domain.model.AnimeDetails
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.model.OkanimeEpisode
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.model.VideoLinks
import com.example.dogetime.domain.repository.Anime4upRepository
import com.example.dogetime.util.ExtractorProp
import com.example.dogetime.util.Resource
import com.example.dogetime.util.extractor
import com.example.dogetime.util.parseAnime
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import org.jsoup.Jsoup
import retrofit2.HttpException
import javax.inject.Inject

class Anime4upUseCase @Inject constructor(
    private val repo: Anime4upRepository,
) {
    fun getLatestEpisodes(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getLatestEpisodes()
            if (res.code() != 200) {
                throw Exception("Anime4up error ${res.code()}")
            }

                val doc = res.body()?.let { Jsoup.parse(it) } ?: throw Exception("Anime episodes not found")
                val animeCards = doc.getElementsByClass("anime-card-container")
                val episodes = animeCards.map { card ->
                    val slug = card.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1)
                        ?.split("%")?.get(0)?.dropLast(1)
                    MovieHome(
                        id = slug ?: "",
                        title = card.selectFirst("img")?.attr("alt") ?: "",
                        poster = card.selectFirst("img")?.attr("src") ?: "",
                        type = "anime"
                    )

                }
                emit(Resource.Success(episodes))
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(Resource.Error("HTTP Error"))
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Resource.Error("IO Error"))
        }
    }



    fun getAnimeDetails(slug: String): Flow<Resource<AnimeDetails>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getAnimeDetails(slug).body()
            if (res != null) {
                val info = Jsoup.parse(res).getElementsByClass("anime-info-container")
                val malLink =
                    info.select("div.anime-external-links").select("a").getOrNull(1)?.attr("href")
                val row = info.select("div.anime-info")
                val details = Details(
                    id = slug,
                    imdbId = null,
                    title = info.select("h1.anime-details-title").text() ?: "",
                    backdrop = info.select("img").attr("src"),
                    poster = info.select("img").attr("src"),
                    homepage = malLink ?: "",
                    genres = info.select("li").map { it.text() },
                    overview = info.select("p.anime-story").text() ?: "",
                    releaseDate = row[1].text().split(":")[1],
                    runtime = row[4].text().split(" ")[2].toIntOrNull(),
                    status = row[2].select("a").text(),
                    tagline = null,
                    rating = null,
                    type = "anime",
                    seasons = null,
                    lastAirDate = null,
                    episodes = row[3].text().split(":")[1],
                )
                val episodesSection = Jsoup.parse(res).getElementsByClass("hover ehover6")
                val episodes = episodesSection.map { episode ->
                    OkanimeEpisode(
                        title = episode.select("h3").text(),
                        slug = episode.select("h3").select("a").attr("href").split("/")
                            .reversed()[1],
                        poster = episode.select("img").attr("src"),
                        episodeNumber = episode.select("h3").text().split(" ")[1],

                        )
                }
                emit(Resource.Success(AnimeDetails(details = details, episodes = episodes)))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP exception"))
        } catch (e: HttpException) {
            emit(Resource.Error("IO exception"))
        }
    }

    fun getEpisode(slug: String): Flow<List<Source>> = flow {
        try {
            val doc = repo.getEpisode(slug).body()

            if (doc != null) {
                val base64 = Jsoup.parse(doc).selectFirst("input[name=wl]")?.attr("value")
                val sources = String(Base64.decode(base64, Base64.DEFAULT))
                val data: VideoLinks = Gson().fromJson(sources, VideoLinks::class.java)

                val links = mutableListOf<ExtractorProp>()

                data.hd?.values?.forEach {
                    links.add(ExtractorProp(link = it, "HD"))
                }
                data.sd?.values?.forEach { links.add(ExtractorProp(link = it, "SD")) }
                data.fhd?.values?.forEach { links.add(ExtractorProp(link = it, "SD")) }
                val extractedLinks = extractor(links)

                Log.e("Extracted anime4up", extractedLinks.toString())

                emit(extractedLinks)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun getAnime(page: Int, genre: String?, catalog: String?): Flow<Resource<List<MovieHome>>> =
        flow {
            emit(Resource.Loading())
            try {
                val doc = if (genre != null) {
                    repo.getAnimeByGenre(genre).body()
                } else if (catalog != null) {
                    repo.getAnimeByType(catalog).body()
                } else {
                    repo.getAnime(page).body()
                }
                if (doc != null) {
                    val animeCards = Jsoup.parse(doc).select("div.anime-card-poster")
                    val anime = parseAnime(animeCards)
                    emit(Resource.Success(anime))
                }
            } catch (e: HttpException) {
                emit(Resource.Error("HTTP Error"))
            } catch (e: IOException) {
                emit(Resource.Error("HTTP Error"))
            }
        }

    fun searchAnime(query: String): Flow<List<MovieHome>> = flow {
        val doc = repo.searchAnime(query).body()
        if (doc != null) {
            val animeCards = Jsoup.parse(doc).select("div.anime-card-poster")
            val anime = parseAnime(animeCards)
            emit(anime)
        }
    }
}