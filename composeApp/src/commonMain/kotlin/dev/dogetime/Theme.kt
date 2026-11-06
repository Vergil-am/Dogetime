package dev.dogetime

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

enum class AppTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val previewColors: List<Color>,
) {
    Midnight(
        id = "midnight",
        displayName = "Chofo Midnight",
        description = "Deep night black with glowing periwinkle accents",
        previewColors = listOf(
            Color(0xFF000807),
            Color(0xFFFBF9FF),
            Color(0xFF9395D3),
            Color(0xFFB3B7EE),
            Color(0xFFA2A3BB),
        ),
    ),
    Mocha(
        id = "mocha",
        displayName = "Catppuccin Mocha",
        description = "Warm dark slate with soft lavender and pastel hues",
        previewColors = listOf(
            Color(0xFF1E1E2E),
            Color(0xFF89B4FA),
            Color(0xFFCBA6F7),
            Color(0xFFA6ADC8),
            Color(0xFFCDD6F4),
        ),
    ),
}

data class AppPalette(
    val crust: Color,
    val mantle: Color,
    val base: Color,
    val surface0: Color,
    val surface1: Color,
    val surface2: Color,
    val overlay0: Color,
    val subtext0: Color,
    val text: Color,
    val lavender: Color,
    val blue: Color,
    val mauve: Color,
    val pink: Color,
    val red: Color,
    val peach: Color,
    val green: Color,
    val colorScheme: ColorScheme,
)

val MidnightPalette = run {
    val base = Color(0xFF000807)      // Main background from Coolors
    val crust = Color(0xFF000504)     // Deepest black depth
    val mantle = Color(0xFF040D0C)    // Slightly elevated dark tone
    val surface0 = Color(0xFF0A1413)  // Card / surface base
    val surface1 = Color(0xFF121E1C)  // Elevated surface
    val surface2 = Color(0xFF1C2B29)  // Borders & subtle dividers
    val overlay0 = Color(0xFF424558)  // Outline / separators
    val subtext0 = Color(0xFFA2A3BB)  // Cool slate secondary text (#A2A3BB from Coolors)
    val text = Color(0xFFFBF9FF)      // Ghost white primary text (#FBF9FF from Coolors)
    val mauve = Color(0xFFB3B7EE)     // Bright periwinkle primary accent (#B3B7EE from Coolors)
    val blue = Color(0xFF9395D3)      // Slate blue secondary accent (#9395D3 from Coolors)
    val lavender = Color(0xFFB3B7EE)
    val pink = Color(0xFFC7B8EA)
    val red = Color(0xFFF38BA8)
    val peach = Color(0xFFFAB387)
    val green = Color(0xFFA6E3A1)

    AppPalette(
        crust = crust,
        mantle = mantle,
        base = base,
        surface0 = surface0,
        surface1 = surface1,
        surface2 = surface2,
        overlay0 = overlay0,
        subtext0 = subtext0,
        text = text,
        lavender = lavender,
        blue = blue,
        mauve = mauve,
        pink = pink,
        red = red,
        peach = peach,
        green = green,
        colorScheme = darkColorScheme(
            primary = text,
            onPrimary = base,
            primaryContainer = surface1,
            onPrimaryContainer = text,
            secondary = blue,
            onSecondary = base,
            secondaryContainer = surface0,
            onSecondaryContainer = blue,
            tertiary = subtext0,
            onTertiary = base,
            tertiaryContainer = surface0,
            onTertiaryContainer = mauve,
            background = base,
            onBackground = text,
            surface = base,
            onSurface = text,
            surfaceVariant = surface0,
            onSurfaceVariant = subtext0,
            surfaceContainerLowest = crust,
            surfaceContainerLow = mantle,
            surfaceContainer = mantle,
            surfaceContainerHigh = surface0,
            surfaceContainerHighest = surface1,
            inverseSurface = text,
            inverseOnSurface = base,
            inversePrimary = lavender,
            outline = overlay0,
            outlineVariant = surface2,
            error = red,
            onError = base,
            errorContainer = surface0,
            onErrorContainer = red,
            scrim = crust,
        ),
    )
}

