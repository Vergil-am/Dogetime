package com.example.kotlinmovieapp.domain.use_case.okanime

import android.util.Base64
import android.util.Log
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.model.OkanimeEpisode
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.model.VideoLinks
import com.example.kotlinmovieapp.domain.repository.OKanimeRepository
import com.google.gson.Gson
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
//            val animeCards = doc.getElementsByClass("anime-card episode-card")
            val animeCards = doc.getElementsByClass("anime-card-container")
            val episodes = animeCards.map { card ->
                val slug =
                    card.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1)?.split("%")
                        ?.get(0)?.dropLast(1)
//                CoroutineScope(Dispatchers.Default).async {
//                    val slug = card.getElementsByClass("anime-title").first()?.select("a")?.attr("href")
//                        ?.split("/")?.get(4) ?: ""
//
//                    val poster = repo.getAnimeDetails(slug).body()?.let {
//                        Jsoup.parse(it).selectFirst("img.shadow-lg")?.attr("src")
//                    }
//
//                    val episode = MovieHome(
//                        id = slug,
//                        title = card.getElementsByClass("anime-title").first()?.select("a")?.text()?.trim()
//                            ?: "",
//                        poster = poster ?: "",
//                        type = "anime"
//                    )
//
//                    episode
//                }
//            }.awaitAll()
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
//            val doc = Jsoup.parse(res).getElementsByClass("container")
//            val listInfo = doc.select("div.full-list-info")
//            val runtime = listInfo[2].select("small:eq(1)").text().split(" ")[1]
//
//            val details = Details(
//                    id = slug,
//                    imdbId = null,
//                    title = doc.select("h1").first()?.text() ?: "",
//                    backdrop = doc.select("img.shadow-lg").attr("src"),
//                    poster = doc.select("img.shadow-lg").attr("src"),
//                    homepage = null,
//                    genres = doc.select("div.review-author-info a").map { it.text() },
//                    overview = doc.select("p").first()?.text() ?: "",
//                    releaseDate = listInfo[0].select("small:eq(1)").text(),
//                    runtime = runtime.toInt(),
//                    status = listInfo[5].select("small:eq(1)").text(),
//                    tagline = null,
//                    rating = null,
//                    type = "anime",
//                    seasons = null ,
//                    lastAirDate = null,
//                    episodes = listInfo[4].select("small:eq(1)").text(),
//            )
//            val episodesHtml = doc.select("div.row.no-gutters div.small")
//            val episodes = episodesHtml.map {
//                OkanimeEpisode(
//                    title = it.selectFirst("div.anime-title h5 a")?.text() ?: "",
//                    slug = it.selectFirst("div.episode-image a")?.attr("href")?.split("/")?.reversed()?.get(1)
//                        ?: "",
//                    poster = it.selectFirst("div.episode-image img")?.attr("src") ?: "",
//                    episodeNumber = it.selectFirst("div.anime-title h5 a")?.text()?.split(" ")?.get(1)
//                        ?: ""
//                )
//            }
//

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
            Log.e("BASE64", base64.toString())
            val sources = String(Base64.decode(base64, Base64.DEFAULT))
            // NEEDS JSON Parse
            val videoLinks : VideoLinks = Gson().fromJson(sources, VideoLinks::class.java)
            Log.e("Sources", sources.toString())
//            val sources = Jsoup.parse(doc).select("a.ep-link").map {element ->
//                Source(
//                    url =  element.attr("data-src") ?: "",
//                quality = element.selectFirst("span")?.text().let {
//                    when (it)  {
//                        "FHD" -> "1080p"
//                        "HD" -> "720p"
//                        "SD" -> "480p"
//                        else -> "240p"
//                    }
//                },
//                label = element.selectFirst("span")?.text() ?: "?",
//                source = element.childNodes().last().toString()
//                )
//            }

//            Log.e("SOURCES", sources.toString())

//            val urlRegex = Regex(
//                "((https?|ftp)://|(www\\.)|ftp\\.)[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/\\S*)?"
//            )
//            emit(sources.filter { it.url.matches(urlRegex) }.sortedBy { it.label })
            Log.e("VIDEO Links", videoLinks.toString())
            emit(videoLinks)
        }

    }




    // Unchanged yet
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