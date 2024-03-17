package com.example.dogetime.util.extractors.dailymotion

import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.extractors.dailymotion.models.DailymotionDTO
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

    suspend fun getVideoFromUrl(url: String) : List<Source> {
        try {
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

            val qualities = parsed.qualities ?: return emptyList()

            val data = qualities.auto.map {
                api.getDocument(it.url).body()
            }

            val videoRegex = Regex("""NAME="([^"]+)".+?"(https://[^"]+)""")
            val sources = mutableListOf<Source>()
            data.map { Data ->
                if (Data != null) {
                    videoRegex.findAll(Data).forEach {
                        val (videoName, videoUrl) = it.destructured
                        sources.add(
                            Source(
                                url = videoUrl,
                                label = videoName,
                                quality = videoName,
                                header = null,
                                source = "DailyMotion"
                            )
                        )
                    }
                }
            }
            return sources
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}
