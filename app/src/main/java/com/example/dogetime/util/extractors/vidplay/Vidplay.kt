package com.example.dogetime.util.extractors.vidplay

import android.util.Base64
import android.util.Log
import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.extractors.vidplay.models.GithubKeysDTO
import com.example.dogetime.util.extractors.vidplay.models.VidplayFile
import com.example.dogetime.util.extractors.vidsrcto.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

class Vidplay {
    //    private val keyUrl = "https://raw.githubusercontent.com/KillerDogeEmpire/vidplay-keys/keys/"
//    private val keyUrl = "https://raw.githubusercontent.com/Ciarands/vidsrc-keys/main/"
    private val keyUrl = "https://github.com/Ciarands/vidsrc-keys/blob/main/"
    private val providerUrl = "https://vidplay.online"

    interface KeysAPI {
        @GET("keys.json")
        suspend fun fetchKeys(
            @Header("Accept") accept: String = "application/json"
        ): Response<GithubKeysDTO>
    }

    private val gson = GsonBuilder().setLenient().create()
    private val keysAPI: KeysAPI =
        Retrofit.Builder().baseUrl(keyUrl).addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(KeysAPI::class.java)

    interface VidplayAPI {
        @GET("/futoken")
        suspend fun getFutoken(
            @Header("Referer") referer: String
        ): Response<String>

        @GET
        suspend fun getVideo(
            @Url url: String,
            @Header("Referer") referer: String?
        ): Response<String>
    }

    private val vidplayAPI: VidplayAPI =
        Retrofit.Builder().baseUrl(providerUrl)
            .addConverterFactory(ScalarsConverterFactory.create()).build()
            .create(VidplayAPI::class.java)

    suspend fun resolveSource(url: String): List<Source> {
        return try {
            val urlData = url.split("?")
            val key = encodeId(urlData[0].split("/e/").last())
            val token = getFuToken(key = key, url = url)
            val newUrl = "${providerUrl}/mediainfo/${token}?${urlData[1]}&autostart=true"
            getFile(newUrl, url)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun encodeId(id: String): String {
        val res = keysAPI.fetchKeys()
        if (res.code() != 200) {
            throw Exception("failed to fetch decryption keys")
        }
        val listType = object : TypeToken<List<String>>() {}.type
        val keys =
            gson.fromJson<List<String?>>(res.body()?.payload?.blob?.rawLines?.get(0), listType)

        val key1 = keys[0]
        val key2 = keys[1]

        if (key1 == null || key2 == null) {
            throw Exception("Could not fetch decryption keys")
        }
        val decodedId = Utils().decodeData(key = key1, data = id.toByteArray())
        val encodedResult = Utils().decodeData(key = key2, data = decodedId)
        val encodedBase64 = Base64.encodeToString(encodedResult, Base64.DEFAULT)
        return encodedBase64.replace("/", "_")
    }

    private suspend fun getFuToken(key: String, url: String): String {
        val res = vidplayAPI.getFutoken(referer = url)
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

    private suspend fun getFile(url: String, referer: String): List<Source> {
        val res = vidplayAPI.getVideo(url, referer = url)
        Log.e("Response", res.body().toString())
        val json = Gson().fromJson(res.body(), VidplayFile::class.java)
        val fileUrl = json.result.sources[0].file
        val file = vidplayAPI.getVideo(fileUrl, referer).body() ?: throw Exception("file not found")
        return parseM3u8(file, fileUrl, referer)


    }

    private fun parseM3u8(file: String, url: String, referer: String): List<Source> {
        val regex = Regex("""#EXT-X-STREAM-INF:BANDWIDTH=\d+?,RESOLUTION=\d+x(\d+)\n(\S+)""")
        val matches = regex.findAll(file)
        val sources = mutableListOf<Source>()
        val baseUrl = url.split("list")[0]
        matches.forEach {
            sources.add(
                Source(
                    url = "$baseUrl${it.groupValues[2]}",
                    quality = "${it.groupValues[1]}P",
                    label = when (it.groupValues[1]) {
                        "1080" -> "FHD"
                        "720" -> "HD"
                        "360" -> "SD"
                        else -> {
                            "Unknown"
                        }
                    },
                    source = "Vidplay",
                    header = referer
                )
            )

        }
        return sources
    }

}