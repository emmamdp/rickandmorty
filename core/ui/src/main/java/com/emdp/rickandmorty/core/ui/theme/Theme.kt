package com.emdp.rickandmorty.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    primary = PortalGreen,
    onPrimary = Neutral0,
    primaryContainer = Color(0xFFDAF2B4),
    onPrimaryContainer = Neutral10,

    secondary = RickCyan,
    onSecondary = Neutral0,
    secondaryContainer = Color(0xFFB9EAF2),
    onSecondaryContainer = Neutral10,

    tertiary = MortyYellow,
    onTertiary = Neutral0,
    tertiaryContainer = Color(0xFFFFF3B5),
    onTertiaryContainer = Neutral10,

    error = ErrorRed,
    onError = Neutral0,
    errorContainer = Color(0xFFF9C9C7),
    onErrorContainer = Neutral10,

    background = Neutral98,
    onBackground = Neutral10,
    surface = Neutral100,
    onSurface = Neutral10,

    surfaceVariant = Color(0xFFE6E6E6),
    onSurfaceVariant = Neutral10,

    outline = Neutral30,
    outlineVariant = Color(0xFFCCCCCC),

    scrim = Color(0x66000000),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = PortalGreen,
    onPrimary = Neutral0,
    primaryContainer = Color(0xFF325319),
    onPrimaryContainer = Neutral100,

    secondary = RickCyan,
    onSecondary = Neutral0,
    secondaryContainer = Color(0xFF004B55),
    onSecondaryContainer = Neutral100,

    tertiary = MortyYellow,
    onTertiary = Neutral0,
    tertiaryContainer = Color(0xFF5A511B),
    onTertiaryContainer = Neutral100,

    error = ErrorRed,
    onError = Neutral0,
    errorContainer = Color(0xFF5F1B18),
    onErrorContainer = Neutral100,

    background = Neutral10,
    onBackground = Neutral98,
    surface = Neutral10,
    onSurface = Neutral98,

    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Neutral98,

    outline = Neutral30,
    outlineVariant = Color(0xFF3A3A3A),

    scrim = Color(0x99000000),
)

@Composable
fun RickAndMortyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = RickAndMortyTypography,
        shapes = Shapes(),
        content = content
    )
}