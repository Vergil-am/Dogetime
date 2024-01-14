package com.example.kotlinmovieapp.presentation.webView

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(34)
@SuppressLint("SetJavaScriptEnabled", "SourceLockedOrientationActivity")
@Composable
fun WebView(
    url: String, windowCompat: WindowInsetsControllerCompat
) {
    val activity = LocalView.current.context as Activity
    var isFullscreen by remember {
        mutableStateOf(false)
    }
    var opened by remember {
        mutableStateOf(false)
    }
    windowCompat.hide(WindowInsetsCompat.Type.systemBars())
    windowCompat.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    BackHandler(opened) {
        opened = true
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?, request: WebResourceRequest?
                    ): Boolean {
                        Log.e("REQUEST", request?.url.toString())
                        return !request?.url.toString().contains("jamiesamewalk.com")
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                    }

//                    override fun shouldInterceptRequest(
//                        view: WebView?, request: WebResourceRequest?
//                    ): WebResourceResponse? {
//                        val domain = request?.url.toString()
//                        val contains = Constants.FilterList.any{string ->
//                            domain.contains(string)
//                        }
//                        return if (contains) {
//                            WebResourceResponse("text/plain", "utf-8", null)
//                        } else {
//                            super.shouldInterceptRequest(view, request)
//                        }
//                    }
                }
                webChromeClient = object : WebChromeClient() {
                    var customView: View? = null
                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        super.onShowCustomView(view, callback)
                        isFullscreen = true
                        if (this.customView != null) {
                            onHideCustomView()
                            return
                        }
                        this.customView = view
                        (activity.window.decorView as FrameLayout).addView(
                            this.customView, FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }

                    override fun onHideCustomView() {
                        super.onHideCustomView()
                        isFullscreen = false
                        (activity.window.decorView as FrameLayout).removeView(this.customView)
                        this.customView = null
                    }

                }
                loadUrl(url)

            }
        },
    )

    if (opened) {
        AlertDialog(onDismissRequest = { opened = false }) {

            Text(text = "Hello")
        }
    }

}
