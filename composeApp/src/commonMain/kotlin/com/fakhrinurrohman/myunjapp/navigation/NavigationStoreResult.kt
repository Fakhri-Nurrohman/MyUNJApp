package com.fakhrinurrohman.myunjapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
class NavigationStoreResult {
    private val results = mutableMapOf<Any, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getResult(key: Any) : T? = results[key] as? T

    fun <T> setResult(key: Any, value: T) {
        results[key] = value
    }

    fun removeResult(key: Any) {
        results.remove(key)
    }

    companion object {
        val Saver = Saver<NavigationStoreResult, Map<Any, Any?>>(
            save = { it.results.toMap() },
            restore = { NavigationStoreResult().apply {
                results.putAll(it)
            } }
        )
    }
}

@Composable
fun rememberNavigationStoreResult() = rememberSaveable(
    saver = NavigationStoreResult.Saver
) {
    NavigationStoreResult()
}