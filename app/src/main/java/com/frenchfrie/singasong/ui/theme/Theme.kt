package com.frenchfrie.singasong.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Provisional palette; SAS-015 owns the reviewed visual identity.
private val LightColors = lightColorScheme(
    primary = Color(0xFF006A68),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9CF1EC),
    onPrimaryContainer = Color(0xFF00201F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF80D5D0),
    onPrimary = Color(0xFF003735),
    primaryContainer = Color(0xFF00504D),
    onPrimaryContainer = Color(0xFF9CF1EC),
)

@Composable
fun SingASongTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
