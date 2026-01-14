package com.fakhrinurrohman.myunjapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fakhrinurrohman.myunjapp.component.RevealWrapper
import com.fakhrinurrohman.myunjapp.navigation.NavigationStoreResult

@Composable
fun SettingScreenChange(
    navigationStoreResult: NavigationStoreResult,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember {
        mutableStateOf("")
    }

    RevealWrapper(isLoading = false) {
        Column (
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Button(
                onClick = {
                    navigationStoreResult.setResult("Main_Setting", text)
                    onSave()
                },
            ) {
                Text("Save")
            }
        }
    }
}
