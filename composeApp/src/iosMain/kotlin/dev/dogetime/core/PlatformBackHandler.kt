package dev.dogetime.core

import androidx.compose.runtime.Composable

// iOS uses the in-app back button only; there is no hardware back action.
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
