package com.example.kotlinmovieapp.util.extractors

import android.util.Log
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
    suspend fun getVideoFromUrl(url: String) {
        val res = api.getDocument(url)
        val doc = res.body()?.let { Jsoup.parse(it) }

        val videoUrl = doc?.select("meta[property=og:video]")?.attr("content")

        Log.e("sendvid url", videoUrl.toString())
    }
}