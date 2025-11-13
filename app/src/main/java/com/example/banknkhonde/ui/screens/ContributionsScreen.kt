package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.NumberFormat
import java.util.*
import com.example.banknkhonde.ui.models.Contribution


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    var contributions by remember { mutableStateOf(listOf<Contribution>()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        val listener: ListenerRegistration =
            db.collection("contributions")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        error = "Failed to load contributions: ${e.message}"
                        loading = false
                        return@addSnapshotListener
                    }

                    contributions = snapshot?.documents?.mapNotNull {
                        it.toObject(Contribution::class.java)
                    } ?: emptyList()

                    loading = false
                }

        onDispose { listener.remove() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contributions") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("addContribution") }) {
                        Icon(Icons.Default.Add, "Add Contribution")
                    }
                }
            )
        }
    ) { padding ->

        when {
            loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }

            error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(error!!, color = Color.Red)
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                item {
                    SummaryCard(contributions.sumOf { it.amount })
                }

                item { ListHeader() }

                items(contributions) {
                    ContributionRow(it)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(total: Int) {
    val nf = NumberFormat.getCurrencyInstance(Locale("en", "MW"))
    nf.currency = Currency.getInstance("MWK")
    val formatted = nf.format(total).replace("MWK", "MK ")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Savings, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Total Contributions", color = Color.Gray)
                Text(formatted, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun ListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Recent Deposits", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ContributionRow(c: Contribution) {
    val nf = NumberFormat.getCurrencyInstance(Locale("en", "MW"))
    nf.currency = Currency.getInstance("MWK")
    val amount = nf.format(c.amount).replace("MWK", "+ MK ")

    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Person,
            null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFEFEF))
                .padding(8.dp),
            tint = Color.Gray
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(c.memberName, fontWeight = FontWeight.SemiBold)
            Text(c.date, fontSize = 12.sp, color = Color.Gray)
        }

        Text(amount, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
    }
}
