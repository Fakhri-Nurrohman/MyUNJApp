package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable

@Composable
@UiComposable
expect fun WebViewScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
)
