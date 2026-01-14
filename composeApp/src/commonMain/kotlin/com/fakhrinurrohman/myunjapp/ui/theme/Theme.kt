package com.fakhrinurrohman.myunjapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(

    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = LightGreen.copy(alpha = 0.2f),
    onPrimaryContainer = DarkGreen,
    
    secondary = BrightYellow,
    onSecondary = Color.Black,
    secondaryContainer = SoftYellow.copy(alpha = 0.3f),
    onSecondaryContainer = DarkYellow,
    
    background = BackgroundWhite,
    surface = SurfaceWhite,
    error = ErrorRed

)

private val DarkColorScheme = darkColorScheme(
    primary = LightGreen,
    onPrimary = DarkGreen,
    primaryContainer = ForestGreen,
    onPrimaryContainer = Color.White,
    
    secondary = SoftYellow,
    onSecondary = Color.Black,
    secondaryContainer = DarkYellow,
    onSecondaryContainer = Color.White
)

@Composable
fun MyUNJTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
