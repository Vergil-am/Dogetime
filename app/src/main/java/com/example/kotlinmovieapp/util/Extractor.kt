package com.example.kotlinmovieapp.util

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.util.extractors.Mp4upload
import com.example.kotlinmovieapp.util.extractors.Sendvid
import com.example.kotlinmovieapp.util.extractors.Shared
import com.example.kotlinmovieapp.util.extractors.SoraPlay
import com.example.kotlinmovieapp.util.extractors.Streamwish
import com.example.kotlinmovieapp.util.extractors.Uqload
import com.example.kotlinmovieapp.util.extractors.Vidblue
import com.example.kotlinmovieapp.util.extractors.Vidmoly
import com.example.kotlinmovieapp.util.extractors.vidplay.Vidplay
import com.example.kotlinmovieapp.util.extractors.dailymotion.Dailymotion

suspend fun extractor (links : List<ExtractorProp>) : List<Source> {
    val sources = mutableListOf<Source>()
    links.forEach {
        when {
            it.link.contains("uqload") -> Uqload().getVideoFromUrl(it.link, quality = it.quality)
                ?.let { it1 -> sources.add(it1) }
            it.link.contains("mp4upload") -> Mp4upload().videoFromUrl(it.link, quality = it.quality)
                ?.let { it1 -> sources.add(it1) }
            it.link.contains("vidmoly") -> Vidmoly().getVideoFromUrl(it.link, quality = it.quality)
                ?.let { it1 -> sources.add(it1) }
            it.link.contains("vidblue") -> Vidblue().getVideoFromUrl(it.link)
                ?.let { it1 -> sources.add(it1) }
//            it.link.contains("leech") -> Leech().getVideoFromUrl(it.link)
            it.link.contains("sendvid") -> Sendvid().getVideoFromUrl(it.link, quality = it.quality)
                ?.let { it1 -> sources.add(it1) }
            it.link.contains("dailymotion") -> sources.addAll(Dailymotion().getVideoFromUrl(it.link))
            it.link.contains("cdnwish") -> Streamwish().getVideoFromUrl(it.link)
                ?.let { it1 -> sources.add(it1) }
            it.link.contains("vidplay") -> sources.addAll(Vidplay().resolveSource(it.link))
            it.link.contains("yonaplay") -> sources.addAll(SoraPlay().extractSources(it.link))
            it.link.contains("yourupload") -> sources.add(Source(url = it.link, quality = it.quality ?: "unknown", label = it.quality ?: "unknown", header = "https://www.yourupload.com/", source = "yourupload"))
            it.link.contains("4shared") -> Shared().getVideoFromUrl(it.link)
                ?.let { it1 -> sources.add(it1) }
        }
    }

    Log.e("Extracted links", sources.toString())

    return sources
}