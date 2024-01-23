package com.example.kotlinmovieapp.util.extractors

import android.util.Log
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

    suspend fun resolveSource(url: String) {
        val res = filemoonAPI.getFilemoon(url)
        if (res.code() != 200) {
            throw Exception("Failed to get filemoon file")
        }
        val regexPattern1 = """eval\(function\(p,a,c,k,e,d\).*?\}\('(.*?)'\.split""".toRegex()
        val match1 = res.body()?.let { regexPattern1.find(it) }?.groupValues?.get(1)
            ?: throw Exception("Failed to retrieve media, could not find eval function..")
        Log.e("Match1", match1)

//        TODO("I have no idea how to fix this regex")
//        val regexPattern2 = Regex("^(.*?}\\);),'(.*?)',(.*?),'(.*?)$")
//            "^(.*?}\\);),'(.*?)',(.*?),'(.*?)$".toRegex()
//            Pattern.compile("""^(.*?}\);)\',(.*?),(.*?),'(.*?)$""")
//        val match2 = regexPattern2.find(match1)
//            regexPattern2.matcher(match1)

//        if (match2 != null) {
//            Log.e("Match2", match2.groupValues.toString())
//        }

    }
}