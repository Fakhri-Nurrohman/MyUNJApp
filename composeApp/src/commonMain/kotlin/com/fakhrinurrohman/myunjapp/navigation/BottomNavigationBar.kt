package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey

@Composable
fun BottomNavigationBar(
    selectedKey: NavKey,
    onSelectedKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val topLevelDestinations = getTopLevelDestinations()
    NavigationBar(
        modifier = modifier,
        // Set the bar background to Forest Green
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        topLevelDestinations.forEach { (topLevelDestination, data) ->
            NavigationBarItem(
                selected = topLevelDestination == selectedKey,
                onClick = {
                    onSelectedKey(topLevelDestination)
                },
                icon = {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = data.title
                    )
                },
                label = {
                    Text(data.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    // When selected: Green icon on a Yellow "pill" indicator
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.secondary,
                    
                    // When unselected: White-ish colors on the Green bar
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            )
        }
    }
}
