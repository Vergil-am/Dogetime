package dev.dogetime

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Catppuccin Mocha — https://catppuccin.com/palette
object Mocha {
    val Crust = Color(0xFF11111B)
    val Mantle = Color(0xFF181825)
    val Base = Color(0xFF1E1E2E)
    val Surface0 = Color(0xFF313244)
    val Surface1 = Color(0xFF45475A)
    val Surface2 = Color(0xFF585B70)
    val Overlay0 = Color(0xFF6C7086)
    val Subtext0 = Color(0xFFA6ADC8)
    val Text = Color(0xFFCDD6F4)
    val Lavender = Color(0xFFB4BEFE)
    val Blue = Color(0xFF89B4FA)
    val Mauve = Color(0xFFCBA6F7)
    val Pink = Color(0xFFF5C2E7)
    val Red = Color(0xFFF38BA8)
    val Peach = Color(0xFFFAB387)
    val Green = Color(0xFFA6E3A1)
}

private val MochaColors = darkColorScheme(
    primary = Mocha.Mauve,
    onPrimary = Mocha.Crust,
    primaryContainer = Mocha.Surface1,
    onPrimaryContainer = Mocha.Mauve,
    secondary = Mocha.Blue,
    onSecondary = Mocha.Crust,
    secondaryContainer = Mocha.Surface0,
    onSecondaryContainer = Mocha.Blue,
    tertiary = Mocha.Peach,
    onTertiary = Mocha.Crust,
    tertiaryContainer = Mocha.Surface0,
    onTertiaryContainer = Mocha.Peach,
    background = Mocha.Base,
    onBackground = Mocha.Text,
    surface = Mocha.Base,
    onSurface = Mocha.Text,
    surfaceVariant = Mocha.Surface0,
    onSurfaceVariant = Mocha.Subtext0,
    surfaceContainerLowest = Mocha.Crust,
    surfaceContainerLow = Mocha.Mantle,
    surfaceContainer = Mocha.Mantle,
    surfaceContainerHigh = Mocha.Surface0,
    surfaceContainerHighest = Mocha.Surface1,
    inverseSurface = Mocha.Text,
    inverseOnSurface = Mocha.Base,
    inversePrimary = Mocha.Lavender,
    outline = Mocha.Overlay0,
    outlineVariant = Mocha.Surface2,
    error = Mocha.Red,
    onError = Mocha.Crust,
    errorContainer = Mocha.Surface0,
    onErrorContainer = Mocha.Red,
    scrim = Mocha.Crust,
)

@Composable
fun DogeTimeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MochaColors,
        content = content,
    )
}
