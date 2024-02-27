package com.example.kotlinmovieapp.util

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.util.extractors.Mp4upload
import com.example.kotlinmovieapp.util.extractors.Sendvid
import com.example.kotlinmovieapp.util.extractors.SoraPlay
import com.example.kotlinmovieapp.util.extractors.Streamwish
import com.example.kotlinmovieapp.util.extractors.Uqload
import com.example.kotlinmovieapp.util.extractors.Vidmoly
import com.example.kotlinmovieapp.util.extractors.Vidplay
import com.example.kotlinmovieapp.util.extractors.dailymotion.Dailymotion

suspend fun extractor (links : List<ExtractorProp>) : List<Source> {
    val sources = mutableListOf<Source>()
    links.forEach {
        when {
            it.link.contains("uqload") -> sources.add(Uqload().getVideoFromUrl(it.link, quality = it.quality))
            it.link.contains("mp4upload") -> sources.add(Mp4upload().videoFromUrl(it.link, quality = it.quality))
            it.link.contains("vidmoly") -> sources.add(Vidmoly().getVideoFromUrl(it.link, quality = it.quality))
//            it.link.contains("vidblue") -> Vidblue().getVideoFromUrl(it.link)
//            it.link.contains("leech") -> Leech().getVideoFromUrl(it.link)
            it.link.contains("sendvid") -> sources.add(Sendvid().getVideoFromUrl(it.link, quality = it.quality))
            it.link.contains("dailymotion") -> sources.addAll(Dailymotion().getVideoFromUrl(it.link))
            it.link.contains("cdnwish") -> sources.add(Streamwish().getVideoFromUrl(it.link))
            it.link.contains("vidplay") -> sources.addAll(Vidplay().resolveSource(it.link))
            it.link.contains("yonaplay") -> sources.addAll(SoraPlay().extractSources(it.link))
            it.link.contains("yourupload") -> sources.add(Source(url = it.link, quality = it.quality ?: "unknown", label = it.quality ?: "unknown", header = "https://www.yourupload.com/", source = "yourupload"))
        }
    }

    Log.e("Extracted links", sources.toString())

    return sources
}