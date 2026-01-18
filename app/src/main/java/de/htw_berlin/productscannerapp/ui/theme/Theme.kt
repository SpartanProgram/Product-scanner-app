package de.htw_berlin.productscannerapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE4E2FF),
    onPrimaryContainer = Color(0xFF10103A),

    secondary = BrandSecondary,
    onSecondary = Color(0xFF062012),
    secondaryContainer = Color(0xFFD8F7E1),
    onSecondaryContainer = Color(0xFF062012),

    tertiary = BrandTertiary,
    onTertiary = Color(0xFF2A1200),
    tertiaryContainer = Color(0xFFFFE1D3),
    onTertiaryContainer = Color(0xFF2A1200),

    background = LightBackground,
    onBackground = Color(0xFF0E111B),

    surface = LightSurface,
    onSurface = Color(0xFF0E111B),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF3B4358),

    outline = Color(0xFFB8C1DD),
    scrim = Color(0xFF000000),
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color(0xFF0B0B22),
    primaryContainer = Color(0xFF23227A),
    onPrimaryContainer = Color(0xFFE8E6FF),

    secondary = BrandSecondary,
    onSecondary = Color(0xFF062012),
    secondaryContainer = Color(0xFF0F3B22),
    onSecondaryContainer = Color(0xFFBFF1CF),

    tertiary = BrandTertiary,
    onTertiary = Color(0xFF2A1200),
    tertiaryContainer = Color(0xFF5A2500),
    onTertiaryContainer = Color(0xFFFFE1D3),

    background = DarkBackground,
    onBackground = Color(0xFFEAF0FF),

    surface = DarkSurface,
    onSurface = Color(0xFFEAF0FF),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB7C0DD),

    outline = Color(0xFF3A4268),
    scrim = Color(0xFF000000),
)

@Composable
fun ProductScannerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
