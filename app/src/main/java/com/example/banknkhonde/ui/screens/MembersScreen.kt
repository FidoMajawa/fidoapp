package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Member(
    val firstName: String = "",
    val lastName: String = "",
    val memberId: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val chairEmail: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    var members by remember { mutableStateOf<List<Member>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load members from Firestore
    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("clubMembers")
                .whereEqualTo("chairEmail", currentUserEmail)
                .get()
                .await()
            members = snapshot.documents.mapNotNull { it.toObject(Member::class.java) }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Failed to load members: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Club Members") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addMember") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search members...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorMessage!!, color = Color.Red)
                    }
                }
                else -> {
                    val filteredMembers = members.filter {
                        it.firstName.contains(searchQuery.text, ignoreCase = true) ||
                                it.lastName.contains(searchQuery.text, ignoreCase = true) ||
                                it.memberId.contains(searchQuery.text, ignoreCase = true)
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredMembers) { member ->
                            MemberCard(member = member)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberCard(member: Member) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (member.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(member.imageUrl),
                        contentDescription = "Member Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("${member.firstName} ${member.lastName}", fontSize = 18.sp, color = Color.Black)
                Text("ID: ${member.memberId}", fontSize = 14.sp, color = Color.Gray)
                Text("Phone: ${member.phoneNumber}", fontSize = 14.sp, color = Color.Gray)
                Text("Email: ${member.email}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
