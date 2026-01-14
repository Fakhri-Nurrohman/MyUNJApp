package com.fakhrinurrohman.myunjapp.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A platform-agnostic Map component.
 * Android: Shows a real Google Map.
 * Desktop/iOS: Shows a placeholder (for now).
 */
@Composable
expect fun MapView(
    latitude: Double,
    longitude: Double,
    title: String,
    modifier: Modifier = Modifier
)
