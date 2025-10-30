package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Member Contributions") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add new contribution */ }) {
                Text("+")
            }
        }
    ) { padding ->

        val sampleContributions = listOf(
            ContributionData("John Banda", 5000.0, "Oct 2025"),
            ContributionData("Mary Chirwa", 7500.0, "Oct 2025"),
            ContributionData("James Mwale", 4000.0, "Oct 2025"),
            ContributionData("Grace Phiri", 6000.0, "Oct 2025"),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Total Contributions: MWK ${
                    sampleContributions.sumOf { it.amount }
                }",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleContributions) { contribution ->
                    ContributionCard(contribution)
                }
            }
        }
    }
}

@Composable
fun ContributionCard(data: ContributionData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Member: ${data.memberName}", style = MaterialTheme.typography.titleMedium)
            Text("Amount: MWK ${data.amount}")
            Text("Month: ${data.month}")
        }
    }
}

data class ContributionData(
    val memberName: String,
    val amount: Double,
    val month: String
)
