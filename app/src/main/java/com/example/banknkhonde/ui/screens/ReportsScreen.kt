package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Report(val title: String, val amount: Int, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Reports") }

    val reports = listOf(
        Report("Monthly Contributions", 500000, "Summary of member deposits for this month."),
        Report("Loan Status", 1200000, "Total outstanding and paid loans."),
        Report("Interest Reports", 75000, "Accumulated interest for savings and loans."),
        Report("Yearly Summary", 6500000, "Complete yearly report for the club.")
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF002147))
                    .padding(12.dp)
            ) {
                Text("Bank Nkhonde", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(selectedTab, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        },
        bottomBar = {
            BottomNavigationBar(navController, selectedTab) { tab -> selectedTab = tab }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reports) { report ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(report.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Total: MWK ${report.amount}", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(report.description, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color(0xFF002147)) {
        NavigationBarItem(
            selected = selectedTab == "Dashboard",
            onClick = { onTabSelected("Dashboard"); navController.navigate("dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = selectedTab == "Members",
            onClick = { onTabSelected("Members"); navController.navigate("members") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Members") },
            label = { Text("Members") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = selectedTab == "Reports",
            onClick = { onTabSelected("Reports"); navController.navigate("reports") },
            icon = { Icon(Icons.Default.Description, contentDescription = "Reports") },
            label = { Text("Reports") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = selectedTab == "Settings",
            onClick = { onTabSelected("Settings"); navController.navigate("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}
