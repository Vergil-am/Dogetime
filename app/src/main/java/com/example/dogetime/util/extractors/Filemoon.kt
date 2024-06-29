package com.example.dogetime.util.extractors

import android.util.Log
import com.example.dogetime.domain.model.Source
import dev.datlag.jsunpacker.JsUnpacker
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

class Filemoon {
    private val filemoonBaseUrl = "https://filemoon.sx"

    interface FilemoonAPI {
        @GET
        suspend fun getFilemoon(
            @Url url: String
        ): Response<String>
    }

    private val filemoonAPI = Retrofit.Builder().baseUrl(filemoonBaseUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build()
        .create(FilemoonAPI::class.java)

    suspend fun resolveSource(url: String): Source? {
        try {
            val res = filemoonAPI.getFilemoon(url)
            if (res.code() != 200) {
                throw Exception("Failed to get filemoon file status code ${res.code()}")
            }
            val doc = res.body()?.let { Jsoup.parse(it) }
            val jsEval = doc?.selectFirst("script:containsData(eval):containsData(m3u8)")?.data()
                ?: throw Exception("Failed to get filemoon file script not found")
            val unpacked = JsUnpacker.unpackAndCombine(jsEval).orEmpty()

            val fileUrl = unpacked.takeIf(String::isNotBlank)?.substringAfter("{file:\"", "")
                ?.substringBefore("\"}", "")?.takeIf(String::isNotBlank)
                ?: throw Exception("could not get filemoon file")

            Log.e("File url", fileUrl)
            return Source(
                source = "Filemoon",
                url = fileUrl,
                quality = "1080p",
                label = "FHD",
                header = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}