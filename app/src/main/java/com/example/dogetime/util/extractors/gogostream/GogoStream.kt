package com.example.dogetime.util.extractors.gogostream

import android.util.Base64
import android.util.Log
import com.example.dogetime.domain.model.Source
import com.example.dogetime.domain.use_case.goganime.dto.AjaxDTO
import com.example.dogetime.domain.use_case.goganime.dto.SourcesDTO
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class GogoStream {


    interface API {
        @GET
        suspend fun getDocument(
            @Url url: String,
        ): Response<String>

        @GET
        suspend fun getJson(
            @Url url: String,
            @Header("X-Requested-With") requestedWith: String = "XMLHttpRequest"
        ): Response<String>

    }

    private val api: API = Retrofit.Builder()
        .baseUrl("https://embtaku.pro/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(API::class.java)

    private fun Element.getBytesAfter(item: String) =
        className().substringAfter(item).filter(Char::isDigit).toByteArray()

    suspend fun extractVideos(url: String): List<Source> {
        try {
            val res = api.getDocument(url)

            if (res.code() != 200) {
                throw Exception("gogo-stream error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val iv = doc.selectFirst("div.wrapper")?.getBytesAfter("container-") ?: throw Exception(
                "No iv found"
            )
            val secretKey = doc.selectFirst("body[class]")?.getBytesAfter("container-")
                ?: throw Exception("no Secret key found")
            val decryptionKey =
                doc.selectFirst("div.videocontent")?.getBytesAfter("videocontent-")
                    ?: throw Exception("no decrypt key found")

            val decryptedAjaxParams = cryptoHandler(
                string = doc.selectFirst("script[data-value]")!!.attr("data-value"),
                iv = iv,
                secretKeyString = secretKey,
                encrypt = false
            ).substringAfter("&")

            Log.e("Decrypted", decryptedAjaxParams)
            Log.e("Url", url)

            val httpUrl = url.toHttpUrl()
            val host = "https://" + httpUrl.host
            val id = httpUrl.queryParameter("id") ?: throw Exception("error getting id")

            val encryptedId = cryptoHandler(id, iv, secretKey)
            val token = httpUrl.queryParameter("token")
            val qualityPrefix = if (token != null) "Gogostream - " else "Vidstreaming - "

            val newUrl = "$host/encrypt-ajax.php?id=$encryptedId&$decryptedAjaxParams&alias=$id"

            val jsonString = api.getJson(newUrl).body() ?: throw Exception("Failed to get json")

            val data = Gson().fromJson(jsonString, AjaxDTO::class.java).data

            val sourceList = cryptoHandler(
                data,
                iv,
                decryptionKey,
                false
            )

            val sourcesJson = Gson().fromJson(sourceList, SourcesDTO::class.java)

            val sources = mutableListOf<Source>()

            sources.addAll(
                sourcesJson.source.map {
                    Source(
                        url = it.file,
                        header = null,
                        quality = "Multi",
                        label = "Multi",
                        source = qualityPrefix
                    )
                }
            )
            sources.addAll(
                sourcesJson.source_bk.map {
                    Source(
                        url = it.file,
                        header = null,
                        quality = "Multi",
                        label = "Multi",
                        source = qualityPrefix
                    )
                }
            )

            return sources
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }


    }

    private fun cryptoHandler(
        string: String,
        iv: ByteArray,
        secretKeyString: ByteArray,
        encrypt: Boolean = true,
    ): String {
        val ivParameterSpec = IvParameterSpec(iv)
        val secretKey = SecretKeySpec(secretKeyString, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        return if (!encrypt) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
            String(cipher.doFinal(Base64.decode(string, Base64.DEFAULT)))
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            Base64.encodeToString(cipher.doFinal(string.toByteArray()), Base64.NO_WRAP)

        }


    }
}