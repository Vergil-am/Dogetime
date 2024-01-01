package com.example.kotlinmovieapp.domain.use_case.anime4up

import android.util.Base64
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.model.OkanimeEpisode
import com.example.kotlinmovieapp.domain.model.VideoLinks
import com.example.kotlinmovieapp.domain.repository.Anime4upRepository
import com.example.kotlinmovieapp.util.parseAnime
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class Anime4upUseCase @Inject constructor(
    private val repo: Anime4upRepository
){
fun getLatestEpisodes(page: Int) : Flow<List<MovieHome>> = flow {
    val res = repo.getLatestEpisodes().body()
    if (res != null) {
        val doc = Jsoup.parse(res)
            val animeCards = doc.getElementsByClass("anime-card-container")
            val episodes = animeCards.map { card ->
                val slug =
                    card.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1)?.split("%")
                        ?.get(0)?.dropLast(1)
                 MovieHome(
                    id = slug ?: "",
                    title = card.selectFirst("img")?.attr("alt") ?: "",
                    poster = card.selectFirst("img")?.attr("src") ?: "",
                    type = "anime"
                 )

            }
            emit(episodes)
        }
    }

    data class OkanimeDetails(
        val details: Details,
        val episodes: List<OkanimeEpisode>,
    )
    fun getAnimeDetails(slug: String) : Flow<OkanimeDetails> = flow {
        val res = repo.getAnimeDetails(slug).body()
        if (res != null ) {
            val info = Jsoup.parse(res).getElementsByClass("anime-info-container")
            val row = info.select("div.anime-info")
            val details = Details(
                    id = slug,
                    imdbId = null,
                    title = info.select("h1.anime-details-title").text() ?: "",
                    backdrop = info.select("img").attr("src"),
                    poster = info.select("img").attr("src"),
                    homepage = "",
                    genres = info.select("li").map { it.text() },
                    overview = info.select("p.anime-story").text() ?: "",
                    releaseDate = row[1].text().split(":")[1],
                    runtime = row[4].text().split(" ")[2].toInt(),
                    status = row[2].select("a").text(),
                    tagline = null,
                    rating = null,
                    type = "anime",
                    seasons = null ,
                    lastAirDate = null,
                    episodes = row[3].text().split(":")[1],
            )
            val episodesSection = Jsoup.parse(res).getElementsByClass("hover ehover6")
            val episodes = episodesSection.map {episode ->
                OkanimeEpisode(
                    title = episode.select("h3").text(),
                    slug = episode.select("h3").select("a").attr("href").split("/").reversed()[1],
                    poster = episode.select("img").attr("src"),
                    episodeNumber = episode.select("h3").text().split(" ")[1],

                )
            }
            emit(OkanimeDetails(details= details, episodes = episodes))
        }
    }

    fun getEpisode(slug: String) : Flow<VideoLinks> = flow {
        val doc = repo.getEpisode(slug).body()
        if (doc != null) {
            val base64 = Jsoup.parse(doc).selectFirst("input[name=wl]")?.attr("value")
            val sources = String(Base64.decode(base64, Base64.DEFAULT))
            val videoLinks : VideoLinks = Gson().fromJson(sources, VideoLinks::class.java)
            emit(videoLinks)
        }

    }




    fun getAnime(page: Int) : Flow<List<MovieHome>> = flow {
        val doc = repo.getAnime(page).body()
        if (doc != null) {
            val animeCards = Jsoup.parse(doc).select("div.anime-card-poster")
            val anime = parseAnime(animeCards)
            emit(anime)
        }
    }

    fun searchAnime(query: String) : Flow<List<MovieHome>> = flow {
        val doc = repo.searchAnime(query).body()
        if (doc != null) {
            val animeCards = Jsoup.parse(doc).select("div.anime-card-poster")
            val anime = parseAnime(animeCards)
            emit(anime)
        }
    }
}