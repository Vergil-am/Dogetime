package dev.dogetime.ui.watch

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.multiplatform.webview.web.AccompanistWebViewClient
import com.multiplatform.webview.web.PlatformWebViewParams
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import dev.dogetime.core.CapturedStream
import com.multiplatform.webview.web.WebView as ComposeWebView

@Composable
actual fun PlayerWebView(
    state: WebViewState,
    navigator: WebViewNavigator,
    allowedHost: String?,
    onStreamFound: (CapturedStream) -> Unit,
    modifier: Modifier,
) {
    val currentOnStreamFound by rememberUpdatedState(onStreamFound)
    val client = remember { StreamSniffingWebViewClient { currentOnStreamFound(it) } }
    ComposeWebView(
        state = state,
        navigator = navigator,
        modifier = modifier,
        onCreated = { webView: WebView -> webView.addAutoPlayScript() },
        platformWebViewParams = PlatformWebViewParams(client = client),
    )
}

/** Runs [AUTO_PLAY_JS] in every frame the page loads, including the player's iframes. */
private fun WebView.addAutoPlayScript() {
    if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
        Log.w("dogetime", "WebView can't run scripts in iframes, so play has to be pressed by hand")
        return
    }
    WebViewCompat.addDocumentStartJavaScript(this, AUTO_PLAY_JS, setOf("*"))
}

/**
 * Reports HLS playlists the page requests, including inside the player's iframes, with the
 * headers needed to replay them in another app. Navigation filtering stays with the base client,
 * which applies the navigator's request interceptor.
 */
private class StreamSniffingWebViewClient(
    private val onStreamFound: (CapturedStream) -> Unit,
) : AccompanistWebViewClient() {

    private val mainThread = Handler(Looper.getMainLooper())

    // Called on a background thread, so the stream is handed over on the main thread.
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val url = request?.url?.toString()
        if (view != null && url != null && isHlsPlaylist(url)) {
            val stream = request.toCapturedStream(view)
            mainThread.post { onStreamFound(stream) }
        }
        return super.shouldInterceptRequest(view, request)
    }
}

private fun WebResourceRequest.toCapturedStream(view: WebView): CapturedStream {
    val url = url.toString()
    val headers = forwardedHeaders(requestHeaders.orEmpty())
    // The WebView adds cookies and its User-Agent after this callback, so they are filled in here.
    CookieManager.getInstance().getCookie(url)?.takeIf { it.isNotBlank() }?.let { headers.putIfAbsent("Cookie", it) }
    headers.putIfAbsent("User-Agent", WebSettings.getDefaultUserAgent(view.context))
    return CapturedStream(url, headers)
}
