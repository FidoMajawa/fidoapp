package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import java.text.NumberFormat
import java.util.Locale

// Data class for a report. It's good to add an icon for visual distinction.
data class Report(
    val title: String,
    val amount: Int,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun ReportsScreen(navController: NavController) {
    // Sample data for the reports. This would come from a ViewModel.
    // Assigning a unique icon and color to each report type makes the UI richer.
    val reports = listOf(
        Report(
            "Monthly Contributions",
            500000,
            "Summary of all member deposits.",
            Icons.Default.AccountBalanceWallet,
            Color(0xFF1E88E5) // Blue
        ),
        Report(
            "Loan Status",
            1200000,
            "Total outstanding and paid loans.",
            Icons.Default.MonetizationOn,
            Color(0xFFE53935) // Red
        ),
        Report(
            "Interest Accrued",
            75000,
            "Interest from savings and loans.",
            Icons.AutoMirrored.Filled.TrendingUp,
            Color(0xFF43A047) // Green
        ),
        Report(
            "Yearly Summary",
            6500000,
            "Complete financial overview.",
            Icons.Default.Assessment,
            Color(0xFF8E24AA) // Purple
        )
    )

    // FIX: Removed the conflicting Scaffold, TopBar, and BottomBar.
    // This screen's content will now correctly render inside the main AppNavHost Scaffold.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            ReportHeader()
        }

        // --- List of Reports ---
        items(reports) { report ->
            ReportCard(report = report, onClick = { /* TODO: Navigate to specific report details */ })
        }
    }
}

@Composable
fun ReportHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = "Financial Reports",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Review and export your club's financial data.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ReportCard(report: Report, onClick: () -> Unit) {
    // Format the currency for better readability
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW"))
    currencyFormat.currency = java.util.Currency.getInstance("MWK")
    val formattedAmount = currencyFormat.format(report.amount).replace("MWK", "MK ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Icon ---
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(report.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = report.icon,
                    contentDescription = report.title,
                    tint = report.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- Details Column ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = report.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // --- Amount and Arrow ---
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formattedAmount,
                    color = report.color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(14.dp)
                )
            }
        }
    }
}
