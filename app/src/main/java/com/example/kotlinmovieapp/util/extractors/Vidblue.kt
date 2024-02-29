package com.example.kotlinmovieapp.util.extractors

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url
import com.example.kotlinmovieapp.domain.model.Source

class Vidblue {
    private val baseUrl = "https://a3.vidblue.online"
    interface API {
        @GET
        @Headers("User-Agent: insomnia/8.6.1")
        suspend fun getDocument(
            @Url url: String,
        ) : Response<String>
    }

    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(API::class.java)
    suspend fun getVideoFromUrl(url: String) : Source? {
        try {
            val res = api.getDocument(url)
            val pattern =
                Regex("""sources:\s*\[.*?file:"(.*?)".*?\]""", RegexOption.DOT_MATCHES_ALL)
            val matchResult = pattern.find(res.body() ?: "")
            val fileUrl = matchResult?.groupValues?.get(1)

            return fileUrl?.let {
                Source(
                    url = it,
                    source = "Vidblue",
                    quality = "Unknown",
                    label = "Unknown",
                    header = baseUrl
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}