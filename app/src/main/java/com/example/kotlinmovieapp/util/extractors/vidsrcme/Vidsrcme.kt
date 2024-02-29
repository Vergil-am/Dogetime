package com.example.kotlinmovieapp.util.extractors.vidsrcme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.kotlinmovieapp.domain.model.Source
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
            @Header("Referer") referer: String,
        ): Response<String>
    }

    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSources(url: String) : List<Source> {
        try {

        val res = api.getDocument(url, baseUrl)
        if (res.code() != 200) {
            throw Exception("vidsrc.me error code ${res.code()}")
        }
        val doc = res.body()?.let { Jsoup.parse(it) }
        val links = doc?.select("div.server")?.filter{
            it.text().isNotEmpty() && it.hasAttr("data-hash")
        }?.associate {
            it.text() to it.attr("data-hash")
        }
        val sources = mutableListOf<Source>()
        links?.map {
            sources.addAll(getSource(hash = it.value, provider = it.key))
        }

        return sources

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getSource(hash : String, provider: String) : List<Source> {
        val res = api.getDocument("$rcpUrl/$hash", baseUrl)
        if (res.code() != 200) {
            throw Exception("$provider error code ${res.code()}")
        }

        val doc = res.body()?.let { Jsoup.parse(it) } ?: throw Exception("$provider no body found")

        val encoded = doc.select("div#hidden").attr("data-h") ?: throw Exception("$provider data hash not found")
        val seed = doc.select("body").attr("data-i")
        var decoded = Utils().decodeSrc(encoded, seed)

        if (decoded.startsWith("//")) {
            decoded = "https:$decoded"
        }
        return getSourceUrl(decoded)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getSourceUrl(decoded: String) : List<Source> {
        val res = api.getDocument(decoded, baseUrl)
        val source = res.toString().substringAfter("url=").substringBefore("}")
        Log.e("Vidsrc.me", source)
        val sources = mutableListOf<Source>()
        if (res.body() == null) {
            throw Exception("vidsrc Pro error code ${res.code()}")
        }

        when {
            source.contains("vidsrc.stream") -> VidsrcPro().vidsrcPro(res.body()!!)
                ?.let { sources.add(it) }
            source.contains("streambucket") -> StreamBucket().streamBucket(res.body()!!)
        }
        return sources
    }

}