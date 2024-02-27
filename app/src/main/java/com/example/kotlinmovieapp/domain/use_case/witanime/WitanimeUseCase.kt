package com.example.kotlinmovieapp.domain.use_case.witanime

import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.repository.WitanimeRepository
import com.example.kotlinmovieapp.util.ExtractorProp
import com.example.kotlinmovieapp.util.extractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.ByteString.Companion.decodeBase64
import org.jsoup.Jsoup
import javax.inject.Inject

class WitanimeUseCase @Inject constructor(
    private val repo: WitanimeRepository
) {
    fun getSources(slug: String): Flow<List<Source>> = flow {
//        TODO("I need to modify the slug")
        try {
            val res = repo.getSources(slug)
            val doc = res.body()?.let { Jsoup.parse(it) }
            val links = doc?.select("ul#episode-servers li a")
                ?.distinctBy { it.text().substringBefore(" -") }?.map {element ->
                    ExtractorProp(
                        link = element.attr("data-url").takeUnless(String::isBlank)
                            ?.decodeBase64()?.toByteArray()?.let { String(it, Charsets.UTF_8) } ?: "",
                        quality = element.text().split("-").getOrNull(1)
                    )
                } ?: emptyList()
            val sources = extractor(links)
            emit(sources)
        } catch (e: Exception) {
            e.printStackTrace()
//            throw Exception(e.message)
        }
    }
}