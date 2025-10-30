package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

// Import your colors
import com.example.banknkhonde.ui.screens.NavyBlue
import com.example.banknkhonde.ui.screens.Gold
import com.example.banknkhonde.ui.screens.Slate
import com.example.banknkhonde.ui.screens.LightSlate

@Composable
fun DashboardScreenUnique(navController: NavController) {
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            DashboardBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        "home" -> navController.navigate("dashboard")
                        "notifications" -> navController.navigate("notifications")
                        "settings" -> navController.navigate("settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f))
                .padding(padding)
        ) {
            item { DashboardHeaderSection(name = "Club Administrator") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { DashboardQuickActionsSection(navController) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { DashboardFinancialSummaryCard() }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Recent activity",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(
                listOf(
                    Triple("Loan repayment - J. Doe", "+ MK 15,000", "27 Oct 2025"),
                    Triple("Contribution - M. Chirwa", "+ MK 5,000", "26 Oct 2025"),
                    Triple("Loan disbursed - K. Banda", "- MK 50,000", "25 Oct 2025")
                )
            ) { (desc, amount, date) ->
                DashboardTransactionRow(
                    description = desc,
                    amount = amount,
                    date = date,
                    isCredit = amount.startsWith("+")
                )
            }
        }
    }
}

/* -------------------------------
   Dashboard helper composables
-------------------------------- */

@Composable
fun DashboardHeaderSection(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(listOf(NavyBlue, Color(0xFF172A46))))
            .padding(20.dp)
    ) {
        Column {
            Text(text = "Welcome back,", color = LightSlate, fontSize = 18.sp)
            Text(text = name, color = Gold, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Savings", color = LightSlate)
                    Text(
                        text = "MK 1,240,500.00",
                        color = Gold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Growth +12.5%", color = Color(0xFF64FFDA), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun DashboardQuickActionsSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            DashboardActionCard(
                label = "Members",
                icon = Icons.Default.Person,
                onClick = { navController.navigate("members") },
                modifier = Modifier.weight(1f)
            )
            DashboardActionCard(
                label = "Loans",
                icon = Icons.Default.AccountCircle,
                onClick = { navController.navigate("loans") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            DashboardActionCard(
                label = "Reports",
                icon = Icons.Default.Description,
                onClick = { navController.navigate("reports") },
                modifier = Modifier.weight(1f)
            )
            DashboardActionCard(
                label = "Settings",
                icon = Icons.Default.Settings,
                onClick = { navController.navigate("settings") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DashboardActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = label, tint = NavyBlue, modifier = Modifier.size(28.dp))
            Text(label, style = MaterialTheme.typography.titleMedium, color = NavyBlue, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DashboardFinancialSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Financial Overview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Track total contributions, loan repayments and accrued interests. Quick snapshots of your club financial health.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate
            )
        }
    }
}

@Composable
fun DashboardTransactionRow(description: String, amount: String, date: String, isCredit: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NavyBlue)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(description, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(date, style = MaterialTheme.typography.bodySmall, color = Slate)
        }

        Text(
            amount,
            fontWeight = FontWeight.Bold,
            color = if (isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun DashboardBottomNavBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = NavyBlue) {
        NavigationBarItem(
            selected = selectedTab == "home",
            onClick = { onTabSelected("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold, selectedTextColor = Gold)
        )

        NavigationBarItem(
            selected = selectedTab == "notifications",
            onClick = { onTabSelected("notifications") },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
            label = { Text("Alerts") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold, selectedTextColor = Gold)
        )

        NavigationBarItem(
            selected = selectedTab == "settings",
            onClick = { onTabSelected("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold, selectedTextColor = Gold)
        )
    }
}
