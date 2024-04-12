package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.MyCimaAPI
import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.repository.MyCimaRepository
import com.example.dogetime.util.Constants
import com.example.dogetime.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class MyCimaRepoImplementation @Inject constructor(
    private val api: MyCimaAPI
) : MyCimaRepository {
    override suspend fun getLatest(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val res = api.getLatest()
            if (res.code() != 200) {
                throw Exception("My cima error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val slider =
                doc.select("div.home-slider").select("div.glide-track").select("div.content-box")


            val movies = mutableListOf<MovieHome>()

            slider.map {
                val id =
                    it.selectFirst("a")?.attr("href")?.substringBeforeLast("/")
                        ?.substringAfter(Constants.CIMALEK_URL)
                        ?: throw Exception("My cima can't get Id")

                movies.add(
                    MovieHome(
                        id = id,
                        poster = it.select("img").attr("src"),
                        title = it.selectFirst("a")?.attr("title") ?: "",
                        type = "mycima"
                    )
                )
            }
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("My cima error ${e.message}"))
        }

    }

    override suspend fun getDetails(id: String): Flow<Resource<Details>> = flow {
        emit(Resource.Loading())
        try {
            val res = api.getDetails(id)
            if (res.code() != 200) {
                throw Exception("My cima error ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val mediaDetails = doc.select("div.media-details")
            val details = Details(
                id = id,
                title = mediaDetails.select("div.title").text(),
                backdrop = mediaDetails.select("a.poster-image").attr("style")
                    .substringAfter("url(").substringBefore(")"),
                poster = mediaDetails.select("a.poster-image").attr("style")
                    .substringAfter("url(").substringBefore(")"),
                genres = emptyList(),
                overview = mediaDetails.select("div.post-story").select("p").text(),
                releaseDate = "",
                status = "",
                type = "mycima",
                episodes = null,
                tagline = "",
                homepage = "",
                lastAirDate = null,
                imdbId = "",
                rating = 7.1,
                runtime = 45,
                seasons = null,
            )
            emit(Resource.Success(details))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("My cima error ${e.message}"))
        }

    }
}