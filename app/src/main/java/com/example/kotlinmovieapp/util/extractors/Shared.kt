package com.example.kotlinmovieapp.util.extractors

import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import com.example.kotlinmovieapp.domain.model.Source

class Shared {
    private val baseUrl = "https://www.4shared.com/"

    interface API {
        @GET
        suspend fun getDocument(
            @Url url: String
        ): Response<String>
    }

    private val api =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
            .build().create(API::class.java)

    suspend fun getVideoFromUrl(url: String) : Source {
        val res = api.getDocument(url)
        val doc = res.body()?.let { Jsoup.parse(it) } ?: throw Exception("link invalid")
        val videoLink = doc.selectFirst("source")?.attr("src") ?: throw Exception("No file found")
        Log.e("Shared videoLink", videoLink)

         return Source(
             url = videoLink,
             header = baseUrl,
             source = "4Shared",
             quality = "unknown",
             label = "unknown"
         )
    }
}