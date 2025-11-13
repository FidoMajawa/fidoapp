package com.example.banknkhonde.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.tasks.await


/* -----------------------------
   Member Data Class
------------------------------ */
data class NewMember(
    val firstName: String = "",
    val lastName: String = "",
    val memberId: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val chairEmail: String = ""
)

/* -----------------------------
   Add Member Screen
------------------------------ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var memberId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    fun validateFields(): Boolean =
        firstName.isNotBlank() && lastName.isNotBlank() && memberId.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add New Member") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            // Form Fields
            OutlinedTextField(
                value = firstName, onValueChange = { firstName = it },
                label = { Text("First Name*") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                label = { Text("Last Name*") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = memberId, onValueChange = { memberId = it },
                label = { Text("Member ID*") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                ),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber, onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                ),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
                ),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Add Member Button
            Button(
                onClick = {
                    if (!validateFields()) {
                        scope.launch { snackbarHostState.showSnackbar("Fill all required fields!") }
                        return@Button
                    }

                    scope.launch {
                        try {
                            var imageUrl = ""
                            selectedImageUri?.let { uri ->
                                // 1. Create a safe and unique file name.
                                // Using a UUID ensures the file name is unique and avoids
                                // potential overwrites or caching issues.
                                val safeMemberId = memberId.replace("[^a-zA-Z0-9]".toRegex(), "_")
                                val fileName = "${safeMemberId}_${UUID.randomUUID()}"
                                val ref = storage.reference.child("memberImages/$fileName")

                                // 2. Upload the file and get the TaskSnapshot result
                                val taskSnapshot = ref.putFile(uri).await()

                                // 3. Get the download URL from the *snapshot's* reference
                                // This is the correct, reliable way to get the URL
                                // and avoids the "object does not exist" race condition.
                                // Your retry loop is no longer needed.
                                imageUrl = taskSnapshot.storage.downloadUrl.await().toString()
                            }

                            val newMember = NewMember(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                memberId = memberId.trim(),
                                phoneNumber = phoneNumber.trim(),
                                email = email.trim(),
                                imageUrl = imageUrl,
                                chairEmail = currentUserEmail
                            )

                            // Save member to Firestore
                            db.collection("clubMembers")
                                .document(memberId)
                                .set(newMember)
                                .await()

                            snackbarHostState.showSnackbar("Member added successfully!")
                            navController.popBackStack()

                        } catch (e: Exception) {
                            // Provide a slightly more detailed error message
                            snackbarHostState.showSnackbar("Error adding member: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Member", fontSize = 16.sp)
            }
        }
    }
}