package com.example.dogetime.domain.use_case.mycima

import android.util.Log
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.repository.MyCimaRepository
import com.example.dogetime.util.Constants
import com.example.dogetime.util.Resource
import com.example.dogetime.util.extractors.Mycima
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
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
                val id =
                    it.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1)
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

    fun getDetails(id: String): Flow<Resource<Details>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getDetails(id)
            if (res.code() != 200) {
                throw Exception("My cima error ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val mediaDetails = doc.select("div.media-details")
            val title = mediaDetails.select("div.title").text()
            val poster = mediaDetails.select("a.poster-image").attr("style")
                .substringAfter("url(").substringBefore(")")
            val details = Details(
                id = id,
                title = title,
                backdrop = poster,
                poster = poster,
                genres = emptyList(),
                overview = mediaDetails.select("div.post-story").select("p").text(),
                releaseDate = "",
                status = "",
                type = if (title.contains("فيلم")) "mycima - movie" else "mycima - show",
                episodes = null,
                tagline = "",
                homepage = "",
                lastAirDate = null,
                imdbId = "",
                rating = 7.1,
                runtime = 45,
                seasons = null,
            )
            getSeasons(id, poster)
            emit(Resource.Success(details))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("My cima error ${e.message}"))
        }
    }


    suspend fun getSeasons(id: String, poster: String) {
        try {
            val seriesId =
                id.substringAfter("مسلسل-").substringBefore("-حلقة").substringBefore("-موسم")
            val url = "https://t4cce4ma.shop/series/$seriesId"
            val res = repo.getSeasons(url)
            if (res.code() != 200) {
                throw Exception("My cima seasons error code ${res.code()}")
            }

            val doc = Jsoup.parse(res.body()!!)
            val seasonsContainer = doc.select("div.List--Seasons--Episodes").select("a")

            val episodesContainer = doc.select("div.Episodes--Seasons--Episodes").select("a")
            Log.e("Seasons container", seasonsContainer.toString())
//            Log.e("Episodes container", episodesContainer.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
                    url = link,
                    header = referer,
                    source = source
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
}