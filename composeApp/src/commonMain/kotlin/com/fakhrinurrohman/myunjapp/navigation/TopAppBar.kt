package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    selectedKey: NavKey,
    modifier: Modifier = Modifier,
    titleOverride: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    val currentData = getTopLevelDestinations()[selectedKey]
    val displayTitle = titleOverride ?: currentData?.title ?: "MyUNJ"

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            // Changed icon colors to secondary (yellow) as requested
            navigationIconContentColor = MaterialTheme.colorScheme.secondary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary
        ),
        title = {
            AnimatedContent(
                targetState = displayTitle,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "AppBarTitleTransition"
            ) { title ->
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = navigationIcon,
        actions = actions
    )
}
