package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AegisDarkColorScheme = darkColorScheme(
    primary = AegisCyanGlow,
    onPrimary = AegisVoidBlack,
    primaryContainer = AegisDarkSurface,
    onPrimaryContainer = AegisCyanGlow,
    secondary = AegisPlasmaGold,
    onSecondary = AegisVoidBlack,
    secondaryContainer = AegisSurfaceElevated,
    onSecondaryContainer = AegisPlasmaGold,
    tertiary = AegisEmerald,
    onTertiary = AegisVoidBlack,
    background = AegisVoidBlack,
    onBackground = AegisTextPrimary,
    surface = AegisDarkSurface,
    onSurface = AegisTextPrimary,
    surfaceVariant = AegisSurfaceElevated,
    onSurfaceVariant = AegisTextMuted,
    outline = AegisBorderGlow,
    error = AegisCrimson,
    onError = AegisTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep consistent cybernetic HUD theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AegisDarkColorScheme,
        typography = Typography,
        content = content
    )
}
