package com.example.kotlinmovieapp.util.extractors

import android.util.Log
import okhttp3.HttpUrl.Companion.toHttpUrl
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
            throw Exception("Dailymotion error ${res.code()}")
        }
        val internalData =
            res.body()?.substringAfter("\"dmInternalData\":")?.substringBefore("</script>")
                ?: throw Exception("Failed to parse internal data")
        val ts = internalData.substringAfter("\"ts\":").substringBefore(",")
        val v1st = internalData.substringAfter("\"v1st\":\"").substringBefore("\",")

        val videoQuery = url.toHttpUrl().run {
            queryParameter("video") ?: pathSegments.last()
        }

        val jsonUrl = "$baseUrl/player/metadata/video/$videoQuery?locale=en-US&dmV1st=$v1st&dmTs=$ts&is_native_app=0"
        Log.e("JsonUrl", jsonUrl)
    }
}