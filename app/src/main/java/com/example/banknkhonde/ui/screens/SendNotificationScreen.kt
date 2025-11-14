package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SendNotificationScreen(navController: androidx.navigation.NavHostController) {

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("info") }

    val db = FirebaseFirestore.getInstance()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp)
        ) {

            Text("Send Notification", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = userEmail,
                onValueChange = { userEmail = it },
                label = { Text("Send to Email (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    val data = hashMapOf(
                        "title" to title,
                        "message" to message,
                        "userEmail" to userEmail,
                        "time" to System.currentTimeMillis().toString(),
                        "type" to type,
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )

                    db.collection("notifications").add(data)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Notification")
            }
        }
    }
}
