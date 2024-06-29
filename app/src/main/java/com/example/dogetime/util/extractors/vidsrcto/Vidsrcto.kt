package com.example.dogetime.util.extractors.vidsrcto

import android.util.Log
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.model.VidSrcSources
import com.example.dogetime.domain.model.VidsrcSource
import com.example.dogetime.util.extractors.Filemoon
import com.example.dogetime.util.extractors.vidplay.Vidplay
import com.example.dogetime.util.extractors.vidplay.models.Subtitle
import com.example.dogetime.util.extractors.vidplay.models.SubtitlesDTO
import com.example.dogetime.util.extractors.vidsrcto.model.VidsrctoReturnType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    //    private val key = "8z5Ag5wgagfsOuhz"
    private val key = "WXrUARXb1aDLaZjI"

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

    suspend fun getSources(url: String): VidsrctoReturnType {
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
                    decodedLink.contains("vidplay") ||
                            decodedLink.contains("55a0716b8c") ||
                            decodedLink.contains("e69975b881") ||
                            decodedLink.contains("vid2v11") ||
                            decodedLink.contains("vid30c")
                    -> {
                        coroutineScope {
                            async {
                                result.addAll(Vidplay().resolveSource(decodedLink))
                            }
                            async {
                                subtitles.addAll(getSubtitles(decodedLink))
                            }
                        }
                    }

                    decodedLink.contains("filemoon") || decodedLink.contains("kerapoxy") ->
                        coroutineScope {
                            async {
                                Filemoon().resolveSource(decodedLink)
                                    ?.let { it1 -> result.add(it1) }
                            }
                        }


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
            Log.e("Subtitle url", subtitlesUrlFormatted)
            val res = api.getSubtitles(subtitlesUrlFormatted)
            Log.e("subtitles", res.body().toString())
            if (res.code() != 200) {
                throw Exception("Subtitles not found")
            }


            return res.body()?.toList() ?: throw Exception("no subtitles found")

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

    }
}