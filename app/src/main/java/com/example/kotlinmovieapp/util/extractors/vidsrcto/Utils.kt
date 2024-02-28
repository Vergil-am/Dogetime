package com.example.kotlinmovieapp.util.extractors.vidsrcto

import kotlin.experimental.xor

class Utils {

    // For vidsrc.to
    fun decodeData(data: ByteArray, key: String): ByteArray {
        val keyBytes = key.toByteArray()
        val s = ByteArray(256) { it.toByte() }
        var j = 0
        for (i in 0 until 256) {
            j = (j + s[i] + keyBytes[i % keyBytes.size]) and 0xFF
            s[i] = s[j].also { s[j] = s[i] }
        }
        val decoded = ByteArray(data.size)
        var i = 0
        var k = 0
        for (index in data.indices) {
            i = (i + 1) and 0xFF
            k = (k + s[i]) and 0xFF
            s[i] = s[k].also { s[k] = s[i] }
            val t = (s[i] + s[k]) and 0xFF
            decoded[index] = data[index] xor s[t]
        }
        return decoded
    }
}