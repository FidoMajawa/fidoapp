package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding

data class Member(
    val name: String,
    val loan: Int,
    val balance: Int,
    val phone: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(navController: NavController) {
    val members = listOf(
        Member("John Banda", 5000, 2000, "0999 001 098"),
        Member("Mary Chirwa", 10000, 5000, "0999 002 764"),
        Member("Peter Phiri", 0, 3000, "0999 645 003"),
        Member("Alice Mwale", 12000, 8000, "0999 435 004"),
        Member("David Kamanga", 6000, 2500, "0999 433 005")
    )

    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding() // Content starts below status bar
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- Search Bar ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search members...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // --- Filtered Member List ---
            val filteredMembers = members.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMembers) { member ->
                    MemberCard(member = member, onClick = {
                        // TODO: navigate to details
                        // navController.navigate("memberDetails/${member.name}")
                    })
                }
            }
        }

        // --- Floating Buttons ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(), // Avoid navigation bar
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Warning Button
            FloatingActionButton(
                onClick = { /* TODO: show alerts */ },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Alerts")
            }

            // Add Button
            FloatingActionButton(
                onClick = { /* TODO: navigate to add member */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    }
}

@Composable
fun MemberCard(member: Member, onClick: () -> Unit) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW"))
    currencyFormat.currency = Currency.getInstance("MWK")
    val formattedLoan = currencyFormat.format(member.loan)
    val formattedBalance = currencyFormat.format(member.balance)

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Initial
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

            // Member Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

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
                        color = Color(0xFF00695C),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
