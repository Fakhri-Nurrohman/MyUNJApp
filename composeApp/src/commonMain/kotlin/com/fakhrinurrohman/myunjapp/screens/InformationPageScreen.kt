package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.RevealWrapper
import com.fakhrinurrohman.myunjapp.component.SectionHeader
import com.fakhrinurrohman.myunjapp.viewmodels.InformationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationPageScreen(
    viewModel: InformationViewModel,
    onNavigateToUrl: (String, String) -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToCampuses: () -> Unit,
    onNavigateToFaculties: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    RevealWrapper(isLoading = uiState.isLoading) {
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Campus Guide",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "State University of Jakarta",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 2. Portals (Quick Links)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PortalCard("SIAKAD", "https://siakad.unj.ac.id", Icons.Default.School, Modifier.weight(1f), onNavigateToUrl)
                    // Updated SIMTA to http as it often lacks SSL on internal university servers
                    PortalCard("SIMTA", "http://simta.unj.ac.id", Icons.AutoMirrored.Filled.Assignment, Modifier.weight(1f), onNavigateToUrl)
                    PortalCard("E-Lib", "http://lib.unj.ac.id", Icons.AutoMirrored.Filled.LibraryBooks, Modifier.weight(1f), onNavigateToUrl)
                }
            }

            // 3. Main Menus
            item { SectionHeader(title = "General Information", icon = Icons.Default.Info) }
            
            item {
                InfoMenuCard(
                    title = "University News",
                    subtitle = "Latest updates, achievements, and innovations",
                    icon = Icons.Default.Newspaper,
                    onClick = onNavigateToNews
                )
            }

            item {
                InfoMenuCard(
                    title = "Campus Directory",
                    subtitle = "Explore buildings and facilities across campuses",
                    icon = Icons.Default.Map,
                    onClick = onNavigateToCampuses
                )
            }

            item {
                InfoMenuCard(
                    title = "Academic Directory",
                    subtitle = "Faculties, study programs, and lecturers",
                    icon = Icons.Default.AccountBalance,
                    onClick = onNavigateToFaculties
                )
            }

            // 4. Help & FAQ
            item { SectionHeader(title = "Support", icon = Icons.AutoMirrored.Filled.HelpCenter) }
            
            item {
                InfoMenuCard(
                    title = "Help & FAQ",
                    subtitle = "Frequently asked questions about UNJ",
                    icon = Icons.AutoMirrored.Filled.HelpCenter,
                    onClick = { /* Could lead to a dedicated FAQ screen if desired */ }
                )
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun InfoMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        ListItem(
            headlineContent = { Text(title, fontWeight = FontWeight.Bold) },
            supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
            leadingContent = { 
                Icon(
                    imageVector = icon, 
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                ) 
            },
            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun PortalCard(label: String, url: String, icon: ImageVector, modifier: Modifier, onOpenUrl: (String, String) -> Unit) {
    ElevatedCard(
        modifier = modifier,
        onClick = { onOpenUrl(url, label) },
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}
