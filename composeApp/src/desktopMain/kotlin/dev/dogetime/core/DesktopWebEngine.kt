package dev.dogetime.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

sealed interface WebEngineState {
    data object NotStarted : WebEngineState
    data object Preparing : WebEngineState
    data class Downloading(val percent: Float) : WebEngineState
    data object Ready : WebEngineState
    data object RestartRequired : WebEngineState
    data class Failed(val message: String) : WebEngineState
}

/**
 * The Chromium engine (KCEF) behind the desktop WebView. It is started the first time a
 * WebView is shown. The first start downloads the Chromium bundle into ~/.dogetime;
 * later starts reuse it.
 */
object DesktopWebEngine {
    var state: WebEngineState by mutableStateOf(WebEngineState.NotStarted)
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val baseDir = File(System.getProperty("user.home"), ".dogetime")

    fun start() {
        if (state != WebEngineState.NotStarted) return
        state = WebEngineState.Preparing
        scope.launch {
            KCEF.init(
                builder = {
                    installDir(File(baseDir, "kcef-bundle"))
                    progress {
                        onDownloading { percent -> state = WebEngineState.Downloading(percent.coerceIn(0f, 100f)) }
                        onInitialized { state = WebEngineState.Ready }
                    }
                    settings {
                        cachePath = File(baseDir, "kcef-cache").absolutePath
                    }
                },
                onError = { error ->
                    state = WebEngineState.Failed(error?.message ?: "Couldn't start the web player")
                },
                onRestartRequired = { state = WebEngineState.RestartRequired },
            )
        }
    }

    /** Call when the app exits so Chromium's helper processes shut down. */
    fun dispose() {
        if (state != WebEngineState.NotStarted) KCEF.disposeBlocking()
    }
}
