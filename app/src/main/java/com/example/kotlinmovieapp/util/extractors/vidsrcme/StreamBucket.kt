package com.example.kotlinmovieapp.util.extractors.vidsrcme

import android.util.Log
import org.jsoup.Jsoup

class StreamBucket {

    fun streamBucket(body: String)  {
        val doc = Jsoup.parse(body)
        val scripts = doc.select("script")

        scripts.forEach { script ->
            val scriptContent = script.html()
            if (scriptContent.contains("eval(function(h,u,n,t,e,r)")) {
                val startIndex = scriptContent.indexOf("eval(function(h,u,n,t,e,r)")
                val endIndex = scriptContent.indexOf("})", startIndex)
                val extractedContent = scriptContent.substring(startIndex, endIndex + 2)
                Log.e("Extracted content", extractedContent)
            } else {
                return
            }
        }

    }
}