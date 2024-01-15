package com.example.kotlinmovieapp.util

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class VidsrcTO {

    suspend fun getStreams(url: String) {
        withContext(Dispatchers.IO) {
            Log.e("getStreams", "ran")
            try {
                val doc = Jsoup.connect(url).get().body()
                val dataId = doc.selectFirst("a[data-id]")?.attr("data-id")
                if (dataId != null) {
                    Log.e("this ran", "")
                    getSources(dataId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    private suspend fun getSources(dataId: String) {
        withContext(Dispatchers.IO) {
            val url = "${Constants.VIDSRC_MULTI}/ajax/embed/episode/$dataId/sources"
            val request = Request.Builder().url(url).build()
            try {
                val response = OkHttpClient().newCall(request).execute().use { res ->
                    val body = res.body?.string() ?: ""
                    val data = Gson().fromJson(body, Map::class.java)
                    Log.e("result", data.toString())
                    return@use res
                }
                return@withContext response
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }
}