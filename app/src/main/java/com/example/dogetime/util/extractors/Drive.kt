package com.example.dogetime.util.extractors

import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

class Drive {

    private val baseUrl = "https://drive.google.com"

    interface API {
        @GET
        suspend fun getVideo(
            @Url url: String
        ): Response<String>
    }

    private val api =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
            .build().create(API::class.java)

    suspend fun getVideoFromUrl(url: String) {
        val res = api.getVideo(url)
        if (res.code() != 200) {
            throw Exception("Google drive error ${res.code()}")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }
//        TODO()

    }
}