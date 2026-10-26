package dev.dogetime.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
actual fun WebEngineGate(content: @Composable () -> Unit) {
    LaunchedEffect(Unit) {
        DesktopWebEngine.start()
    }

    val state = DesktopWebEngine.state
    if (state == WebEngineState.Ready) {
        content()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (state) {
                WebEngineState.NotStarted, WebEngineState.Preparing -> {
                    CircularProgressIndicator()
                    Text("Starting the web player…")
                }
                is WebEngineState.Downloading -> {
                    LinearProgressIndicator(
                        progress = { state.percent / 100f },
                        modifier = Modifier.width(240.dp),
                    )
                    Text("Downloading the web player… ${state.percent.roundToInt()}%")
                    Text(
                        text = "This only happens the first time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                WebEngineState.RestartRequired -> Text(
                    text = "Restart DogeTime to finish installing the web player.",
                    textAlign = TextAlign.Center,
                )
                is WebEngineState.Failed -> Text(
                    text = state.message,
                    textAlign = TextAlign.Center,
                )
                WebEngineState.Ready -> Unit
            }
        }
    }
}
