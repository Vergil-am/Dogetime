package com.example.kotlinmovieapp.domain.use_case.okanime

import android.util.Log
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.repository.OKanimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class OKanimeUseCase @Inject constructor(
    private val repo: OKanimeRepository
){
    fun getLatestEpisdoes() : Flow<List<MovieHome>> = flow {
        val res = repo.getHome().body()
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
}