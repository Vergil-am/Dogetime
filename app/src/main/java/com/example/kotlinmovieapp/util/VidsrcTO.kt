package com.example.kotlinmovieapp.util

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

data class Source(
    val id: String,
    val title: String
)

class VidsrcTO {

    suspend fun getStreams(url: String) {
        withContext(Dispatchers.IO) {
            Log.e("getStreams", "ran")
            try {
                val doc = Jsoup.connect(url).get().body()
                val dataId = doc.selectFirst("a[data-id]")?.attr("data-id")
                if (dataId != null) {
                    getSources(dataId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private suspend fun getSources(dataId: String) {
        val url = "${Constants.VIDSRC_MULTI}/ajax/embed/episode/$dataId/sources"
        val request = Request.Builder().url(url).build()

        val res = try {

            val response = OkHttpClient().newCall(request).execute().use { res ->
                val body = res.body?.string() ?: ""
                val type = object : TypeToken<List<Source>>() {}.type
                val result: List<Source> =  Gson().fromJson(body, type)
                Log.e("Result", result.toString())


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        TODO("I need to finish this")
    }

}