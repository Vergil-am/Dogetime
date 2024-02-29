package com.example.kotlinmovieapp.util.extractors.vidsrcto

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.model.VidSrcSources
import com.example.kotlinmovieapp.domain.model.VidsrcSource
import com.example.kotlinmovieapp.util.extractors.Filemoon
import com.example.kotlinmovieapp.util.extractors.Vidplay
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
    }

    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    suspend fun getSources(url: String): List<Source> {
        Log.e("VIDSRC TO", url)
        try {
            val res = api.getMovie(url).body()
            val doc = res?.let { Jsoup.parse(it) }
            val dataId = doc?.selectFirst("a[data-id]")?.attr("data-id")
                ?: throw Exception("Data id not found")

            val sources = api.getSources(dataId).result
            val result = mutableListOf<Source>()

            sources.map {
                val title = it.title
                val link = api.getSource(it.id).result.url
                val newLink = link.replace("_", "/").replace("-", "+").decodeBase64()?.toByteArray()
                    ?: throw Exception("can't decode link")
                val decodedLink = URLDecoder.decode(
                    String(
                        Utils().decodeData(data = newLink, key = key), Charsets.UTF_8
                    )
                )
                if (decodedLink.contains("vidplay")) {
                    try {
                        val vidplay = Vidplay().resolveSource(decodedLink)
                        vidplay.forEach { source ->
                            result.add(source)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }


                } else if (decodedLink.contains("filemoon")) {
                    try {
                        val filemoon = Filemoon().resolveSource(decodedLink)
                        result.add(
                            Source(
                                source = title,
                                url = filemoon,
                                quality = "1080p",
                                label = "external",
                                header = null
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
//                    TODO()
                }

            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}