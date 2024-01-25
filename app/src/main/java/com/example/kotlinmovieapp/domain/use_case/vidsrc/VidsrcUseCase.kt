package com.example.kotlinmovieapp.domain.use_case.vidsrc

import android.util.Log
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.repository.VidsrcToRepository
import com.example.kotlinmovieapp.util.Utils
import com.example.kotlinmovieapp.util.extractors.Filemoon
import com.example.kotlinmovieapp.util.extractors.Vidplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.ByteString.Companion.decodeBase64
import org.jsoup.Jsoup
import java.net.URLDecoder
import javax.inject.Inject


class VidsrcUseCase @Inject constructor(
    private val repo: VidsrcToRepository
) {
    private val key = "8z5Ag5wgagfsOuhz"
    fun getSources(id: Int): Flow<List<Source>> = flow {
        try {
            val res = repo.getMovie(id).body() ?: throw Exception("no response body")
            val doc = Jsoup.parse(res)
            val dataId = doc.selectFirst("a[data-id]")?.attr("data-id")
                ?: throw Exception("Data id not found")

            val sources = repo.getSources(dataId).result
            val result = mutableListOf<Source>()

            sources.map {
                val title = it.title
                val link = repo.getSource(it.id).result.url
                val newLink = link.replace("_", "/").replace("-", "+").decodeBase64()?.toByteArray()
                    ?: throw Exception("can't decode link")
                val decodedLink = URLDecoder.decode(
                    String(
                        Utils().decodeData(data = newLink, key = key), Charsets.UTF_8
                    )
                )
                if (decodedLink.contains("vidplay")) {
                    try {
                        val vidplay = Vidplay().resolveSource(decodedLink)
                        result.add(
                            Source(
                                source = title,
                                url = vidplay.result.sources[0].file,
                                quality = "multi",
                                label = "external"
                            )
                        )
                    } catch (_: Exception){

                    }



                } else if (decodedLink.contains("filemoon")) {
                    try {

                        val filemoon = Filemoon().resolveSource(decodedLink)
                        result.add(
                            Source(
                                source = title,
                                url = filemoon,
                                quality = "1080p",
                                label = "external"
                            )
                        )
                    } catch (_: Exception) {

                    }
                } else {
//                    TODO()
                }


            }
            Log.e("Sources", result.toString())
            emit(result)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}