package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TechCyanLight,
    onPrimary = Color(0xFF003554),
    primaryContainer = TechCyanDark,
    onPrimaryContainer = Color(0xFFCBE6FF),
    secondary = TechIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3730A3),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = TechEmerald,
    onTertiary = Color.White,
    background = TechNavyBg,
    onBackground = Color(0xFFE2E8F0),
    surface = TechNavySurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = TechNavyCard,
    onSurfaceVariant = Color(0xFF94A3B8),
    error = TechRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TechCyanPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF001E30),
    secondary = TechIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEF2FF),
    onSecondaryContainer = Color(0xFF1E1B4B),
    tertiary = TechEmerald,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    error = TechRose,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
