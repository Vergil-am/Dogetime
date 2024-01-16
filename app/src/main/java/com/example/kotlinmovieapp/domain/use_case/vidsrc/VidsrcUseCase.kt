package com.example.kotlinmovieapp.domain.use_case.vidsrc

import android.util.Log
import com.example.kotlinmovieapp.domain.model.VidsrcSourcesResult
import com.example.kotlinmovieapp.domain.repository.VidsrcToRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject


class VidsrcUseCase @Inject constructor(
    private val repo: VidsrcToRepository
) {

    fun getSources(id: Int): Flow<List<VidsrcSourcesResult>> = flow {
        try {
            val res = repo.getMovie(id).body() ?: throw Exception("no response body")
            val doc = Jsoup.parse(res)
            val dataId = doc.selectFirst("a[data-id]")?.attr("data-id")
            Log.e("DATA ID", dataId.toString())

            if (dataId == null) {
                throw Exception("Data id not found")
            }
            val sources = repo.getSources(dataId).result
            Log.e("Sources", sources.toString())
            val links = sources.map {
                val link = repo.getSource(it.id).result
                Log.e("Link", link.url)
                link.url
            }
            Log.e("Links", links.toString())
            emit(emptyList())

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}