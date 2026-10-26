package dev.dogetime.core

import androidx.compose.runtime.Composable

/** Intercepts the system back gesture or button while [enabled] is true. */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
