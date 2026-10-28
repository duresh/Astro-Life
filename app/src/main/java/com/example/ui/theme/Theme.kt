package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CosmicDarkPrimary,
    onPrimary = CosmicDarkOnPrimary,
    primaryContainer = CosmicDarkPrimaryContainer,
    onPrimaryContainer = CosmicDarkOnPrimaryContainer,
    secondary = CosmicDarkSecondary,
    onSecondary = CosmicDarkOnSecondary,
    secondaryContainer = CosmicDarkSecondaryContainer,
    onSecondaryContainer = Color(0xFFD7E2FF),
    tertiary = CosmicDarkTertiary,
    onTertiary = CosmicDarkOnTertiary,
    background = CosmicDarkBackground,
    onBackground = Color(0xFFE3E8F4),
    surface = CosmicDarkSurface,
    onSurface = Color(0xFFE3E8F4),
    surfaceVariant = CosmicDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4CCE0),
    outline = CosmicDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = CosmicLightPrimary,
    onPrimary = CosmicLightOnPrimary,
    primaryContainer = CosmicLightPrimaryContainer,
    onPrimaryContainer = CosmicLightOnPrimaryContainer,
    secondary = CosmicLightSecondary,
    onSecondary = CosmicLightOnSecondary,
    secondaryContainer = CosmicLightSecondaryContainer,
    onSecondaryContainer = Color(0xFF001A41),
    tertiary = CosmicLightTertiary,
    onTertiary = CosmicLightOnTertiary,
    background = CosmicLightBackground,
    onBackground = Color(0xFF1B1C1E),
    surface = CosmicLightSurface,
    onSurface = Color(0xFF1B1C1E),
    surfaceVariant = CosmicLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4C4639),
    outline = CosmicLightOutline
)

@Composable
fun AstroLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
