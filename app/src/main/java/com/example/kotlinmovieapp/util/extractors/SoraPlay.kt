package com.example.kotlinmovieapp.util.extractors

import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Url

class SoraPlay {
//    TODO("This is not working for somme reason")
    private val baseUrl = "https://yonaplay.org/"

    interface API {

        @GET
        suspend fun getDocument(
            @Url url: String,
            @Header("referer") referer: String
        ): Response<String>
    }

    private val api =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
            .build().create(API::class.java)

    suspend fun extractSources(url: String) {
        val res = api.getDocument(url = url, referer = baseUrl)
        if (res.code() != 200) {
            throw Exception("Soraplay error code ${res.code()}")
        }
        val doc = res.body()?.let { Jsoup.parse(it) } ?: throw Exception("Not found")
        val script = doc.selectFirst("script:containsData(sources)")

        val data = script?.data()?.substringAfter("sources: [")?.substringBefore("],")

        Log.e("Data", data.toString())

    }
}