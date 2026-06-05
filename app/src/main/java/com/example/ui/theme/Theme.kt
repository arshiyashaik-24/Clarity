package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ClarityPrimaryDark,
    secondary = ClaritySecondaryDark,
    tertiary = ClarityAccentDark,
    background = ClarityBackgroundDark,
    surface = ClaritySurfaceDark,
    onPrimary = ClarityBackgroundDark,
    onSecondary = ClarityBackgroundDark,
    onTertiary = ClarityBackgroundDark,
    onBackground = ClarityTextPrimaryDark,
    onSurface = ClarityTextPrimaryDark,
    surfaceVariant = ClaritySurfaceDark,
    onSurfaceVariant = ClarityTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = ClarityPrimary,
    secondary = ClaritySecondary,
    tertiary = ClarityAccent,
    background = ClarityBackground,
    surface = ClaritySurface,
    onPrimary = ClaritySurface,
    onSecondary = ClaritySurface,
    onTertiary = ClaritySurface,
    onBackground = ClarityTextPrimary,
    onSurface = ClarityTextPrimary,
    surfaceVariant = ClarityBackground,
    onSurfaceVariant = ClarityTextSecondary,
    error = ClarityError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to enforce our polished brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
