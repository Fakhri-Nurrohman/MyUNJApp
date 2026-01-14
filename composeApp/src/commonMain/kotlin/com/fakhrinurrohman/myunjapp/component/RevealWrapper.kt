package com.fakhrinurrohman.myunjapp.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

/**
 * A wrapper that provides a smooth white-out reveal animation.
 * Centralizes the loading and transition logic for main dashboard screens.
 */
@Composable
fun RevealWrapper(
    isLoading: Boolean,
    minDelayMillis: Long = 300,
    revealDurationMillis: Int = 500,
    content: @Composable () -> Unit
) {
    var isRevealed by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(minDelayMillis)
            isRevealed = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        AnimatedVisibility(
            visible = !isRevealed,
            exit = fadeOut(tween(durationMillis = revealDurationMillis))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }
    }
}
