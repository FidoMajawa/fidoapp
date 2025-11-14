package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.banknkhonde.ui.models.Contribution
import com.example.banknkhonde.ui.models.Loan
import com.example.banknkhonde.ui.models.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    var notifications by remember { mutableStateOf(listOf<NotificationItem>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Real-time listener for contributions and loans
    LaunchedEffect(Unit) {
        isLoading = true

        // Listen to contributions
        db.collection("contributions")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { contributionSnapshot, _ ->
                val contributionNotifications = mutableListOf<NotificationItem>()
                contributionSnapshot?.documents?.forEach { doc ->
                    val contribution = doc.toObject(Contribution::class.java)
                    if (contribution != null) {
                        contributionNotifications.add(
                            NotificationItem(
                                title = "New Contribution",
                                message = "${contribution.memberName} contributed MK ${contribution.amount}",
                                time = contribution.date ?: "",
                                userEmail = "",
                                type = "contribution"
                            )
                        )
                    }
                }

                // Listen to loans
                db.collection("loans")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { loanSnapshot, _ ->
                        val loanNotifications = mutableListOf<NotificationItem>()
                        loanSnapshot?.documents?.forEach { doc ->
                            val loan = doc.toObject(Loan::class.java)
                            if (loan != null) {
                                loanNotifications.add(
                                    NotificationItem(
                                        title = "Loan Update",
                                        message = "${loan.memberName} loan status: ${loan.status}",
                                        time = loan.date ?: "",
                                        userEmail = "",
                                        type = "loan"
                                    )
                                )
                            }
                        }

                        // Merge notifications
                        notifications = (contributionNotifications + loanNotifications)
                            .sortedByDescending { it.time } // sort newest first
                        isLoading = false
                    }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                notifications.isEmpty() -> Text(
                    text = "No notifications available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notifications) { item ->
                        NotificationCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                item.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                item.time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
