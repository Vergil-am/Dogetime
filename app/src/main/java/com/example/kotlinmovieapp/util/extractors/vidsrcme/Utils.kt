package com.example.kotlinmovieapp.util.extractors.vidsrcme

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Base64

class Utils {

    fun decodeSrc(encoded: String, seed: String) : String {

        val encodedBuffer = ByteArray(encoded.length / 2) { encoded.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
        var decoded = ""
        for (i in encodedBuffer.indices) {
            decoded += (encodedBuffer[i].toInt() xor seed[i % seed.length].toInt()).toChar()
        }
        return decoded
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decodeBase64UrlSafe(s: String): ByteArray {
        val standardizedInput = s.replace('_', '/').replace('-', '+')
        return Base64.getDecoder().decode(standardizedInput)
    }

}