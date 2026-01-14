package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import myunjapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

data class BottomNavItem (
    val title: String,
    val icon: ImageVector,
)

@Composable
fun getTopLevelDestinations(): Map<Route, BottomNavItem> {
    return mapOf(
        Route.HomePage to BottomNavItem(
            title = stringResource(Res.string.nav_home),
            icon = Icons.Default.Home
        ),
        Route.TasksPage to BottomNavItem(
            title = stringResource(Res.string.nav_tasks),
            icon = Icons.Default.Checklist
        ),
        Route.SchedulePage to BottomNavItem(
            title = stringResource(Res.string.nav_schedule),
            icon = Icons.Default.CalendarMonth
        ),
        Route.InformationPage to BottomNavItem(
            title = stringResource(Res.string.nav_info),
            icon = Icons.Default.Info
        ),
        Route.MorePage to BottomNavItem(
            title = stringResource(Res.string.nav_settings),
            icon = Icons.Default.Settings
        )
    )
}
