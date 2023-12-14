package com.example.kotlinmovieapp.presentation.video_player
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@RequiresApi(34)
@SuppressLint("SetJavaScriptEnabled", "SourceLockedOrientationActivity")
@Composable
fun VideoPlayer(
   id: String,
   season: Int?,
   episode: Int?,
   windowCompat: WindowInsetsControllerCompat,
) {
   windowCompat.hide(WindowInsetsCompat.Type.systemBars())
   windowCompat.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
   val activity = LocalView.current.context as Activity
   val isFullscreen = remember {
      mutableStateOf(false)
   }
   val url: String = if (season != 0 && episode != 0) {
//      "${Constants.VIDEO_URL}tv/$id/$season/$episode"
      "https://vidsrc.xyz/embed/tv?tmdb=$id&season=$season&episode=$episode"
   }
   else {
//      "${Constants.VIDEO_URL}movie/$id"
      "https://vidsrc.xyz/embed/movie?tmdb=$id"
//     "https://vidsrc.xyz/embed/tv?imdb=tt0944947&season=1&episode=1"
   }
   DisposableEffect(key1 = activity ) {
      activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      onDispose {
         activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      }
   }

      AndroidView(
         modifier = Modifier
            .fillMaxSize(),
         factory = {
            WebView(it).apply {
               layoutParams = ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.MATCH_PARENT
               )

               settings.javaScriptEnabled = true
               settings.domStorageEnabled = true
               webViewClient = object: WebViewClient() {
                  override fun shouldOverrideUrlLoading(
                     view: WebView?,
                     request: WebResourceRequest?
                  ): Boolean {
                     return true
                  }
               }
               webChromeClient = object : WebChromeClient() {
                  var customView: View? = null
                  override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                     super.onShowCustomView(view, callback)
                     isFullscreen.value = true
                     if (this.customView != null) {
                        onHideCustomView()
                        return
                     }
                     this.customView = view
                     (activity.window.decorView as FrameLayout).addView(this.customView,
                        FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT))
                  }

                  override fun onHideCustomView() {
                     super.onHideCustomView()
                     isFullscreen.value = false
                     (activity.window.decorView as FrameLayout).removeView(this.customView)
                     this.customView = null
                  }
               }
               loadUrl(url)
            }
      },
      )
   }