val MochaPalette = run {
    val crust = Color(0xFF11111B)
    val mantle = Color(0xFF181825)
    val base = Color(0xFF1E1E2E)
    val surface0 = Color(0xFF313244)
    val surface1 = Color(0xFF45475A)
    val surface2 = Color(0xFF585B70)
    val overlay0 = Color(0xFF6C7086)
    val subtext0 = Color(0xFFA6ADC8)
    val text = Color(0xFFCDD6F4)
    val lavender = Color(0xFFB4BEFE)
    val blue = Color(0xFF89B4FA)
    val mauve = Color(0xFFCBA6F7)
    val pink = Color(0xFFF5C2E7)
    val red = Color(0xFFF38BA8)
    val peach = Color(0xFFFAB387)
    val green = Color(0xFFA6E3A1)

    AppPalette(
        crust = crust,
        mantle = mantle,
        base = base,
        surface0 = surface0,
        surface1 = surface1,
        surface2 = surface2,
        overlay0 = overlay0,
        subtext0 = subtext0,
        text = text,
        lavender = lavender,
        blue = blue,
        mauve = mauve,
        pink = pink,
        red = red,
        peach = peach,
        green = green,
        colorScheme = darkColorScheme(
            primary = mauve,
            onPrimary = crust,
            primaryContainer = surface1,
            onPrimaryContainer = mauve,
            secondary = blue,
            onSecondary = crust,
            secondaryContainer = surface0,
            onSecondaryContainer = blue,
            tertiary = peach,
            onTertiary = crust,
            tertiaryContainer = surface0,
            onTertiaryContainer = peach,
            background = base,
            onBackground = text,
            surface = base,
            onSurface = text,
            surfaceVariant = surface0,
            onSurfaceVariant = subtext0,
            surfaceContainerLowest = crust,
            surfaceContainerLow = mantle,
            surfaceContainer = mantle,
            surfaceContainerHigh = surface0,
            surfaceContainerHighest = surface1,
            inverseSurface = text,
            inverseOnSurface = base,
            inversePrimary = lavender,
            outline = overlay0,
            outlineVariant = surface2,
            error = red,
            onError = crust,
            errorContainer = surface0,
            onErrorContainer = red,
            scrim = crust,
        ),
    )
}

val LocalAppPalette = compositionLocalOf { MidnightPalette }
val LocalActiveTheme = compositionLocalOf { AppTheme.Midnight }
val LocalThemeSwitcher = compositionLocalOf<(AppTheme) -> Unit> { {} }

object Mocha {
    val Crust: Color @Composable get() = LocalAppPalette.current.crust
    val Mantle: Color @Composable get() = LocalAppPalette.current.mantle
    val Base: Color @Composable get() = LocalAppPalette.current.base
    val Surface0: Color @Composable get() = LocalAppPalette.current.surface0
    val Surface1: Color @Composable get() = LocalAppPalette.current.surface1
    val Surface2: Color @Composable get() = LocalAppPalette.current.surface2
    val Overlay0: Color @Composable get() = LocalAppPalette.current.overlay0
    val Subtext0: Color @Composable get() = LocalAppPalette.current.subtext0
    val Text: Color @Composable get() = LocalAppPalette.current.text
    val Lavender: Color @Composable get() = LocalAppPalette.current.lavender
    val Blue: Color @Composable get() = LocalAppPalette.current.blue
    val Mauve: Color @Composable get() = LocalAppPalette.current.mauve
    val Pink: Color @Composable get() = LocalAppPalette.current.pink
    val Red: Color @Composable get() = LocalAppPalette.current.red
    val Peach: Color @Composable get() = LocalAppPalette.current.peach
    val Green: Color @Composable get() = LocalAppPalette.current.green
}

@Composable
fun ChofoTheme(
    theme: AppTheme = AppTheme.Midnight,
    onThemeChange: (AppTheme) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val palette = when (theme) {
        AppTheme.Midnight -> MidnightPalette
        AppTheme.Mocha -> MochaPalette
    }
    CompositionLocalProvider(
        LocalAppPalette provides palette,
        LocalActiveTheme provides theme,
        LocalThemeSwitcher provides onThemeChange,
    ) {
        MaterialTheme(
            colorScheme = palette.colorScheme,
            content = content,
        )
    }
}

@Composable
fun DogeTimeTheme(content: @Composable () -> Unit) = ChofoTheme(content = content)

@Composable
fun ThemeSelectionDialog(
    currentTheme: AppTheme,
    onSelect: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            modifier = Modifier
                .widthIn(min = 320.dp, max = 400.dp)
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Choose Theme",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppTheme.entries.forEach { theme ->
                        val isSelected = theme == currentTheme
                        val borderColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        )
                        val bgColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(bgColor)
                                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelect(theme)
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = theme.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = theme.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    theme.previewColors.forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                                        )
                                    }
                                }
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelect(theme) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
