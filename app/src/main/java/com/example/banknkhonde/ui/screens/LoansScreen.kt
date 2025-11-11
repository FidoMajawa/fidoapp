package com.example.banknkhonde.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

// --- Data class ---
data class Loan(
    val id: String = "",
    val memberName: String = "",
    val amount: Int = 0,
    val status: String = "Pending", // "Pending" or "Approved"
    val chairEmail: String = ""
)

// --- Tabs ---
enum class LoanTab { Pending, Approved, AddLoan }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoansScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(LoanTab.Pending) }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserEmail = currentUser?.email ?: ""

    var loans by remember { mutableStateOf(listOf<Loan>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load loans from Firestore
    LaunchedEffect(currentUserEmail) {
        isLoading = true
        try {
            val snapshot = db.collection("loans")
                .whereEqualTo("chairEmail", currentUserEmail)
                .get()
                .await()
            loans = snapshot.documents.mapNotNull { doc ->
                val loan = doc.toObject(Loan::class.java)
                loan?.copy(id = doc.id)
            }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Failed to load loans: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {

            TabSelector(selectedTab) { selectedTab = it }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { it }) togetherWith
                            (fadeOut() + slideOutHorizontally { -it })
                },
                label = "TabAnimation"
            ) { tab ->
                Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    when (tab) {
                        LoanTab.Pending -> LoanList(
                            loans.filter { it.status == "Pending" },
                            onApprove = { loan ->
                                db.collection("loans").document(loan.id)
                                    .update("status", "Approved")
                            }
                        )
                        LoanTab.Approved -> LoanList(
                            loans.filter { it.status == "Approved" },
                            onUnapprove = { loan ->
                                db.collection("loans").document(loan.id)
                                    .update("status", "Pending")
                            }
                        )
                        LoanTab.AddLoan -> AddLoanSection(onAddLoan = { memberName, amount ->
                            val newLoan = Loan(
                                memberName = memberName,
                                amount = amount,
                                status = "Pending",
                                chairEmail = currentUserEmail
                            )
                            db.collection("loans").add(newLoan)
                        })
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMessage?.let { msg ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(msg, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun TabSelector(selectedTab: LoanTab, onTabSelected: (LoanTab) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
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
fun LoanList(
    loans: List<Loan>,
    onApprove: ((Loan) -> Unit)? = null,
    onUnapprove: ((Loan) -> Unit)? = null
) {
    if (loans.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No loans in this category.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(loans) { loan ->
                LoanCard(loan, onApprove, onUnapprove)
            }
        }
    }
}

@Composable
fun LoanCard(
    loan: Loan,
    onApprove: ((Loan) -> Unit)? = null,
    onUnapprove: ((Loan) -> Unit)? = null
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = java.util.Currency.getInstance("MWK")
    }
    val formattedAmount = currencyFormat.format(loan.amount).replace("MWK", "MK ")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(loan.memberName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formattedAmount, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (loan.status == "Approved") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                    contentColor = if (loan.status == "Approved") Color(0xFF2E7D32) else Color(0xFFFFA000)
                ) {
                    Text(loan.status, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Approve / Unapprove Button
            when {
                loan.status == "Pending" && onApprove != null -> {
                    Button(onClick = { onApprove(loan) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Approve")
                    }
                }
                loan.status == "Approved" && onUnapprove != null -> {
                    Button(onClick = { onUnapprove(loan) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Unapprove")
                    }
                }
            }
        }
    }
}

@Composable
fun AddLoanSection(onAddLoan: (String, Int) -> Unit) {
    var memberName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = memberName,
            onValueChange = { memberName = it },
            label = { Text("Member Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = hasError && memberName.isBlank()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() } },
            label = { Text("Loan Amount") },
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = hasError && amount.isBlank()
        )

        Button(
            onClick = {
                val amt = amount.toIntOrNull()
                if (memberName.isNotBlank() && amt != null) {
                    onAddLoan(memberName, amt)
                    memberName = ""
                    amount = ""
                    hasError = false
                } else {
                    hasError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Loan Application")
        }
    }
}
