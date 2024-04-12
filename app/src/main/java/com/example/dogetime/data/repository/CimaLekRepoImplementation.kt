package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.CimaLekAPI
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.domain.repository.CimaLekRepository
import com.example.dogetime.util.Constants
import com.example.dogetime.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class CimaLekRepoImplementation @Inject constructor(
    private val api: CimaLekAPI
) : CimaLekRepository {
    override suspend fun getLatest(): Flow<Resource<List<MovieHome>>> = flow {
        emit(Resource.Loading())
        try {
            val res = api.getLatest()
            if (res.code() != 200) {
                throw Exception("Cimalek error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val slider =
                doc.select("div.home-slider").select("div.glide-track").select("div.content-box")


            val movies = mutableListOf<MovieHome>()

            slider.map {
                val id =
                    it.selectFirst("a")?.attr("href")?.substringBeforeLast("/")
                        ?.substringAfter(Constants.CIMALEK_URL)
                        ?: throw Exception("Cimalek can't get Id")
                movies.add(
                    MovieHome(
                        id = id,
                        poster = it.select("img").attr("src"),
                        title = it.selectFirst("a")?.attr("title") ?: "",
                        type = it.select("span.category").text()
                    )
                )
            }
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("Cimalek error ${e.message}"))
        }

    }
}