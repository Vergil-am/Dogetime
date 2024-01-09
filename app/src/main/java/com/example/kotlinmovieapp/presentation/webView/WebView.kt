package com.example.kotlinmovieapp.presentation.webView

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun WebView(
    url: String, windowCompat: WindowInsetsControllerCompat, viewModel: WebViewViewModel
) {
    val state by viewModel.state.collectAsState()

    val activity = LocalView.current.context as Activity
    val isFullscreen = remember {
        mutableStateOf(false)
    }
    windowCompat.hide(WindowInsetsCompat.Type.systemBars())
    windowCompat.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    DisposableEffect(key1 = activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            viewModel.updateState(null)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
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
                        (activity.window.decorView as FrameLayout).addView(
                            this.customView, FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }

                    override fun onHideCustomView() {
                        super.onHideCustomView()
                        isFullscreen.value = false
                        (activity.window.decorView as FrameLayout).removeView(this.customView)
                        this.customView = null
                    }
                }
                state?.let { savedState ->
                    restoreState(savedState) ?: loadUrl(url)
                }
                loadUrl(url)

            }
        },
        update = {view ->
            val bundle = Bundle()
            view.saveState(bundle)
            viewModel.updateState(bundle)
        }
    )


}


