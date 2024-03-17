package com.example.dogetime.util.extractors.vidsrcme

import android.util.Log
import org.jsoup.Jsoup

class StreamBucket {

    fun streamBucket(body: String)  {
        try {
            val doc = Jsoup.parse(body)
            val scripts = doc.select("script")

            scripts.forEach { script ->
                val scriptContent = script.html()
                Log.e("Stream bucket script", scriptContent)
                if (scriptContent.contains("eval(function(h,u,n,t,e,r)")) {
                    val startIndex = scriptContent.indexOf("eval(function(h,u,n,t,e,r)")
                    val endIndex = scriptContent.indexOf("})", startIndex)
                    val extractedContent = scriptContent.substring(startIndex, endIndex + 2)
                    Log.e("Extracted content", extractedContent)
                } else {
                    throw Exception("No eval function")
//                    return null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            return null
        }

    }
}