package com.example.kotlinmovieapp.util.extractors.vidsrcme

import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class Vidsrcme {
    private val baseUrl = "https://vidsrc.me"
    private val rcpUrl = "https://rcp.vidsrc.me/rcp"

    interface API {
        @GET
        suspend fun getDocument(
            @Url url: String,
            @Header("Referer") referer: String
        ): Response<String>
    }

    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    suspend fun getSources(url: String) {
        val res = api.getDocument(url, baseUrl)
        if (res.code() != 200) {
            throw Exception("vidsrc.me error code ${res.code()}")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }

        val sources = doc?.select("div.server")?.filter{
            it.text().isNotEmpty() && it.hasAttr("data-hash")
        }?.associate {
            it.text() to it.attr("data-hash")
        }

        sources?.map {
            getSource(hash = it.value, provider = it.key)
        }
    }

    private suspend fun getSource(hash : String, provider: String) {
        val res = api.getDocument("$rcpUrl/$hash", baseUrl)
        if (res.code() != 200) {
            throw Exception("$provider error code ${res.code()}")
        }

        val doc = res.body()?.let { Jsoup.parse(it) }
        val encoded = doc?.select("div#hidden")?.attr("data-h") ?: throw Exception("$provider data hash not found")

        Log.e("Encoded", encoded)

    }

}