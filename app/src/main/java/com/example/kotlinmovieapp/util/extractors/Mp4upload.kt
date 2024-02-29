package com.example.kotlinmovieapp.util.extractors

import com.example.kotlinmovieapp.domain.model.Source
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class Mp4upload {
 private val baseUrl = "https://mp4upload.com/"
    interface Mp4UploadAPI {
        @GET
        suspend fun getVideo(
            @Url url: String,
            @Header("Referer") referer: String
        ) : Response<String>
    }

    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(Mp4UploadAPI::class.java)

    suspend fun videoFromUrl(url: String, quality: String?) : Source? {
        try {

        val res = api.getVideo(url, baseUrl)
        if (res.code() != 200) {
            throw Exception("Page not found")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }
        val script = doc?.selectFirst("script:containsData(player.src)")?.data()
        ?: throw Exception("No script found")
        val videoLink = script.substringAfter(".src(").substringBefore(")")
            .substringAfter("src:").substringAfter('"').substringBefore('"')

        return Source(
            url = videoLink,
            header = baseUrl,
            source = "Mp4upload",
            quality = quality ?: "unknown",
            label = quality ?: "unknown"
        )

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }


    }
}