package dev.dogetime.ui.watch

import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.WebViewNavigator

/** Applies [isNavigationAllowed] to the WebView's navigations. */
internal class PlayerRequestInterceptor(private val allowedHost: String?) : RequestInterceptor {

    override fun onInterceptUrlRequest(
        request: WebRequest,
        navigator: WebViewNavigator,
    ): WebRequestInterceptResult =
        if (isNavigationAllowed(request.url, request.isForMainFrame, allowedHost)) {
            WebRequestInterceptResult.Allow
        } else {
            WebRequestInterceptResult.Reject
        }
}

/**
 * Keeps the player on its own site. Main-page navigations to any other domain (ad redirects,
 * pop-unders opened in place, links to other apps) are rejected. Iframe requests are left alone,
 * because the video itself is usually served from another domain inside an iframe.
 */
internal fun isNavigationAllowed(url: String, isMainFrame: Boolean, allowedHost: String?): Boolean {
    if (!isMainFrame) return true
    if (url.startsWith("about:")) return true

    val host = hostOf(url)
    return allowedHost != null && host != null &&
        (host == allowedHost || host.endsWith(".$allowedHost"))
}

/** Host of an http(s) URL without "www.", or null for other schemes. */
internal fun hostOf(url: String): String? {
    val scheme = url.substringBefore("://", missingDelimiterValue = "").lowercase()
    if (scheme != "http" && scheme != "https") return null
    return url.substringAfter("://")
        .substringBefore('/')
        .substringBefore('?')
        .substringBefore('#')
        .substringAfterLast('@')
        .substringBefore(':')
        .lowercase()
        .removePrefix("www.")
        .ifEmpty { null }
}
