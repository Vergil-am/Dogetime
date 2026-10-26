package dev.dogetime.core

import androidx.compose.runtime.Composable

/**
 * Shows [content] (a WebView) once the platform's web engine is ready.
 * Android and iOS use the system engine; desktop may need to download Chromium first.
 */
@Composable
expect fun WebEngineGate(content: @Composable () -> Unit)
