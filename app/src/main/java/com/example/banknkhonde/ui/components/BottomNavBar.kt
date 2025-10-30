package com.example.banknkhonde.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavBar(
    navController: NavController,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(containerColor = Color(0xFF002147)) {
        val items = listOf(
            "Dashboard" to Icons.Default.Home,
            "Members" to Icons.Default.Person,
            "Reports" to Icons.Default.Description,
            "Settings" to Icons.Default.Settings
        )

        items.forEach { (label, icon) ->
            NavigationBarItem(
                selected = selectedTab == label,
                onClick = {
                    onTabSelected(label)
                    when (label) {
                        "Dashboard" -> navController.navigate("dashboard")
                        "Members" -> navController.navigate("members")
                        "Reports" -> navController.navigate("reports")
                        "Settings" -> navController.navigate("settings")
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White
                )
            )
        }
    }
}
