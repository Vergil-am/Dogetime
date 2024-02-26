package com.example.kotlinmovieapp.util.extractors

import com.example.kotlinmovieapp.domain.model.Source
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

class Vidmoly {

    private val baseUrl = "https://vidmoly.to"
    interface API {
        @GET
        suspend fun getDocument(
            @Url url: String
        ) : Response<String>
    }

    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(API::class.java)
    suspend fun getVideoFromUrl(url: String, quality: String?) : Source {
        val res = api.getDocument(url)
        val pattern = Regex("""sources:\s*\[.*?file:"(.*?)".*?\]""", RegexOption.DOT_MATCHES_ALL)
        val matchResult = pattern.find(res.body() ?: "")
        val fileUrl = matchResult?.groupValues?.get(1) ?: throw Exception("File not found")

        return Source(
            url = fileUrl,
            quality = quality ?: "unknown",
            header = baseUrl,
            label = quality ?: "unknown",
            source = "Vidmoly"
        )
    }
}