package dev.dogetime.ui.watch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import dev.dogetime.core.CapturedStream

// The system WebView plays H.264 streams itself, so nothing needs to be captured here.
@Composable
actual fun PlayerWebView(
    state: WebViewState,
    navigator: WebViewNavigator,
    allowedHost: String?,
    onStreamFound: (CapturedStream) -> Unit,
    modifier: Modifier,
) {
    WebView(state = state, navigator = navigator, modifier = modifier)
}
