package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.RevealWrapper
import com.fakhrinurrohman.myunjapp.navigation.NavigationStoreResult
import myunjapp.composeapp.generated.resources.Res
import myunjapp.composeapp.generated.resources.unj_logo_512_px_1
import org.jetbrains.compose.resources.painterResource

@Composable
fun MoreScreen(
    navigationStoreResult: NavigationStoreResult,
    onChangeSettingClick: () -> Unit,
    onNavigateToUrl: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val setting = navigationStoreResult.getResult<String>("Main_Setting")
    val scrollState = rememberScrollState()

    RevealWrapper(isLoading = false) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Branding / Header
            Image(
                painter = painterResource(Res.drawable.unj_logo_512_px_1),
                contentDescription = "Logo App",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "MyUNJ",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Version 1.1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Options List
            Column(modifier = Modifier.fillMaxWidth()) {
                MoreOptionItem(
                    title = "Settings",
                    subtitle = "Current: ${setting ?: "Default"}",
                    icon = Icons.Default.Settings,
                    onClick = onChangeSettingClick
                )
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                MoreOptionItem(
                    title = "About",
                    subtitle = "Learn more about MyUNJ",
                    icon = Icons.Default.Info,
                    onClick = { /* Handle About */ }
                )
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                MoreOptionItem(
                    title = "Help",
                    subtitle = "Guide and documentation",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    onClick = { /* Handle Help */ }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                MoreOptionItem(
                    title = "Contact",
                    subtitle = "Get in touch with us",
                    icon = Icons.Default.Email,
                    onClick = { /* Handle Contact */ }
                )
            }
        }
    }
}

@Composable
fun MoreOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick() },
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
