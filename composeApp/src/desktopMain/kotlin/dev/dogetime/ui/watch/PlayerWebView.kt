package dev.dogetime.ui.watch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import dev.datlag.kcef.KCEFBrowser
import dev.dogetime.core.CapturedStream
import dev.dogetime.core.PlayerLog
import kotlinx.coroutines.delay

private const val AUTO_PLAY_INTERVAL_MS = 1_000L

@Composable
actual fun PlayerWebView(
    state: WebViewState,
    navigator: WebViewNavigator,
    allowedHost: String?,
    onStreamFound: (CapturedStream) -> Unit,
    modifier: Modifier,
) {
    val currentOnStreamFound by rememberUpdatedState(onStreamFound)
    // Not Compose state: the library reports the browser while a frame is being applied, which
    // doesn't trigger a recomposition, so effects keyed on it would never see it. The effects
    // below read it when they need it instead.
    val browser = remember { BrowserHolder() }

    // Frames (including the player's iframes) load at different times, so the auto-play script
    // is re-applied every second. Each frame runs it only once.
    LaunchedEffect(browser) {
        while (true) {
            browser.current?.let { runCatching { it.runInAllFrames(AUTO_PLAY_JS) } }
            delay(AUTO_PLAY_INTERVAL_MS)
        }
    }

    // The library only disposes the client when the WebView leaves the screen, which leaves the
    // browser running, so it is closed here too. Only ever when leaving the screen: closing a
    // browser that is still on screen makes Chromium close the app window.
    DisposableEffect(browser) {
        onDispose {
            browser.current?.let {
                PlayerLog.log("closing player page")
                it.dispose()
            }
        }
    }

    WebView(
        state = state,
        navigator = navigator,
        modifier = modifier,
        onCreated = { created: KCEFBrowser ->
            browser.current = created
            // JCEF keeps the first request handler it is given. This one is registered before the
            // library's, so it applies the navigation filter itself and also watches for streams.
            created.client.addRequestHandler(
                StreamSniffingRequestHandler(
                    allowedHost = allowedHost,
                    onStreamFound = { currentOnStreamFound(it) },
                ),
            )
        },
    )
}

private class BrowserHolder {
    var current: KCEFBrowser? = null
}

private fun KCEFBrowser.runInAllFrames(script: String) {
    for (id in frameIdentifiers) {
        val frame = getFrameByIdentifier(id) ?: continue
        frame.executeJavaScript(script, frame.url, 0)
    }
}
