package com.example.kotlinmovieapp.util.extractors

import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class SoraPlay {
//    TODO("This is not working for somme reason")
    private val baseUrl = "https://yonaplay.org/"
    private val referer = "https://witanime.one/"

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
        val res = api.getDocument(url = url, referer = referer)
        if (res.code() != 200) {
            throw Exception("Soraplay error code ${res.code()}")
        }
        val doc = res.body()?.let { Jsoup.parse(it) } ?: throw Exception("Not found")
        val data = doc.select("div.OptionsLangDisp").select("li").map {
            it.attr("onclick").substringAfter(" go_to_player('").substringBefore("')")
        }
        Log.e("Yona play links", data.toString())
        val sources = data.map {
            if (it.contains("4shared")) {
                Shared().getVideoFromUrl(it)
            } else if (it.contains("drive.google")) {
                Log.e("Drive", it)
            }
        }


    }
}