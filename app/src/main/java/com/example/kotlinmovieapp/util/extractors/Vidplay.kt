package com.example.kotlinmovieapp.util.extractors

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.util.Utils
import com.google.gson.Gson
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class Vidplay {
    private val keyUrl = "https://raw.githubusercontent.com/KillerDogeEmpire/vidplay-keys/keys/"
    private val providerUrl = "https://vidplay.online"

    interface KeysAPI {
        @GET("keys.json")
        suspend fun fetchKeys(): Response<ArrayList<String>>
    }

    private val keysAPI: KeysAPI =
        Retrofit.Builder().baseUrl(keyUrl).addConverterFactory(GsonConverterFactory.create())
            .build().create(KeysAPI::class.java)

    interface VidplayAPI {
        @GET("/futoken")
        suspend fun getFutoken(
            @Header("Referer") referer: String
        ): Response<String>

        @GET
        suspend fun getVideo(
            @Url url: String
        ): Response<String>
    }

    private val vidplayAPI: VidplayAPI =
        Retrofit.Builder().baseUrl(providerUrl)
            .addConverterFactory(ScalarsConverterFactory.create()).build()
            .create(VidplayAPI::class.java)

    suspend fun resolveSource(url: String): List<Source> {
        val urlData = url.split("?")
        val key = encodeId(urlData[0].split("/e/").last())
        val token = getFuToken(key = key, url = url)
        val newUrl = "${providerUrl}/mediainfo/${token}?${urlData[1]}&autostart=true"
        return getFile(newUrl)


//        TODO("I need to parse subtitles")
//        getSubtitles()


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
        val decodedId = Utils().decodeData(key = key1, data = id.toByteArray())
        val encodedResult = Utils().decodeData(key = key2, data = decodedId)
        val encodedBase64 = Base64.encodeToString(encodedResult, Base64.DEFAULT)
        return encodedBase64.replace("/", "_")
    }

    private suspend fun getFuToken(key: String, url: String): String {
        val res = vidplayAPI.getFutoken(url)
        val regex = Regex("var\\s+k\\s*=\\s*'([^']+)'")
        val matchResult = res.body()?.let { regex.find(it) }
        val fukey = matchResult?.groupValues?.get(1) ?: throw Exception("can't get token")
        val result = StringBuilder()
        result.append(fukey)
        val encodedValues = key.indices.map { i ->
            (fukey[i % fukey.length].code + key[i].code).toString()
        }
        result.append(",").append(encodedValues.joinToString(",")).toString()
        return result.toString()
    }

    private fun getSubtitles() {
//        TODO("I need to get subtitles ")
    }

    private suspend fun getFile(url: String): List<Source> {
        val res = vidplayAPI.getVideo(url)

        val json = Gson().fromJson(res.body(), VidplayFile::class.java)
        val fileUrl = json.result.sources[0].file
        val file = vidplayAPI.getVideo(fileUrl).body() ?: throw Exception("file not found")

        return parseM3u8(file, fileUrl)


    }

    private fun parseM3u8(file: String, url: String): List<Source> {
        val regex = Regex("""#EXT-X-STREAM-INF:BANDWIDTH=\d+?,RESOLUTION=\d+x(\d+)\n(\S+)""")
        val matches = regex.findAll(file)
        val sources = mutableListOf<Source>()
        val baseUrl = url.split("list")[0]
        Log.e("Base url", baseUrl)
        matches.forEach {
            sources.add(
                Source(
                    url = "$baseUrl${it.groupValues[2]}",
                    quality = "${it.groupValues[1]}P",
                    label = "external",
                    source = "Vidplay"
                )
            )

        }
        return sources
    }

}