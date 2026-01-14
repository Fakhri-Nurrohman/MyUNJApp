package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhrinurrohman.myunjapp.component.MapView
import com.fakhrinurrohman.myunjapp.component.RevealWrapper

@Composable
fun DetailInformationScreen(
    subtitle: String,
    description: String? = null,
    extraInfo: Map<String, String> = emptyMap(),
    listItems: List<String> = emptyList(),
    onItemClick: ((Int) -> Unit)? = null,
    filterCategories: List<String>? = null,
    selectedCategory: String? = null,
    onCategorySelected: ((String) -> Unit)? = null,
    coordinates: Pair<Double, Double>? = null,
    locationTitle: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    RevealWrapper(isLoading = false) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            item {
                if (filterCategories != null && selectedCategory != null && onCategorySelected != null) {
                    Box(modifier = Modifier.padding(bottom = 24.dp)) {
                        OutlinedCard(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Kategori: $selectedCategory",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            filterCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        onCategorySelected(category)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
            if (coordinates != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(bottom = 8.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        MapView(
                            latitude = coordinates.first,
                            longitude = coordinates.second,
                            title = locationTitle ?: "Location",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Debug info to verify coordinates are coming through
                    Text(
                        text = "Lat: ${coordinates.first}, Lng: ${coordinates.second}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }

            // Description
            if (!description.isNullOrBlank()) {
                item {
                    Text(
                        text = description, 
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }

            // List Section (Drill Down)
            if (listItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Explore Details",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(listItems.size) { index ->
                    ListItem(
                        headlineContent = { Text(listItems[index]) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier = Modifier.clickable { onItemClick?.invoke(index) }
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }

            // Key-Value Info Section
            if (extraInfo.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            extraInfo.forEach { (key, value) ->
                                DetailItem(label = key, value = value)
                                if (key != extraInfo.keys.last()) {
                                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
