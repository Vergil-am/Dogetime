package dev.dogetime.ui.watch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import dev.dogetime.core.CapturedStream

/**
 * The player WebView. Off-site navigations are blocked with [allowedHost]. On desktop and
 * Android it also reports HLS playlists (.m3u8) through [onStreamFound].
 */
@Composable
expect fun PlayerWebView(
    state: WebViewState,
    navigator: WebViewNavigator,
    allowedHost: String?,
    onStreamFound: (CapturedStream) -> Unit,
    modifier: Modifier = Modifier,
)
