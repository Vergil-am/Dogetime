package com.example.kotlinmovieapp.util.extractors

import android.util.Log
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

    suspend fun videoFromUrl(url: String) : List<String> {
        val res = api.getVideo(url, baseUrl)
//        Log.e("Res", res.body().toString())
        if (res.code() != 200) {
            throw Exception("Page not found")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }
        val script = doc?.selectFirst("script:containsData(player.src)")?.data()
        ?: return emptyList()

        val videoLink = script.substringAfter(".src(").substringBefore(")")
            .substringAfter("src:").substringAfter('"').substringBefore('"')


//        TODO("The Link extracts successfully but it gives error 403")

//        Log.e("script", script)
        Log.e("mp4upload", videoLink)
        return emptyList()

    }
}