package com.example.dogetime.util.extractors

import android.util.Base64
import android.util.Log
import com.example.dogetime.domain.model.Source
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class FuseVideo {
    val baseUrl = "https://fusevideo.io/"

    interface FuseVideoAPI {
        @GET
        suspend fun getVideo(
            @Url url: String,
            @Header("Accept-Language") acceptLanguage: String = "en-US,en;q=0.5",
            @Header("Accept") accept: String = "*/*",
        ): Response<String>
    }

    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(FuseVideoAPI::class.java)

    suspend fun getVideo(url: String): List<Source> {
        Log.e("url", url)
        try {
            val res = api.getVideo(url)
            if (res.code() != 200) {
                throw Exception("Fuse video error code ${res.code()}")
            }

            val doc = Jsoup.parse(res.body()!!)
            val dataUrl = doc.selectFirst("script[src~=f/u/u/u/u]")?.attr("src") ?: throw Exception(
                "No data url found"
            )

            val dataDoc = api.getVideo(dataUrl).body().toString()

            val encoded = Regex("atob\\(\"(.*?)\"\\)").find(dataDoc)?.groupValues?.get(1)
            val data = Base64.decode(encoded, Base64.DEFAULT).toString(Charsets.UTF_8)
            val jsonData = data.split("|||")[1].replace("\\", "")
            val videoUrl = Regex("\"(https://.*?/m/.*)\"").find(jsonData)?.groupValues?.get(1)
                ?: throw Exception("No video url found")

            val file = api.getVideo(videoUrl).body() ?: throw Exception("")

            return splitM3u8File(file)


        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    private fun splitM3u8File(file: String): List<Source> {
        val links = mutableListOf<Source>()
        val regex =
            Regex("""#EXT-X-STREAM-INF:BANDWIDTH=\d+,RESOLUTION=\d+x\d+,NAME="(.+?)"\s+(.+)""")

        regex.findAll(file).forEach { match ->
            val name = match.groupValues[1]
            val url = match.groupValues[2]
            links.add(
                Source(
                    url = url,
                    label = "${name}P",
                    quality = "${name}P",
                    source = "Fusevideo",
                    header = "https://fusevideo.io"
                )
            )
        }

        return links
    }
}