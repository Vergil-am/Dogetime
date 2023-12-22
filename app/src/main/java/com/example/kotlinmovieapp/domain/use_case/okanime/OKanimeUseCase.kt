package com.example.kotlinmovieapp.domain.use_case.okanime

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.repository.OKanimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class OKanimeUseCase @Inject constructor(
    private val repo: OKanimeRepository
){
    fun getLatestEpisdoes(page: Int) : Flow<List<MovieHome>> = flow {
        val res = repo.getLatestEpisodes(
//            page
        ).body()
        if (res != null) {

            val doc = Jsoup.parse(res)
            val animeCards = doc.getElementsByClass("anime-card episode-card")

            val episodes = mutableListOf<MovieHome>()

            for (card in animeCards) {
                val episode = MovieHome(
                    id = 1,
                    title = card.getElementsByClass("anime-title").first()?.select("a")?.text()
                        ?.trim()
                        ?: "",
                    poster = card.select("img").first()?.attr("src") ?: "",
                    slug = card.getElementsByClass("anime-title").first()?.select("a")?.attr("href")
                        ?.split("/")?.get(4) ?: "",
                    type = "anime"
                )
                Log.e("episode", episode.toString())
                episodes.add(episode)
            }
            Log.e("Episodes", episodes.toString())
            emit(episodes)
        }
    }

    fun getAnimeDetails(slug: String) : Flow<Details> = flow {
        val res = repo.getAnimeDetails(slug).body()
        if (res != null ) {

            val doc = Jsoup.parse(res).getElementsByClass("container")
            val listInfo = doc.select("div.full-list-info")
            Log.e("List info", listInfo.toString())
            val runtime = listInfo[2].select("small:eq(1)").text().split(" ")[1]

            val details = Details(
                    id = 1,
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
                    slug = slug
                )

            Log.e("Anime Details", details.toString())
            Log.e("Runtime", runtime)
            emit(details)
        }
    }
}