package com.example.kotlinmovieapp.presentation.video_player

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoPlayer(
   id: String,
   season: Int?,
   episode: Int?
) {
   val url: String = if (season != 0 && episode != 0) {
      "https://vidsrc.to/embed/tv/$id/$season/$episode"
   } else {
      "https://vidsrc.to/embed/movie/$id"
   }
   AndroidView(factory = {
    WebView(it).apply {
       layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
       )
       webViewClient = object : WebViewClient() {
          override fun shouldOverrideUrlLoading(
             view: WebView?,
             request: WebResourceRequest?
          ): Boolean {
             return true
          }
       }
       loadUrl(url)
    }
   },
      update = {
         it.loadUrl(url)
         it.settings.javaScriptEnabled = true
      }
   )
}
