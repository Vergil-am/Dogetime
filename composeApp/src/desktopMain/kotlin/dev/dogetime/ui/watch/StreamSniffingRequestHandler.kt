package dev.dogetime.ui.watch

import dev.dogetime.core.CapturedStream
import dev.dogetime.core.PlayerLog
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceRequestHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.misc.BoolRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse

/**
 * Chromium request handler for the desktop player. It blocks off-site navigations
 * ([isNavigationAllowed]) and reports HLS playlists, including those requested inside the
 * player's iframes, with the headers needed to replay them elsewhere.
 */
internal class StreamSniffingRequestHandler(
    private val allowedHost: String?,
    private val onStreamFound: (CapturedStream) -> Unit,
) : CefRequestHandlerAdapter() {

    private val resourceHandler = object : CefResourceRequestHandlerAdapter() {
        override fun onBeforeResourceLoad(browser: CefBrowser?, frame: CefFrame?, request: CefRequest?): Boolean {
            val url = request?.url ?: return false
            if (isHlsPlaylist(url)) {
                PlayerLog.log("stream: $url")
                onStreamFound(request.toCapturedStream())
            }
            return false
        }

        override fun onResourceResponse(
            browser: CefBrowser?,
            frame: CefFrame?,
            request: CefRequest?,
            response: CefResponse?,
        ): Boolean {
            if (request != null && response != null) logResponse(request, response)
            return false
        }
    }

    override fun onBeforeBrowse(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        userGesture: Boolean,
        isRedirect: Boolean,
    ): Boolean {
        val url = request?.url ?: return false
        val allowed = isNavigationAllowed(url, isMainFrame = frame?.isMain ?: false, allowedHost = allowedHost)
        if (!allowed) PlayerLog.log("blocked navigation: $url")
        // Returning true cancels the navigation.
        return !allowed
    }

    override fun getResourceRequestHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        isNavigation: Boolean,
        isDownload: Boolean,
        requestInitiator: String?,
        disableDefaultHandling: BoolRef?,
    ): CefResourceRequestHandler = resourceHandler
}

/** Logs frame loads (with their security policy) and text/JSON responses, for troubleshooting. */
private fun logResponse(request: CefRequest, response: CefResponse) {
    val type = request.resourceType
    val mime = response.mimeType.orEmpty()
    when {
        type == CefRequest.ResourceType.RT_MAIN_FRAME || type == CefRequest.ResourceType.RT_SUB_FRAME -> {
            val csp = response.getHeaderByName("Content-Security-Policy").orEmpty().ifEmpty { "none" }
            PlayerLog.log("frame ${response.status}: ${request.url} (CSP: $csp)")
        }
        mime.startsWith("text/") || "json" in mime -> PlayerLog.log("response ${response.status} $mime: ${request.url}")
    }
}

private const val FALLBACK_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36"

private fun CefRequest.toCapturedStream(): CapturedStream {
    val raw = HashMap<String, String>()
    getHeaderMap(raw)

    val headers = forwardedHeaders(raw)
    // Chromium adds Referer and User-Agent after this callback, so fill them in when missing.
    referrerURL?.takeIf { it.isNotBlank() }?.let { headers.putIfAbsent("Referer", it) }
    headers.putIfAbsent("User-Agent", FALLBACK_USER_AGENT)
    return CapturedStream(url, headers)
}
