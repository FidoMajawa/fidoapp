package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale

// Data class for a single contribution
data class Contribution(
    val memberName: String,
    val amount: Int,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionScreen(navController: NavController) {
    // Sample contributions
    val contributions = listOf(
        Contribution("Mary Chirwa", 5000, "26 Oct 2025"),
        Contribution("John Banda", 5000, "26 Oct 2025"),
        Contribution("Alice Mwale", 10000, "25 Oct 2025"),
        Contribution("David Kamanga", 5000, "25 Oct 2025"),
        Contribution("Peter Phiri", 5000, "24 Oct 2025")
    )

    // FIX: Replaced the root LazyColumn with a Scaffold for a consistent layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contributions") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Main content is now a LazyColumn inside the Scaffold
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Use padding from Scaffold
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // --- Summary Card ---
            item {
                SummaryCard(totalContributions = contributions.sumOf { it.amount })
            }

            // --- Filter and List Header ---
            item {
                ListHeader()
            }

            // --- List of Contributions ---
            items(contributions) { contribution ->
                ContributionRow(contribution)
            }
        }
    }
}

// REMOVED: The custom ContributionHeader is no longer needed as TopAppBar is used.

@Composable
fun SummaryCard(totalContributions: Int) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = java.util.Currency.getInstance("MWK")
    }
    val formattedTotal = currencyFormat.format(totalContributions).replace("MWK", "MK ")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Adjusted padding
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Savings,
                contentDescription = "Total Contributions",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Total This Month",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedTotal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp), // Adjusted top padding
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Recent Deposits",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = { /* TODO: Implement date filtering */ }) {
            Icon(Icons.Default.DateRange, contentDescription = "Filter by date", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Filter")
        }
    }
}

@Composable
fun ContributionRow(contribution: Contribution) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = java.util.Currency.getInstance("MWK")
    }
    val formattedAmount = currencyFormat.format(contribution.amount).replace("MWK", "+ MK ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Member",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contribution.memberName,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = contribution.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formattedAmount,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            fontSize = 16.sp
        )
    }
}
