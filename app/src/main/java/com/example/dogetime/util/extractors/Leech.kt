package com.example.dogetime.util.extractors

import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

class Leech {
    val baseUrl = "https://leech.megamax.me/"

    interface LeechAPI {
        @GET
        suspend fun getVideo(
            @Url url: String,
        ) : Response<String>
    }

    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(LeechAPI::class.java)

    suspend fun getVideoFromUrl(url: String) {
//        Log.e("Url", url)
        val res = api.getVideo(url)
        if (res.code() != 200) {
            throw Exception("Failed to load url")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }
//        Log.e("Doc", doc.toString())
        val video = doc?.select("div")?.select("#app")?.attr("data-page")
//        TODO("There is more to do here")
//        Log.e("Video", video.toString())
    }
}