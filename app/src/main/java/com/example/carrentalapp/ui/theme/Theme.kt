package com.example.carrentalapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary            = Blue40,
    onPrimary          = Grey99,
    primaryContainer   = Blue90,
    onPrimaryContainer = Blue10,
    secondary          = Orange40,
    onSecondary        = Grey99,
    secondaryContainer = Orange90,
    onSecondaryContainer = Orange40,
    background          = NeutralSurface,
    onBackground        = Grey10,
    surface             = NeutralSurface,
    onSurface           = Grey10,
    surfaceVariant      = NeutralSurfaceVariant,
    onSurfaceVariant    = NeutralOnSurfaceVariant,
    surfaceTint         = NeutralContainerHighest,
    surfaceContainerLowest  = NeutralContainerLowest,
    surfaceContainerLow     = NeutralContainerLow,
    surfaceContainer        = NeutralContainer,
    surfaceContainerHigh    = NeutralContainerHigh,
    surfaceContainerHighest = NeutralContainerHighest,
    outline             = NeutralOutline,
    outlineVariant      = NeutralOutlineVariant,
    error               = Red40,
    onError             = Grey99,
)

private val DarkColorScheme = darkColorScheme(
    primary            = Blue80,
    onPrimary          = Blue10,
    primaryContainer   = Blue40,
    secondary          = Orange80,
    onSecondary        = Grey10,
    secondaryContainer = Orange40,
    background          = DarkNeutralSurface,
    onBackground        = Grey90,
    surface             = DarkNeutralSurface,
    onSurface           = Grey90,
    surfaceVariant      = DarkNeutralSurfaceVariant,
    onSurfaceVariant    = DarkNeutralOnSurfaceVariant,
    surfaceTint         = DarkNeutralContainerHighest,
    surfaceContainerLowest  = DarkNeutralContainerLowest,
    surfaceContainerLow     = DarkNeutralContainerLow,
    surfaceContainer        = DarkNeutralContainer,
    surfaceContainerHigh    = DarkNeutralContainerHigh,
    surfaceContainerHighest = DarkNeutralContainerHighest,
    error               = Red80,
)

@Composable
fun CarRentalAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
