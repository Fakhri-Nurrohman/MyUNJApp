package com.fakhrinurrohman.myunjapp.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
actual fun MapView(
    latitude: Double,
    longitude: Double,
    title: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    if (latitude == 0.0 && longitude == 0.0) return

    // Since billing is required for the in-app map tiles, 
    // we use a professional placeholder that opens the full Google Maps app on click.
    Surface(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($title)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                
                try {
                    context.startActivity(mapIntent)
                } catch (e: Exception) {
                    // Fallback to any map app if Google Maps is not installed
                    context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                }
            },
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "View on Google Maps",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tap to open navigation and directions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
