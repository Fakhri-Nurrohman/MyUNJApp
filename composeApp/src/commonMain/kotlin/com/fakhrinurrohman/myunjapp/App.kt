package com.fakhrinurrohman.myunjapp

import androidx.compose.runtime.Composable
import com.fakhrinurrohman.myunjapp.navigation.NavigationRoot
import com.fakhrinurrohman.myunjapp.ui.theme.MyUNJTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MyUNJTheme {
        NavigationRoot()
    }
}
