package com.example.banknkhonde.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

import com.example.banknkhonde.ui.screens.Gold
import com.example.banknkhonde.ui.screens.LightSlate
import com.example.banknkhonde.ui.screens.NavyBlue
import com.example.banknkhonde.ui.screens.Slate
import com.example.banknkhonde.ui.models.Contribution
import com.example.banknkhonde.ui.models.Loan

/* -----------------------------------------------------
   Transaction Data Class
------------------------------------------------------ */
data class TransactionItem(
    val description: String,
    val amount: Int,
    val date: String,
    val type: String // "credit" or "debit"
)

/* -----------------------------------------------------
   Dashboard Screen
------------------------------------------------------ */
@SuppressLint("UnrememberedMutableState")
@Composable
fun DashboardScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var contributions by remember { mutableStateOf(listOf<Contribution>()) }
    var loans by remember { mutableStateOf(listOf<Loan>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load contributions and loans from Firestore
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val contributionsSnapshot = db.collection("contributions").get().await()
            contributions = contributionsSnapshot.documents.mapNotNull { it.toObject(Contribution::class.java) }

            val loansSnapshot = db.collection("loans").get().await()
            loans = loansSnapshot.documents.mapNotNull { it.toObject(Loan::class.java) }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Compute total savings and growth
    val totalSavings = contributions.sumOf { it.amount }
    val growth = if (contributions.size >= 2) {
        val prevTotal = contributions.dropLast(1).sumOf { it.amount }
        if (prevTotal != 0) ((totalSavings - prevTotal) * 100 / prevTotal) else 0
    } else 0

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = Currency.getInstance("MWK")
    }
    val totalFormatted = currencyFormat.format(totalSavings).replace("MWK", "MK ")
    val growthFormatted = if (growth >= 0) "+$growth%" else "$growth%"

    // Merge contributions and loans for Recent Activity
    val transactions by derivedStateOf {
        val list = mutableListOf<TransactionItem>()
        contributions.forEach {
            list.add(TransactionItem("Contribution - ${it.memberName}", it.amount, it.date, "credit"))
        }
        loans.forEach { loan ->
            val type = if (loan.status == "repaid") "credit" else "debit"
            val desc = when (loan.status) {
                "repaid" -> "Loan repayment - ${loan.memberName}"
                "approved" -> "Loan disbursed - ${loan.memberName}"
                else -> "Loan - ${loan.memberName}"
            }
            list.add(TransactionItem(desc, loan.amount, loan.date, type))
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        list.sortedByDescending {
            try { sdf.parse(it.date) ?: Date(0) } catch (e: Exception) { Date(0) }
        }
    }

    val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                item {
                    DashboardHeader(name = "Club Administrator", total = totalFormatted, growth = growthFormatted)
                }
                item { DashboardQuickActions(navController) }

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

                items(transactions) { t ->
                    val amountFormatted = currencyFormat.format(t.amount).replace(
                        "MWK",
                        if (t.type == "credit") "+ MK " else "- MK "
                    )
                    val displayDate = try { displayDateFormat.format(SimpleDateFormat("yyyy-MM-dd").parse(t.date)) } catch (e: Exception) { t.date }
                    TransactionRow(description = t.description, amount = amountFormatted, date = displayDate)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

/* -----------------------------------------------------
   Dashboard Header
------------------------------------------------------ */
@Composable
fun DashboardHeader(name: String, total: String, growth: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(listOf(NavyBlue, Color(0xFF172A46))))
            .padding(top = 40.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
    ) {
        Column {
            Text(text = "Welcome back,", color = LightSlate, style = MaterialTheme.typography.titleMedium)
            Text(text = name, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

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
                        Text(total, color = Gold, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                    VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = Slate.copy(alpha = 0.5f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Growth", color = LightSlate, style = MaterialTheme.typography.bodySmall)
                        Text(growth, color = if (growth.startsWith("+")) Color(0xFF64FFDA) else Color.Red, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

/* -----------------------------------------------------
   Quick Actions
------------------------------------------------------ */
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

        val actions = listOf(
            ActionItem("Members", Icons.Filled.Group, "members"),
            ActionItem("Loans", Icons.Filled.AccountBalanceWallet, "loans"),
            ActionItem("Contributions", Icons.Filled.Savings, "contributions"),
            ActionItem("Reports", Icons.Filled.BarChart, "reports")
        )

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

/* -----------------------------------------------------
   Transaction Row
------------------------------------------------------ */
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
                .background(if (isCredit) Color(0xFF2E7D32).copy(alpha = 0.1f) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(description, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            amount,
            fontWeight = FontWeight.Bold,
            color = if (isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            fontSize = 16.sp
        )
    }
}

/* -----------------------------------------------------
   Bottom Navigation Bar
------------------------------------------------------ */
@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = NavyBlue) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("dashboard") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Gold) },
            label = { Text("Home", color = LightSlate) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("notifications") },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Gold) },
            label = { Text("Notifications", color = LightSlate) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("settings") },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Gold) },
            label = { Text("Settings", color = LightSlate) }
        )
    }
}
