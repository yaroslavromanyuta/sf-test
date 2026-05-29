package com.example.starkfuturetest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.starkfuturetest.presentation.dashboard.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = StarkColors.StarkRed,
    onPrimary = StarkColors.OnSurfaceDark,
    primaryContainer = StarkColors.StarkRedDark,
    onPrimaryContainer = StarkColors.OnSurfaceDark,
    secondary = StarkColors.OnSurfaceVariantDark,
    onSecondary = StarkColors.BackgroundDark,
    secondaryContainer = StarkColors.SurfaceContainerHighDark,
    onSecondaryContainer = StarkColors.OnSurfaceVariantDark,
    tertiary = StarkColors.SuccessGreen,
    onTertiary = StarkColors.OnSurfaceDark,
    error = StarkColors.ErrorRed,
    onError = StarkColors.OnSurfaceDark,
    background = StarkColors.BackgroundDark,
    onBackground = StarkColors.OnSurfaceDark,
    surface = StarkColors.SurfaceDark,
    onSurface = StarkColors.OnSurfaceDark,
    surfaceVariant = StarkColors.SurfaceContainerDark,
    onSurfaceVariant = StarkColors.OnSurfaceVariantDark,
    outline = StarkColors.OutlineDark,
    outlineVariant = StarkColors.OutlineVariantDark,
)

private val LightColorScheme = lightColorScheme(
    primary = StarkColors.StarkRed,
    onPrimary = StarkColors.OnSurfaceDark,
    primaryContainer = StarkColors.StarkRed,
    onPrimaryContainer = StarkColors.OnSurfaceDark,
    secondary = StarkColors.OnSurfaceVariantLight,
    onSecondary = StarkColors.OnSurfaceDark,
    secondaryContainer = StarkColors.SurfaceContainerHighLight,
    onSecondaryContainer = StarkColors.OnSurfaceLight,
    tertiary = StarkColors.SuccessGreenLight,
    onTertiary = StarkColors.OnSurfaceDark,
    error = StarkColors.ErrorRedLight,
    onError = StarkColors.OnSurfaceDark,
    background = StarkColors.BackgroundLight,
    onBackground = StarkColors.OnSurfaceLight,
    surface = StarkColors.SurfaceLight,
    onSurface = StarkColors.OnSurfaceLight,
    surfaceVariant = StarkColors.SurfaceContainerHighLight,
    onSurfaceVariant = StarkColors.OnSurfaceVariantLight,
    outline = StarkColors.OutlineLight,
    outlineVariant = StarkColors.OutlineVariantLight,
)

@Composable
fun StarkTheme(
    themeMode: ThemeMode = ThemeMode.Dark,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.Dark -> true
        ThemeMode.Light -> false
        ThemeMode.System -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = StarkTypography,
        shapes = StarkShapes,
        content = content,
    )
}