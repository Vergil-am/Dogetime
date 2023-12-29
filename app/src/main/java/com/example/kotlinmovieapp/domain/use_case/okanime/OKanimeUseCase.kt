package com.example.kotlinmovieapp.domain.use_case.okanime

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.model.OkanimeEpisode
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.repository.OKanimeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class OKanimeUseCase @Inject constructor(
    private val repo: OKanimeRepository
){
    fun getLatestEpisodes(page: Int) : Flow<List<MovieHome>> = flow {
        val res = repo.getLatestEpisodes().body()
        if (res != null) {
            val doc = Jsoup.parse(res)
            val animeCards = doc.getElementsByClass("anime-card episode-card")
            val deffered = animeCards.map {card ->
                CoroutineScope(Dispatchers.Default).async {
                    val slug = card.getElementsByClass("anime-title").first()?.select("a")?.attr("href")
                        ?.split("/")?.get(4) ?: ""

                    val poster = repo.getAnimeDetails(slug).body()?.let {
                        Jsoup.parse(it).selectFirst("img.shadow-lg")?.attr("src")
                    }

                    val episode = MovieHome(
                        id = slug,
                        title = card.getElementsByClass("anime-title").first()?.select("a")?.text()?.trim()
                            ?: "",
                        poster = poster ?: "",
                        type = "anime"
                    )

                    episode
                }
            }
            val episodes = deffered.awaitAll()
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

            val doc = Jsoup.parse(res).getElementsByClass("container")
            val listInfo = doc.select("div.full-list-info")
            val runtime = listInfo[2].select("small:eq(1)").text().split(" ")[1]

            val details = Details(
                    id = slug,
                    imdbId = null,
                    title = doc.select("h1").first()?.text() ?: "",
                    backdrop = doc.select("img.shadow-lg").attr("src"),
                    poster = doc.select("img.shadow-lg").attr("src"),
                    homepage = null,
                    genres = doc.select("div.review-author-info a").map { it.text() },
                    overview = doc.select("p").first()?.text() ?: "",
                    releaseDate = listInfo[0].select("small:eq(1)").text(),
                    runtime = runtime.toInt(),
                    status = listInfo[5].select("small:eq(1)").text(),
                    tagline = null,
                    rating = null,
                    type = "anime",
                    seasons = null ,
                    lastAirDate = null,
                    episodes = listInfo[4].select("small:eq(1)").text(),
            )
            val episodesHtml = doc.select("div.row.no-gutters div.small")
            val episodes = episodesHtml.map {
                OkanimeEpisode(
                    title = it.selectFirst("div.anime-title h5 a")?.text() ?: "",
                    slug = it.selectFirst("div.episode-image a")?.attr("href")?.split("/")?.reversed()?.get(1)
                        ?: "",
                    poster = it.selectFirst("div.episode-image img")?.attr("src") ?: "",
                    episodeNumber = it.selectFirst("div.anime-title h5 a")?.text()?.split(" ")?.get(1)
                        ?: ""
                )
            }

            emit(OkanimeDetails(details= details, episodes = episodes))
        }
    }

    fun getEpisode(slug: String) : Flow<List<Source>> = flow {
        val doc = repo.getEpisode(slug).body()
        if (doc != null) {
            val sources = Jsoup.parse(doc).select("a.ep-link").map {element ->
                Source(
                    url =  element.attr("data-src") ?: "",
                quality = element.selectFirst("span")?.text().let {
                    when (it)  {
                        "FHD" -> "1080p"
                        "HD" -> "720p"
                        "SD" -> "480p"
                        else -> "240p"
                    }
                },
                label = element.selectFirst("span")?.text() ?: "?",
                source = element.childNodes().last().toString()
                )
            }

            Log.e("SOURCES", sources.toString())

            val urlRegex = Regex(
                "((https?|ftp)://|(www\\.)|ftp\\.)[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/\\S*)?"
            )
            emit(sources.filter { it.url.matches(urlRegex) }.sortedBy { it.label })
        }

    }


    fun getAnime(page: Int) : Flow<List<MovieHome>> = flow {
        val doc = repo.getAnime(page).body()
        if (doc != null) {
            val animes = Jsoup.parse(doc).select("div.anime-card").map {
                val slug = it.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1) ?: ""
                MovieHome(
                    id = slug,
                    poster = it.selectFirst("img.img-responsive")?.attr("src") ?: "",
                    type = "anime",
                    title = it.selectFirst("h4")?.text() ?: ""
                )
            }
            emit(animes)
        }
    }

    fun searchAnime(query: String) : Flow<List<MovieHome>> = flow {
        val doc = repo.searchAnime(query).body()
        if (doc != null) {
            val animes = Jsoup.parse(doc).select("div.anime-card").map {
                val slug = it.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1)
                MovieHome(
                    id = slug ?: "",
                    poster = it.selectFirst("img.img-responsive")?.attr("src") ?: "",
                    type = "anime",
                    title = it.selectFirst("h4")?.text() ?: ""
                )
            }
            Log.e("animes", animes.toString())
            emit(animes)
        }
    }
}