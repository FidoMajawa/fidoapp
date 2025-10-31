package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

@Composable
fun ContributionScreen(navController: NavController) {
    // This data would come from a ViewModel in a real application
    val contributions = listOf(
        Contribution("Mary Chirwa", 5000, "26 Oct 2025"),
        Contribution("John Banda", 5000, "26 Oct 2025"),
        Contribution("Alice Mwale", 10000, "25 Oct 2025"),
        Contribution("David Kamanga", 5000, "25 Oct 2025"),
        Contribution("Peter Phiri", 5000, "24 Oct 2025")
    )

    // The main Scaffold is in MainActivity, so we only define the content here
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // --- Header ---
        item {
            ContributionHeader()
        }

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

@Composable
fun ContributionHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(NavyBlue, Color(0xFF172A46))
                )
            )
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 40.dp)
    ) {
        Text(
            text = "Contributions",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Track all member savings and deposits.",
            style = MaterialTheme.typography.bodyMedium,
            color = LightSlate
        )
    }
}

@Composable
fun SummaryCard(totalContributions: Int) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = java.util.Currency.getInstance("MWK")
    }
    val formattedTotal = currencyFormat.format(totalContributions).replace("MWK", "MK ")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-24).dp), // Pull the card up into the header
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
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
            color = Color(0xFF2E7D32), // A consistent green for credits
            fontSize = 16.sp
        )
    }
}
