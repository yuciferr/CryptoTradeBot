package com.example.cryptotradebot.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPrimary,
    secondary = AccentSecondary,
    tertiary = NeutralColor,
    
    background = BackgroundPrimary,
    surface = SurfacePrimary,
    surfaceVariant = SurfaceSecondary,
    
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    
    error = LossColor,
    errorContainer = LossColor.copy(alpha = 0.1f),
    onError = TextPrimary,
    
    outline = DividerColor,
    outlineVariant = DividerColor.copy(alpha = 0.5f),
    scrim = BackgroundPrimary.copy(alpha = 0.3f)
)

@Composable
fun CryptoTradeBotTheme(
    darkTheme: Boolean = true, // Her zaman dark theme kullanacağız
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}