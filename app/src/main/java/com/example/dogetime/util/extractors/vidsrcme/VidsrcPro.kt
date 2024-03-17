package com.example.dogetime.util.extractors.vidsrcme

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dogetime.domain.model.Source
import java.util.regex.Pattern

class VidsrcPro {

    @RequiresApi(Build.VERSION_CODES.O)
    fun vidsrcPro(body: String) : Source? {
        try {
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

            return decodeHlsUrl(encodedHlsUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun decodeHlsUrl(data: String): Source {
        val formattedB64 = formatHlsB64(data.substring(2))
        val b64Data = Utils().decodeBase64UrlSafe(formattedB64)
        val url = String(b64Data, Charsets.UTF_8)

        return Source(
            source = "Vidsrc pro",
            url = url,
            label = "FHD",
            quality = "1080p",
            header = null
        )

    }
    private fun formatHlsB64(data: String): String {
        val encodedB64 = data.replace(Regex("/@#@/[^=/]+=="), "")
        if (Pattern.compile("/@#@/[^=/]+==").matcher(encodedB64).find()) {
            return formatHlsB64(encodedB64)
        }
        return encodedB64
    }
}


