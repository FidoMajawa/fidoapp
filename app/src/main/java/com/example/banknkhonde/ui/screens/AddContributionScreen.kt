package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContributionScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var memberName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Contribution") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = memberName,
                onValueChange = { memberName = it },
                label = { Text("Member Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (memberName.isBlank() || amount.isBlank()) {
                        error = "Please fill all fields"
                        return@Button
                    }

                    loading = true
                    val chairEmail = auth.currentUser?.email ?: "unknown"

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val today = dateFormat.format(Date())

                    val data = mapOf(
                        "memberName" to memberName,
                        "amount" to amount.toInt(),
                        "date" to today,
                        "chairEmail" to chairEmail
                    )

                    db.collection("contributions")
                        .add(data)
                        .addOnSuccessListener {
                            loading = false
                            navController.navigateUp()
                        }
                        .addOnFailureListener {
                            loading = false
                            error = "Failed to save: ${it.message}"
                        }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Contribution")
                }
            }
        }
    }
}
