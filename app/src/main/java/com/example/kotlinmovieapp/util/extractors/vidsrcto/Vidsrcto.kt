package com.example.kotlinmovieapp.util.extractors.vidsrcto

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.model.VidSrcSources
import com.example.kotlinmovieapp.domain.model.VidsrcSource
import com.example.kotlinmovieapp.util.extractors.Filemoon
import com.example.kotlinmovieapp.util.extractors.vidplay.Vidplay
import com.example.kotlinmovieapp.util.extractors.vidplay.models.Subtitle
import com.example.kotlinmovieapp.util.extractors.vidplay.models.SubtitlesDTO
import com.example.kotlinmovieapp.util.extractors.vidsrcto.model.VidsrctoReturnType
import com.google.gson.Gson
import okio.ByteString.Companion.decodeBase64
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.net.URLDecoder

class Vidsrcto {
    private val baseUrl = "https://vidsrc.to"
    private val key = "8z5Ag5wgagfsOuhz"


    interface API {
        @GET
        suspend fun getMovie(
            @Url url: String
        ): Response<String>

        @GET("ajax/embed/episode/{dataId}/sources")
        suspend fun getSources(
            @Path("dataId") dataId: String
        ): VidSrcSources

        @GET("ajax/embed/source/{sourceId}")
        suspend fun getSource(
            @Path("sourceId") sourceId: String
        ): VidsrcSource

        @GET
        suspend fun getSubtitles(
            @Url url: String
        ): Response<SubtitlesDTO>
    }

    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    suspend fun getSources(url: String) : VidsrctoReturnType {
        try {
            val res = api.getMovie(url).body()
            val doc = res?.let { Jsoup.parse(it) }
            val dataId = doc?.selectFirst("a[data-id]")?.attr("data-id")
                ?: throw Exception("Data id not found")

            val sources = api.getSources(dataId).result
            val result = mutableListOf<Source>()
            val subtitles = mutableListOf<Subtitle>()

            sources.map {
                val link = api.getSource(it.id).result.url
                val newLink = link.replace("_", "/").replace("-", "+").decodeBase64()?.toByteArray()
                    ?: throw Exception("can't decode link")
                val decodedLink = URLDecoder.decode(
                    String(
                        Utils().decodeData(data = newLink, key = key), Charsets.UTF_8
                    )
                )
                when {
                    decodedLink.contains("vidplay") -> {
                        subtitles.addAll(getSubtitles(decodedLink))
                        result.addAll(Vidplay().resolveSource(decodedLink))
                    }

                    decodedLink.contains("filemoon") -> Filemoon().resolveSource(decodedLink)
                        ?.let { it1 -> result.add(it1) }

                    else -> {}
                }
            }
            return VidsrctoReturnType(
                sources = result,
                subtitles = subtitles
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return VidsrctoReturnType(
                sources = emptyList(),
                subtitles = emptyList()
            )
        }
    }

    private suspend fun getSubtitles(url: String): List<Subtitle> {
        try {
            val urlData = url.split("?")[1]
            val pattern = Regex("info=([^&]+)")
            val matchResult = pattern.find(urlData) ?: throw Exception("Subtitle data not found")

            val subtitlesUrlFormatted = URLDecoder.decode(matchResult.groupValues[1], "UTF-8")
            val res = api.getSubtitles(subtitlesUrlFormatted)

            if (res.code() != 200) {
                throw Exception("Subtitles not found")
            }

            return res.body() ?: throw Exception("failed to return subtitles")
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

    }
}