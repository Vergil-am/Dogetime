package com.example.kotlinmovieapp.util.extractors

import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Url

class Dailymotion {
    private val baseUrl = "https://www.dailymotion.com"
    private val apiUrl = "https://graphql.api.dailymotion.com"

    interface DailyMotionAPI {
        @GET
        suspend fun getDocument(
            @Url url: String
        ): Response<String>
    }

    private val api =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
            .build().create(DailyMotionAPI::class.java)

    suspend fun getVideoFromUrl(url: String) {
        val res = api.getDocument(url)
        if (res.code() != 200) {
            throw Exception("sddsds")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }
        Log.e("Doc", doc.toString())

    }
}