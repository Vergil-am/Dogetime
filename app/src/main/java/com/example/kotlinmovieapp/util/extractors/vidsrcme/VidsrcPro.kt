package com.example.kotlinmovieapp.util.extractors.vidsrcme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.regex.Pattern

class VidsrcPro {

    @RequiresApi(Build.VERSION_CODES.O)
    fun vidsrcPro(body: String) {
        // Extract the encoded HLS URL
        val encodedHlsUrlPattern = Regex("""file:"([^"]*)"""")
        val encodedHlsUrlMatch = encodedHlsUrlPattern.find(body)
        val encodedHlsUrl = encodedHlsUrlMatch?.groupValues?.get(1) ?: ""

        // Extract the HLS password URL
        val hlsPasswordUrlPattern = Regex("""var pass_path = "([^"]*)";""")
        val hlsPasswordUrlMatch = hlsPasswordUrlPattern.find(body)
        var hlsPasswordUrl = hlsPasswordUrlMatch?.groupValues?.get(1)?.trim() ?: ""

        if (hlsPasswordUrl.startsWith("//")) {
            hlsPasswordUrl = "https:$hlsPasswordUrl"
        }

        val hlsUrl = decodeHlsUrl(encodedHlsUrl)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun decodeHlsUrl(data: String) {
        val formatedB64 = formatHlsB64(data.substring(2))
        val b64Data = Utils().decodeBase64UrlSafe(formatedB64)
        val url = String(b64Data, Charsets.UTF_8)
        Log.e("final url", url)
    }


    private fun formatHlsB64(data: String): String {
        val encodedB64 = data.replace(Regex("/@#@/[^=/]+=="), "")
        if (Pattern.compile("/@#@/[^=/]+==").matcher(encodedB64).find()) {
            return formatHlsB64(encodedB64)
        }
        return encodedB64
    }
}