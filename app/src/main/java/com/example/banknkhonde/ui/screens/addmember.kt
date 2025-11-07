package com.example.banknkhonde.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// Data class to represent a new member
data class NewMember(
    val firstName: String,
    val lastName: String,
    val memberId: String,
    val phoneNumber: String,
    val email: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var memberId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // State for validation errors
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var memberIdError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun validateFields(): Boolean {
        firstNameError = if (firstName.isBlank()) "First name cannot be empty" else null
        lastNameError = if (lastName.isBlank()) "Last name cannot be empty" else null
        memberIdError = if (memberId.isBlank()) "Member ID cannot be empty" else null
        return firstNameError == null && lastNameError == null && memberIdError == null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add New Member") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Make the column scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // First Name Field
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it; firstNameError = null },
                label = { Text("First Name*") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = firstNameError != null,
                supportingText = { if (firstNameError != null) Text(firstNameError!!) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Last Name Field
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it; lastNameError = null },
                label = { Text("Last Name*") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = lastNameError != null,
                supportingText = { if (lastNameError != null) Text(lastNameError!!) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Member ID Field
            OutlinedTextField(
                value = memberId,
                onValueChange = { memberId = it; memberIdError = null },
                label = { Text("Member ID*") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = memberIdError != null,
                supportingText = { if (memberIdError != null) Text(memberIdError!!) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Phone Number Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = {
                    if (validateFields()) {
                        val newMember = NewMember(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            memberId = memberId.trim(),
                            phoneNumber = phoneNumber.trim(),
                            email = email.trim()
                        )
                        // --- TODO: Add your logic here ---
                        // For example, save the 'newMember' to your database or ViewModel
                        // Show a confirmation message
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Member ${newMember.firstName} added successfully!",
                                duration = SnackbarDuration.Short
                            )
                        }
                        // Navigate back after successful submission
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Member", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMemberScreenPreview() {
    // FIX: Replaced the non-existent 'BankNkhondeTheme' with the default 'MaterialTheme'
    MaterialTheme {
        AddMemberScreen(navController = rememberNavController())
    }
}
