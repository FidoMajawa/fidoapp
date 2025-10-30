package com.example.banknkhonde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.banknkhonde.ui.screens.*  // Import all your screens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = "login") {
                // Login Screen
                composable("login") {
                    LoginScreen(navController = navController)
                }

                // Dashboard
                composable("dashboard") {
                    DashboardScreenUnique(navController = navController)
                }

                // Other screens
                composable("notifications") {
                    NotificationsScreen(navController = navController)
                }
                composable("settings") {
                    SettingsScreen(navController = navController)
                }
                composable("members") {
                    MembersScreen(navController = navController)
                }
                composable("loans") {
                    LoansScreen(navController = navController)
                }
                composable("reports") {
                    ReportsScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(navController: NavHostController) {
    TODO("Not yet implemented")
}

@Composable
fun SimpleScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, style = MaterialTheme.typography.titleLarge)
    }
}
