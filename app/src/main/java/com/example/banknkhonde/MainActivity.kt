package com.example.banknkhonde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.banknkhonde.ui.screens.*
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
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
            NavHost(navController = navController, startDestination = "splash") {

                // ✅ Splash Screen
                composable("splash") {
                    SplashScreen(navController = navController)
                }

                // ✅ Login Screen
                composable("login") {
                    LoginScreen(navController = navController)
                }

                // ✅ Dashboard
                composable("dashboard") {
                    DashboardScreen(navController = navController)
                }

                // ✅ Attendance Screen (shows club members + penalties)
                composable("attendance") {
                    AttendanceScreen(navController = navController)
                }

                // Optional: Separate Notifications Screen if needed in future
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
                    AddContributionScreen(navController = navController)
                }

                composable("reports") {
                    ReportsScreen(navController = navController)
                }

                composable("sendNotification") {
                    SendNotificationScreen(navController = navController)
                }
            }
        }
    }
}
