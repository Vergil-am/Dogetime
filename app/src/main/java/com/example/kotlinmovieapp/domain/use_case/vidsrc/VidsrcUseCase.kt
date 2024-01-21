package com.example.kotlinmovieapp.domain.use_case.vidsrc

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.model.VidsrcSourcesResult
import com.example.kotlinmovieapp.domain.repository.VidsrcToRepository
import com.example.kotlinmovieapp.util.extractors.Vidplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.ByteString.Companion.decodeBase64
import org.jsoup.Jsoup
import java.net.URLDecoder
import javax.inject.Inject
import kotlin.experimental.xor


class VidsrcUseCase @Inject constructor(
    private val repo: VidsrcToRepository
) {
    private val key = "8z5Ag5wgagfsOuhz"
    fun getSources(id: Int): Flow<List<VidsrcSourcesResult>> = flow {
        try {
            val res = repo.getMovie(id).body() ?: throw Exception("no response body")
            val doc = Jsoup.parse(res)
            val dataId = doc.selectFirst("a[data-id]")?.attr("data-id")
                ?: throw Exception("Data id not found")
//            Log.e("DATA ID", dataId.toString())

            val sources = repo.getSources(dataId).result
//            Log.e("Sources", sources.toString())
            val links = sources.map {
                val title = it.title
                val link = repo.getSource(it.id).result.url
//                Log.e("link", link)
                val decodedLink = decodeLink(link)
                Source(
                    source = title,
                    url = decodedLink,
                    quality = if (title == "Vidplay") {
                        "Multi"
                    } else {
                        "1080p"
                    },
                    label = "FHD"
                )

            }
//            Log.e("Links", links.toString())
            emit(emptyList())

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun decodeLink(link: String) : String{
//        Log.e("Link", link)
        val newLink = link.replace("_", "/").replace("-", "+").decodeBase64()?.toByteArray()
            ?: throw Exception("can't decode link")
        val keyBytes = key.toByteArray()
        val s = ByteArray(256) { it.toByte() }
        var j = 0
        for (i in 0 until 256) {
            j = (j + s[i] + keyBytes[i % keyBytes.size]) and 0xFF
            s[i] = s[j].also { s[j] = s[i] }
        }
        val decoded = ByteArray(newLink.size)
        var i = 0
        var k = 0
        for (index in newLink.indices) {
            i = (i + 1) and 0xFF
            k = (k + s[i]) and 0xFF
            s[i] = s[k].also { s[k] = s[i] }
            val t = (s[i] + s[k]) and 0xFF
            decoded[index] = newLink[index] xor s[t]
        }
        val url = URLDecoder.decode(String(decoded, Charsets.UTF_8))

        if (url.contains("vidplay")) {
            Vidplay().resolveSource(url)
        } else if (url.contains("filemoon")) {
            Log.e("Filemoon", url )
        }

//        Log.e("Decoded", url)
//        TODO("the links are good i only need to extract videos from them now")
        return url
    }
}