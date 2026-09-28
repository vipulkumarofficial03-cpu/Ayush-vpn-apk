package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AyushVpnColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = EmeraldDeepDark,
    primaryContainer = EmeraldSurfaceVariant,
    onPrimaryContainer = NeonEmeraldLight,
    secondary = CyberCyan,
    onSecondary = EmeraldDeepDark,
    secondaryContainer = EmeraldSurface,
    onSecondaryContainer = EmeraldMint,
    tertiary = EmeraldBright,
    onTertiary = EmeraldDeepDark,
    background = EmeraldDark,
    onBackground = TextPrimary,
    surface = EmeraldSurface,
    onSurface = TextPrimary,
    surfaceVariant = EmeraldSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = EmeraldOutline,
    error = CyberRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep consistent sleek green theme as requested
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AyushVpnColorScheme,
        typography = Typography,
        content = content
    )
}
