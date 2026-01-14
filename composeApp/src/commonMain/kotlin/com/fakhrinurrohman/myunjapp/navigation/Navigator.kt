package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey

class Navigator (val state: NavigationState) {
    fun navigate (route: NavKey) {
        if(route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Backstack for ${state.topLevelRoute} doesn't exist")
        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute){
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun fadeToBlackTransition(
        fadeDuration: Int = 300,
        blackPause: Int = 300
    ): AnimatedContentTransitionScope<NavigationState>.() -> ContentTransform {
        return {
            fadeIn(animationSpec = tween(durationMillis = fadeDuration, delayMillis = blackPause)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = fadeDuration))
        }
    }
}
