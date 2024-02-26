package com.example.kotlinmovieapp.util.extractors

import com.example.kotlinmovieapp.domain.model.Source
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class Uqload {
    private val baseUrl = "https://uqload.co/"

    interface UqloadAPI {
        @GET
        suspend fun getVideo(
            @Url url : String,
            @Header("Referer") referer: String
        ): Response<String>
    }

    private val api =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
            .build().create(UqloadAPI::class.java)

    suspend fun getVideoFromUrl(url: String, quality: String?) : Source {
        val res = api.getVideo(url = url, referer = baseUrl)
        if (res.code() != 200) {
            throw Exception("Could not get page")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }

        val script = doc?.selectFirst("script:containsData(sources:)")?.data()
            ?: throw Exception("Video not available")

        val videoUrl = script.substringAfter("sources: [\"").substringBefore('"')
            .takeIf(String::isNotBlank)
            ?.takeIf { it.startsWith("http") } ?: throw Exception("No video link found")

        return Source(
            url = videoUrl,
            quality = quality ?: "unknown",
            header = baseUrl,
            label = quality ?: "unknown",
            source = "Uqload"
        )
    }
}