package com.example.banknkhonde.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding

// Data class remains the same
data class Loan(
    val memberName: String,
    val amount: Int,
    val status: String // "Pending" or "Approved"
)

// Enum for managing tabs to avoid string errors
enum class LoanTab {
    Pending,
    Approved,
    AddLoan
}

@Composable
fun LoansScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(LoanTab.Pending) }

    // Sample loans list
    val loans = remember {
        mutableStateListOf(
            Loan("John Banda", 50000, "Pending"),
            Loan("Mary Chirwa", 75000, "Approved"),
            Loan("Peter Phiri", 120000, "Pending"),
            Loan("Alice Mwale", 45000, "Approved")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding() // Content starts below status bar
    ) {
        // --- Header ---
        ScreenHeader()

        // --- Tab Selector ---
        TabSelector(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // --- Animated Content ---
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                (fadeIn() + slideInHorizontally { it })
                    .togetherWith(fadeOut() + slideOutHorizontally { -it })
            },
            label = "Tab Animation"
        ) { tab ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                when (tab) {
                    LoanTab.Pending -> LoanList(loans.filter { it.status == "Pending" })
                    LoanTab.Approved -> LoanList(loans.filter { it.status == "Approved" })
                    LoanTab.AddLoan -> AddLoanSection(
                        onAddLoan = { newLoan ->
                            loans.add(newLoan)
                            selectedTab = LoanTab.Pending
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Loan Management",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Review pending requests and manage approved loans.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TabSelector(selectedTab: LoanTab, onTabSelected: (LoanTab) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        LoanTab.values().forEach { tab ->
            val title = when (tab) {
                LoanTab.AddLoan -> "New Loan"
                else -> tab.name
            }
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(title, fontWeight = FontWeight.Bold) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoanList(loans: List<Loan>) {
    if (loans.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No loans in this category.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(loans) { loan ->
                LoanCard(loan = loan)
            }
        }
    }
}

@Composable
fun LoanCard(loan: Loan) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = java.util.Currency.getInstance("MWK")
    }
    val formattedAmount = currencyFormat.format(loan.amount).replace("MWK", "MK ")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = loan.memberName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedAmount,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (loan.status == "Approved") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                    contentColor = if (loan.status == "Approved") Color(0xFF2E7D32) else Color(0xFFFFA000)
                ) {
                    Text(
                        text = loan.status,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddLoanSection(onAddLoan: (Loan) -> Unit) {
    var memberName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(), // Avoid bottom navigation bar
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = memberName,
            onValueChange = { memberName = it },
            label = { Text("Member Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = hasError && memberName.isBlank()
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { char -> char.isDigit() } },
            label = { Text("Loan Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
            isError = hasError && amount.isBlank()
        )
        Button(
            onClick = {
                val amountInt = amount.toIntOrNull()
                if (memberName.isNotBlank() && amountInt != null) {
                    onAddLoan(Loan(memberName, amountInt, "Pending"))
                    hasError = false
                } else {
                    hasError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Submit Loan Application", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
