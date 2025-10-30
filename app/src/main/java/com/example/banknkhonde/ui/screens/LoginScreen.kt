package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {

    var clubId by remember { mutableStateOf("") }
    var memberPin by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo / Title
        Text(text = "💰", fontSize = 64.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text(
            text = "Bank Nkhonde",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = NavyBlue,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Digital Savings Club Management",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Login Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Secure Member Sign In",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Club ID Input
                OutlinedTextField(
                    value = clubId,
                    onValueChange = { clubId = it },
                    label = { Text("Club ID") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Club ID") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                // Member PIN Input
                OutlinedTextField(
                    value = memberPin,
                    onValueChange = { memberPin = it },
                    label = { Text("Member PIN / Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error Message
                loginError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign In Button (sample ID: bank2025 / admin2025)
                Button(
                    onClick = {
                        if (clubId == "bank2025" && memberPin == "admin2025") {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            loginError = "Invalid Club ID or PIN."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Sign In Securely", fontWeight = FontWeight.Bold, color = Gold)
                }
            }
        }

        // Footnote
        Text(
            text = "Secure login via encrypted token",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
