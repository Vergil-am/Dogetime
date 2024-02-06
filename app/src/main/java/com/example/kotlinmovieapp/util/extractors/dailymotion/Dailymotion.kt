package com.example.kotlinmovieapp.util.extractors.dailymotion

import android.util.Log
import com.example.kotlinmovieapp.util.extractors.dailymotion.models.DailymotionDTO
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
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

        val jsonUrl =
            "$baseUrl/player/metadata/video/$videoQuery?locale=en-US&dmV1st=$v1st&dmTs=$ts&is_native_app=0"
        val json = api.getDocument(jsonUrl).body()
        val parsed = Gson().fromJson(json, DailymotionDTO::class.java)

        val data = parsed.qualities.auto.map {
            api.getDocument(it.url).body()
//                ?.trimIndent()
        }

        val videoRegex = Regex("""NAME="([^"]+)".+?"(https://[^"]+)""")
        data.map { Data ->
            Log.e("DATA", Data.toString())
            if (Data != null) {
                videoRegex.findAll(Data).map {
                    Log.e("REGEX", it.toString())
                }
            }


        }
//        data.map {
//            if (it != null) {
//                val lines = it.lines().map {line ->
//                    Log.e("Line", line)
//                }
//            }
//        }

    }
}

data class Test(
    val name: String,
    val url: String
)

