package dev.dogetime.core

import androidx.compose.runtime.Composable

// The system WebView is always available here.
@Composable
actual fun WebEngineGate(content: @Composable () -> Unit) = content()
