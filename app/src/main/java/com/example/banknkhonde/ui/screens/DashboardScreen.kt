package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// 1. FIX: Import icons from the 'filled' set for a consistent look
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward // 2. FIX: Add missing import
import androidx.compose.material.icons.filled.ArrowUpward   // 2. FIX: Add missing import
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// 3. FIX: Removed the redundant color definitions.
// They are automatically available because Color.kt is in the same package.
// val NavyBlue = Color(0xFF0A192F) <-- REMOVED
// val Gold = Color(0xFFFFD700)     <-- REMOVED
// val Slate = Color(0xFF8892B0)      <-- REMOVED
// val LightSlate = Color(0xFFCCD6F6) <-- REMOVED

@Composable
fun DashboardScreen(navController: NavController) {
    // This content now renders correctly inside the main AppNavHost Scaffold.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest) // Use a lighter, modern background
    ) {
        // --- Header Section ---
        item { DashboardHeader(name = "Club Administrator") }

        // --- Quick Actions Section ---
        item {
            DashboardQuickActions(navController)
        }

        // --- Recent Activity Section ---
        item {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // --- Transaction List ---
        val transactions = listOf(
            Triple("Loan repayment - P. Majawa", "+ MK 15,000", "27 Oct 2025"),
            Triple("Contribution - M. Chirwa", "+ MK 5,000", "26 Oct 2025"),
            Triple("Loan disbursed - K. Banda", "- MK 50,000", "25 Oct 2025")
        )
        items(transactions) { (desc, amount, date) ->
            TransactionRow(
                description = desc,
                amount = amount,
                date = date
            )
        }

        // Add padding at the bottom to avoid content being too close to the navigation bar
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

/* -------------------------------------------
   Redesigned & Professional Helper Composables
-------------------------------------------- */

@Composable
fun DashboardHeader(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(listOf(NavyBlue, Color(0xFF172A46))))
            .padding(top = 40.dp, bottom = 24.dp, start = 20.dp, end = 20.dp) // Added top padding for status bar
    ) {
        Column {
            Text(text = "Welcome back,", color = LightSlate, style = MaterialTheme.typography.titleMedium)
            Text(text = name, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            // Summary Card within the header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Savings", color = LightSlate, style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "MK 1.24M",
                            color = Gold,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        thickness = 1.dp,
                        color = Slate.copy(alpha = 0.5f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Growth", color = LightSlate, style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "+12.5%",
                            color = Color(0xFF64FFDA),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardQuickActions(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Replaced "Settings" with "Contributions" and used appropriate icons
        val actions = listOf(
            ActionItem("Members", Icons.Filled.Group, "members"),
            ActionItem("Loans", Icons.Filled.AccountBalanceWallet, "loans"),
            ActionItem("Contributions", Icons.Filled.Savings, "contributions"),
            ActionItem("Reports", Icons.Filled.BarChart, "reports")
        )

        // Display actions in a 2x2 grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardActionCard(actions[0], modifier = Modifier.weight(1f)) { navController.navigate(actions[0].route) }
                DashboardActionCard(actions[1], modifier = Modifier.weight(1f)) { navController.navigate(actions[1].route) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardActionCard(actions[2], modifier = Modifier.weight(1f)) { navController.navigate(actions[2].route) }
                DashboardActionCard(actions[3], modifier = Modifier.weight(1f)) { navController.navigate(actions[3].route) }
            }
        }
    }
}

data class ActionItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun DashboardActionCard(item: ActionItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(item.icon, contentDescription = item.label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionRow(description: String, amount: String, date: String) {
    val isCredit = amount.startsWith("+")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCredit) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
            contentDescription = null,
            tint = if (isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isCredit) Color(0xFF2E7D32).copy(alpha = 0.1f) else MaterialTheme.colorScheme.error.copy(
                        alpha = 0.1f
                    )
                )
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(description, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            text = amount,
            fontWeight = FontWeight.Bold,
            color = if (isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            fontSize = 16.sp
        )
    }
}
