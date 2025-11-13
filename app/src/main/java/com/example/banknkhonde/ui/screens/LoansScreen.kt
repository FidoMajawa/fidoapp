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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.*
import androidx.compose.ui.graphics.Color
import com.example.banknkhonde.ui.models.Loan
import com.example.banknkhonde.ui.models.MemberModel
import com.example.banknkhonde.ui.models.Contribution

enum class LoanTab { Pending, Approved, AddLoan }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoansScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserEmail = currentUser?.email ?: ""

    var loans by remember { mutableStateOf(listOf<Loan>()) }
    var contributions by remember { mutableStateOf(listOf<Contribution>()) }
    var members by remember { mutableStateOf(listOf<MemberModel>()) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(LoanTab.Pending) }

    val scope = rememberCoroutineScope()

    // Load data
    LaunchedEffect(Unit) {
        try {
            // Load loans
            val loanSnap = db.collection("loans")
                .whereEqualTo("chairEmail", currentUserEmail)
                .get().await()
            loans = loanSnap.documents.mapNotNull { it.toObject(Loan::class.java)?.copy(id = it.id) }

            // Load contributions
            val contribSnap = db.collection("contributions")
                .whereEqualTo("chairEmail", currentUserEmail)
                .get().await()
            contributions = contribSnap.documents.mapNotNull { it.toObject(Contribution::class.java) }

            // Load members
            val membersSnap = db.collection("clubMembers")
                .whereEqualTo("chairEmail", currentUserEmail)
                .get().await()
            members = membersSnap.documents.mapNotNull { it.toObject(MemberModel::class.java) }

            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.message
            isLoading = false
        }
    }

    // Approve loan function
    suspend fun approveLoan(db: FirebaseFirestore, loan: Loan) {
        db.collection("loans").document(loan.id).update("status", "Approved").await()
        val contribDoc = db.collection("contributions")
            .whereEqualTo("memberName", loan.memberName)
            .whereEqualTo("chairEmail", currentUserEmail)
            .get().await()
        if (contribDoc.documents.isEmpty()) {
            // Create contribution
            db.collection("contributions").add(
                Contribution(memberName = loan.memberName, amount = -loan.amount, chairEmail = currentUserEmail)
            )
        } else {
            val doc = contribDoc.documents[0]
            val currentAmount = doc.getLong("amount")?.toInt() ?: 0
            db.collection("contributions").document(doc.id)
                .update("amount", currentAmount - loan.amount)
        }
    }

    // Unapprove / Repay loan function
    suspend fun unapproveLoan(db: FirebaseFirestore, loan: Loan) {
        db.collection("loans").document(loan.id).update("status", "Pending").await()
        val contribDoc = db.collection("contributions")
            .whereEqualTo("memberName", loan.memberName)
            .whereEqualTo("chairEmail", currentUserEmail)
            .get().await()
        if (contribDoc.documents.isNotEmpty()) {
            val doc = contribDoc.documents[0]
            val currentAmount = doc.getLong("amount")?.toInt() ?: 0
            db.collection("contributions").document(doc.id)
                .update("amount", currentAmount + loan.amount)
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {

            TabSelector(selectedTab) { selectedTab = it }

            AnimatedContent(targetState = selectedTab) { tab ->
                when (tab) {
                    LoanTab.Pending -> LoanList(
                        loans.filter { it.status == "Pending" },
                        db = db,
                        contributions = contributions,
                        onApprove = { loan -> scope.launch { approveLoan(db, loan) } }
                    )
                    LoanTab.Approved -> LoanList(
                        loans.filter { it.status == "Approved" },
                        db = db,
                        contributions = contributions,
                        onUnapprove = { loan -> scope.launch { unapproveLoan(db, loan) } }
                    )
                    LoanTab.AddLoan -> AddLoanSection(
                        members = members,
                        onAddLoan = { memberName, amount ->
                            scope.launch {
                                db.collection("loans").add(
                                    Loan(
                                        memberName = memberName,
                                        amount = amount,
                                        status = "Pending",
                                        chairEmail = currentUserEmail
                                    )
                                )
                            }
                        }
                    )
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
    TabRow(selectedTabIndex = selectedTab.ordinal) {
        LoanTab.values().forEach { tab ->
            val title = if (tab == LoanTab.AddLoan) "New Loan" else tab.name
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun LoanList(
    loans: List<Loan>,
    db: FirebaseFirestore,
    contributions: List<Contribution>,
    onApprove: ((Loan) -> Unit)? = null,
    onUnapprove: ((Loan) -> Unit)? = null
) {
    if (loans.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No loans in this category.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(loans) { loan ->
                LoanCard(loan, db, contributions, onApprove, onUnapprove)
            }
        }
    }
}

@Composable
fun LoanCard(
    loan: Loan,
    db: FirebaseFirestore,
    contributions: List<Contribution>,
    onApprove: ((Loan) -> Unit)? = null,
    onUnapprove: ((Loan) -> Unit)? = null
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = Currency.getInstance("MWK")
    }
    val formattedAmount = currencyFormat.format(loan.amount).replace("MWK", "MK ")

    val scope = rememberCoroutineScope()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(loan.memberName, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formattedAmount, fontSize = 16.sp)
                Text(loan.status)
            }
            Spacer(Modifier.height(8.dp))
            if (loan.status == "Pending" && onApprove != null) {
                Button(onClick = { scope.launch { onApprove(loan) } }, modifier = Modifier.fillMaxWidth()) {
                    Text("Approve")
                }
            }
            if (loan.status == "Approved" && onUnapprove != null) {
                Button(onClick = { scope.launch { onUnapprove(loan) } }, modifier = Modifier.fillMaxWidth()) {
                    Text("Unapprove / Repay")
                }
            }
        }
    }
}

@Composable
fun AddLoanSection(members: List<MemberModel>, onAddLoan: (String, Int) -> Unit) {
    var memberName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    val memberNames = members.map { "${it.firstName} ${it.lastName}" }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = memberName,
            onValueChange = { memberName = it },
            label = { Text("Member Name") },
            isError = hasError && (memberName.isBlank() || !memberNames.contains(memberName))
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() } },
            label = { Text("Loan Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = hasError && amount.isBlank()
        )

        Button(
            onClick = {
                val amt = amount.toIntOrNull()
                if (memberName.isNotBlank() && amt != null && memberNames.contains(memberName)) {
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
