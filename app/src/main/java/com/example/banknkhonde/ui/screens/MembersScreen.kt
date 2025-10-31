package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
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

// Data class remains the same
data class Member(
    val name: String,
    val loan: Int,
    val balance: Int,
    val phone: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(navController: NavController) {
    // Sample data - in a real app, this would come from a ViewModel
    val members = listOf(
        Member("John Banda", 5000, 2000, "0999 001 098"),
        Member("Mary Chirwa", 10000, 5000, "0999 002 764"),
        Member("Peter Phiri", 0, 3000, "0999 645 003"), // Member with no loan
        Member("Alice Mwale", 12000, 8000, "0999 435 004"),
        Member("David Kamanga", 6000, 2500, "0999 433 005")
    )

    // A single Scaffold should be in your main navigation host (e.g., AppNavHost.kt)
    // This screen should not have its own Scaffold, TopBar, or BottomBar.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // Use theme colors
    ) {
        // --- Search Bar ---
        OutlinedTextField(
            value = "", // This would be a state variable in a real app
            onValueChange = {},
            placeholder = { Text("Search members...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // --- Member List ---
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(members) { member ->
                MemberCard(member = member, onClick = { /* TODO: Navigate to member details */ })
            }
        }
    }
}

@Composable
fun MemberCard(member: Member, onClick: () -> Unit) {
    // Format numbers with commas for better readability
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW"))
    currencyFormat.currency = java.util.Currency.getInstance("MWK")
    val formattedLoan = currencyFormat.format(member.loan)
    val formattedBalance = currencyFormat.format(member.balance)

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Profile Initial ---
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.first().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- Member Details ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Display loan status more clearly
                if (member.loan > 0) {
                    Text(
                        text = "Active Loan: ${formattedLoan.replace("MWK", "MK")}",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "No active loan",
                        color = Color(0xFF00695C), // A calming green color
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // --- Arrow Icon ---
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
