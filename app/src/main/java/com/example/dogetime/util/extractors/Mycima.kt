package com.example.dogetime.util.extractors

import com.example.dogetime.domain.model.Source
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class Mycima {

    private val baseUrl = "https://vidbom.com/"

    interface API {
        @GET
        suspend fun getSource(
            @Url url: String,
            @Header("Referer") header: String
        ): Response<String>
    }

    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(API::class.java)

    suspend fun extractSource(url: String, header: String, source: String): Source? {
        try {
            val res = api.getSource(url, header)
            if (res.code() != 200) {
                throw Exception("Vidbom error code ${res.code()}")
            }

            // File URl
            val pattern =
                Regex("""sources:\s*\[.*?file:"(.*?)".*?\]""", RegexOption.DOT_MATCHES_ALL)
            val matchResult = pattern.find(res.body()!!)
            val fileUrl = matchResult?.groupValues?.get(1) ?: throw Exception("File not found")
            // label
            val labelPattern = Regex("""file:"(.*?)",label:"([^"]+)"""")
            val labelMatchResult = labelPattern.find(res.body()!!)
            val label = labelMatchResult?.groupValues?.get(2)
            return Source(
                url = fileUrl,
                label = label ?: "unknown",
                quality = label ?: "unknown",
                source = source,
                header = null
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}