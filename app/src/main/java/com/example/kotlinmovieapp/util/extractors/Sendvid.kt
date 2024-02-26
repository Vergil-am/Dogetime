package com.example.kotlinmovieapp.util.extractors

import com.example.kotlinmovieapp.domain.model.Source
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

class Sendvid {
    private val baseUrl = "https://sendvid.com"
    interface API {
        @GET
        suspend fun getDocument(
            @Url url: String
        ) : Response<String>
    }

    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(API::class.java)
    suspend fun getVideoFromUrl(url: String, quality: String?) : Source {
        val res = api.getDocument(url)
        val doc = res.body()?.let { Jsoup.parse(it) }

        val videoUrl = doc?.select("meta[property=og:video]")?.attr("content") ?: throw Exception("file not found")


        return Source(
            url = videoUrl,
            quality = quality ?: "unknown",
            header = baseUrl,
            label = quality ?: "unknown",
            source = "Sendvid"
        )
    }
}