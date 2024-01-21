package com.example.kotlinmovieapp.util.extractors

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Vidplay {
    private val KEY_URL = "https://raw.githubusercontent.com/Ciarands/vidsrc-keys/main/"

    interface ApiService {
        @GET("keys.json")
        suspend fun fetchKeys(): Response<ArrayList<String>>
    }

    private val api: ApiService
    init {

        val retrofit = Retrofit.Builder()
            .baseUrl(KEY_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)
    }

    fun resolveSource(url: String) {
        val urlData = url.split("?")
        Log.e("DATA", urlData.toString())
        val key = encodeId(urlData[0].split("/e/").last())
    }

    private fun encodeId(id: String) {
        Log.e("ID", id)
        CoroutineScope(Dispatchers.IO).launch {
            val res = api.fetchKeys()

            if (res.code() != 200) {
                throw Exception("failed to fetch decryption keys")
            }
            val key1 = res.body()?.get(0)
            val key2 = res.body()?.get(1)

//            TODO( "STill a lot todo here")
            Log.e("res", res.body().toString())
        }


    }
}