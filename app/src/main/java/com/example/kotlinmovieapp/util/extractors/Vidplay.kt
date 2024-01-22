package com.example.kotlinmovieapp.util.extractors

import android.util.Base64
import android.util.Log
import com.example.kotlinmovieapp.util.Utils
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

class Vidplay {
    private val keyUrl = "https://raw.githubusercontent.com/Ciarands/vidsrc-keys/main/"
    private val providerUrl = "https://vidplay.online"

    interface KeysAPI {
        @GET("keys.json")
        suspend fun fetchKeys(): Response<ArrayList<String>>
    }

    private val keysAPI: KeysAPI =
        Retrofit.Builder().baseUrl(keyUrl).addConverterFactory(GsonConverterFactory.create())
            .build().create(KeysAPI::class.java)

    interface TokenAPI {
        @GET("/futoken")
        suspend fun getFutoken(
            @Header("Referer") referer: String
        ): Response<String>
    }

    private val tokenAPI: TokenAPI = Retrofit.Builder().baseUrl(providerUrl)
        .addConverterFactory(ScalarsConverterFactory.create()).build().create(TokenAPI::class.java)

    suspend fun resolveSource(url: String) {
        val urlData = url.split("?")
        val key = encodeId(urlData[0].split("/e/").last())
        Log.e("key", key)
        getFuToken(key = key, url = url)
    }

    private suspend fun encodeId(id: String): String {
        val res = keysAPI.fetchKeys()

        if (res.code() != 200) {
            throw Exception("failed to fetch decryption keys")
        }
        val key1 = res.body()?.get(0)
        val key2 = res.body()?.get(1)
        if (key1 == null || key2 == null) {
            throw Exception("Could not fetch decryption keys")
        }
        // These are correct
        val decodedId = Utils().decodeData(key = key1, data = id.toByteArray())
        val encodedResult = Utils().decodeData(key = key2, data = decodedId)
        // I think this works?
        val encodedBase64 = Base64.encodeToString(encodedResult, Base64.DEFAULT)
        return encodedBase64.replace("/", "_")
    }

    private suspend fun getFuToken(key: String, url: String) {
       val res = tokenAPI.getFutoken(url)
        val regex = Regex("var\\s+k\\s*=\\s*'([^']+)'")
        val matchResult = res.body()?.let { regex.find(it) }
        val fukey = matchResult?.groupValues?.get(1)
        if (fukey != null) {
            Log.e("fukey", fukey)
        }
    }

}