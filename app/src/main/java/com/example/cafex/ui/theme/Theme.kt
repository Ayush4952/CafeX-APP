package com.example.cafex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Espresso,
    onPrimary = Cream,
    primaryContainer = Latte,
    onPrimaryContainer = Espresso,
    secondary = Coffee,
    onSecondary = Cream,
    secondaryContainer = ColorTokens.WarmSand,
    onSecondaryContainer = Espresso,
    tertiary = Sage,
    background = Cream,
    onBackground = Espresso,
    surface = Cream,
    onSurface = Espresso,
    surfaceVariant = ColorTokens.PaleLatte,
    onSurfaceVariant = Coffee,
)

private val DarkColors = darkColorScheme(
    primary = Latte,
    onPrimary = Espresso,
    primaryContainer = Coffee,
    onPrimaryContainer = Cream,
    secondary = Caramel,
    onSecondary = Espresso,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = Cream,
    tertiary = ColorTokens.SoftSage,
    background = DarkSurface,
    onBackground = Cream,
    surface = DarkSurface,
    onSurface = Cream,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Latte,
)

private object ColorTokens {
    val WarmSand = androidx.compose.ui.graphics.Color(0xFFF3DDC3)
    val PaleLatte = androidx.compose.ui.graphics.Color(0xFFF7EBDD)
    val SoftSage = androidx.compose.ui.graphics.Color(0xFFB8C8B2)
}

@Composable
fun CafeXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = CafeXTypography,
        content = content,
    )
}
