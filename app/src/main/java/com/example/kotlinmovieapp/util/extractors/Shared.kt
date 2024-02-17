package com.example.kotlinmovieapp.util.extractors

import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

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

    suspend fun getVideoFromUrl(url: String) {
        Log.e("Shared url", url)
        val res = api.getDocument(url)
        val doc = res.body()?.let { Jsoup.parse(it) } ?: throw Exception("link invalid")
        val source = doc.selectFirst("source")?.attr("src")
        Log.e("4shared Source", source.toString())
    }
}