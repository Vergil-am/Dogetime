package dev.dogetime

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.dogetime.core.DesktopWebEngine
import dev.dogetime.ui.player.LocalWindowState

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(420.dp, 720.dp))
    Window(
        onCloseRequest = {
            DesktopWebEngine.dispose()
            exitApplication()
        },
        title = "DogeTime",
        state = windowState,
    ) {
        CompositionLocalProvider(LocalWindowState provides windowState) {
            App()
        }
    }
}
