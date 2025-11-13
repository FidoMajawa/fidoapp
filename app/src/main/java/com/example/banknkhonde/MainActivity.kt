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
import com.example.banknkhonde.ui.screens.*
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase before using FirebaseAuth
        FirebaseApp.initializeApp(this)

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

                // ✅ Login Screen
                composable("login") {
                    LoginScreen(navController = navController)
                }

                // ✅ Dashboard
                composable("dashboard") {
                    DashboardScreen(navController = navController)
                }

                // Other Screens
                composable("notifications") {
                    NotificationsScreen(navController = navController)
                }

                composable("addMember") {
                    AddMemberScreen(navController = navController)
                }

                composable("settings") {
                    SettingsScreen(navController = navController)
                }

                composable("contributions") {
                    ContributionScreen(navController = navController)
                }

                composable("members") {
                    MembersScreen(navController = navController)
                }

                composable("loans") {
                    LoansScreen(navController = navController)
                }
                composable("addContribution") {
                    AddContributionScreen(navController)
                }

                composable("reports") {
                    ReportsScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun DashboardScreenUnique(navController: NavHostController) {
    TODO("Not yet implemented")
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
