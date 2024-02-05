package com.example.kotlinmovieapp.domain.use_case.witanime

import android.util.Log
import com.example.kotlinmovieapp.domain.model.VideoLinks
import com.example.kotlinmovieapp.domain.repository.WitanimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.ByteString.Companion.decodeBase64
import org.jsoup.Jsoup
import javax.inject.Inject

class WitanimeUseCase @Inject constructor(
    private val repo: WitanimeRepository
) {
    fun getSources(slug: String): Flow<VideoLinks> = flow {
        try {
            val res = repo.getSources(slug)
            val doc = res.body()?.let { Jsoup.parse(it) }
            val links = doc?.select("ul#episode-servers li a")
                ?.distinctBy { it.text().substringBefore(" -") }?.map {element ->
                    element.attr("data-url").takeUnless(String::isBlank)
                        ?.decodeBase64()?.toByteArray()?.let { String(it, Charsets.UTF_8) }

                }
            Log.e("Links", links.toString())
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}