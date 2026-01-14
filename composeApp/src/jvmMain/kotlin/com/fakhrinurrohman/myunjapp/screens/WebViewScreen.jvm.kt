package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI

@Composable
@UiComposable
actual fun WebViewScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("WebView is not supported on Desktop yet.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI(url))
                }
            }) {
                Icon(Icons.Default.OpenInNew, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open in Browser")
            }
        }
    }
}
