package com.example.dogetime.util.extractors

import android.util.Log
import com.example.dogetime.domain.model.Source
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class Govid {

    private val baseUrl = "https://govid.com/"

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

    suspend fun extractSource(url: String, header: String): Source? {
        try {
            val res = api.getSource(url, header)
            Log.e("govid", res.toString())
            if (res.code() != 200) {
                throw Exception("govid error code ${res.code()}")
            }

            val pattern =
                Regex("""sources:\s*\[.*?file:"(.*?)".*?\]""", RegexOption.DOT_MATCHES_ALL)
            val matchResult = pattern.find(res.body() ?: "")
            val fileUrl = matchResult?.groupValues?.get(1) ?: throw Exception("File not found")
            Log.e("govid", fileUrl)
            return Source(
                url = fileUrl,
                label = "uknown",
                quality = "uknown",
                source = "govid",
                header = null
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}