package com.trabajo.minitienda.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores CLARO (tu esquema original)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryGreenHover,
    background = BackgroundStart,
    surface = CardBackground,
    error = ErrorColor,
    onPrimary = CardBackground,
    onSecondary = PrimaryText,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    onError = CardBackground
)

// Esquema de colores OSCURO (nuevo)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF66BB6A),           // Verde más claro para oscuro
    secondary = Color(0xFF81C784),         // Verde hover para oscuro
    background = Color(0xFF121212),        // Fondo negro
    surface = Color(0xFF1E1E1E),          // Cards oscuros
    error = Color(0xFFEF5350),            // Error rojo claro
    onPrimary = Color(0xFF1A1A1A),        // Texto en primary
    onSecondary = Color(0xFFE0E0E0),      // Texto en secondary
    onBackground = Color(0xFFE0E0E0),     // Texto principal claro
    onSurface = Color(0xFFE0E0E0),        // Texto en surface
    onError = Color.White                  // Texto en error
)

@Composable
fun MiniTiendaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Selecciona el esquema según el tema
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            // Cambia los iconos de la barra según el tema
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}