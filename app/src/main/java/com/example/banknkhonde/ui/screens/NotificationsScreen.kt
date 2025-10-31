package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsScreen() {
    Scaffold { innerPadding ->
        Text(
            text = "Notifications Screen",
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
    }
}
