package com.example.kotlinmovieapp.util.extractors.vidsrcme

class Utils {

    fun decodeSrc(encoded: String, seed: String) : String {

        val encodedBuffer = ByteArray(encoded.length / 2) { encoded.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
        var decoded = ""
        for (i in encodedBuffer.indices) {
            decoded += (encodedBuffer[i].toInt() xor seed[i % seed.length].toInt()).toChar()
        }
        return decoded
    }
}