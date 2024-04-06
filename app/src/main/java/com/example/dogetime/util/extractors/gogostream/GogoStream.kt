package com.example.dogetime.util.extractors.gogostream

import android.util.Base64
import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class GogoStream {


    interface API {
        @GET
        suspend fun getDocument(
            @Url url: String
        ): Response<String>

    }

    private val api: API = Retrofit.Builder()
        .baseUrl("https://embtaku.pro/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(API::class.java)

    suspend fun extractVideos(url: String): List<String> {
        try {
            val res = api.getDocument(url)

            if (res.code() != 200) {
                throw Exception("gogo-stream error code ${res.code()}")
            }
            val doc = Jsoup.parse(res.body()!!)
            val iv = doc.selectFirst("div.wrapper").toString().substringAfter("container-")
                .substringBefore("\">").toByteArray()
            val secretKey = doc.selectFirst("body[class]").toString().substringAfter("container-")
                .substringBefore("\"").toByteArray()
            val decryptionKey =
                doc.selectFirst("div.videocontent").toString().substringAfter("videocontent-")
                    .substringBefore("\"").toByteArray()

            val decryptedAjaxParams = cryptoHandler(
                string = doc.selectFirst("script[data-value]")!!.attr("data-value"),
                iv = iv,
                secretKeyString = decryptionKey,
                encrypt = false
            ).substringAfter("&")

            Log.e("Decrypted", decryptedAjaxParams)


        } catch (e: Exception) {
            e.printStackTrace()
        }





        return emptyList()

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