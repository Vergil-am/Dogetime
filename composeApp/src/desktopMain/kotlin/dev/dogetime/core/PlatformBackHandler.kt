package dev.dogetime.core

import androidx.compose.runtime.Composable

// Desktop windows have no system back action, so the in-app back button is the only way back.
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
