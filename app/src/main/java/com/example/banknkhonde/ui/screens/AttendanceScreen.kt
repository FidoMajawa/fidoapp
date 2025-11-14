package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.banknkhonde.model.MemberModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Define the penalty amount as a constant
private const val PENALTY_AMOUNT = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for the list of members
    var members by remember { mutableStateOf(listOf<MemberModel>()) }
    // State to hold the attendance (memberId -> isPresent)
    var attendanceState by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // --- Data Loading ---
    // Load members AND today's attendance data
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            // 1. Fetch Members
            val membersSnapshot = db.collection("clubMembers").get().await()
            val memberList = membersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(MemberModel::class.java)?.copy(memberId = doc.id)
            }
            members = memberList

            // 2. Fetch Today's Attendance Document
            val attendanceDoc = db.collection("attendance").document(today).get().await()
            val existingAttendance = if (attendanceDoc.exists()) {
                // Get the existing map of memberId -> isPresent
                attendanceDoc.data as? Map<String, Boolean> ?: emptyMap()
            } else {
                emptyMap()
            }

            // 3. Create the initial state
            // For each member, check if they have an entry in the map.
            // If not, default them to 'true' (Present).
            val initialState = memberList.associate { member ->
                member.memberId to (existingAttendance[member.memberId] ?: true)
            }
            attendanceState = initialState

        } catch (e: Exception) {
            scope.launch { snackbarHostState.showSnackbar("Error loading data: ${e.message}") }
        } finally {
            isLoading = false
        }
    }

    // --- Derived State ---
    // Calculate total penalty automatically based on the 'attendanceState' map
    val totalPenalty by remember(attendanceState) {
        derivedStateOf {
            // Count how many values are 'false' (absent)
            attendanceState.values.count { !it } * PENALTY_AMOUNT
        }
    }

    // Currency Formatter
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "MW")).apply {
        currency = Currency.getInstance("MWK")
    }

    // --- UI ---
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Daily Attendance") },
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
        },
        bottomBar = {
            // Use BottomAppBar for a clean, persistent summary
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Penalty:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        currencyFormat.format(totalPenalty).replace("MWK", "MK "),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (totalPenalty > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        floatingActionButton = {
            // A "Save" button is much more efficient
            ExtendedFloatingActionButton(
                text = { Text("Save Attendance") },
                icon = { Icon(Icons.Filled.Save, "Save") },
                onClick = {
                    if (isLoading) return@ExtendedFloatingActionButton // Prevent saving while loading

                    scope.launch {
                        try {
                            // Save the entire 'attendanceState' map as the document
                            db.collection("attendance").document(today)
                                .set(attendanceState) // This will overwrite/create the doc
                                .await()
                            snackbarHostState.showSnackbar(
                                "Attendance saved successfully!",
                                withDismissAction = true
                            )
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(
                                "Error saving: ${e.message}",
                                withDismissAction = true
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (members.isEmpty()) {
                Text(
                    "No members found.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // --- Member List ---
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "Marking attendance for: $today",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(members, key = { it.memberId }) { member ->
                        // Get the member's current status from the map, default to 'true'
                        val isPresent = attendanceState[member.memberId] ?: true

                        AttendanceItem(
                            memberName = "${member.firstName} ${member.lastName}",
                            isPresent = isPresent,
                            onStatusChange = { newStatus ->
                                // Update the local state map.
                                // This triggers recomposition for the item and total.
                                attendanceState = attendanceState.toMutableMap().apply {
                                    this[member.memberId] = newStatus
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * A clean, reusable Composable for each member row.
 */
@Composable
private fun AttendanceItem(
    memberName: String,
    isPresent: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            // Member's Name
            headlineContent = { Text(memberName) },
            // Status and Penalty
            supportingContent = {
                Text(
                    text = if (isPresent) "Present" else "Absent (Penalty: MK $PENALTY_AMOUNT)",
                    color = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            // The Toggle Switch
            trailingContent = {
                Switch(
                    checked = isPresent,
                    onCheckedChange = onStatusChange
                )
            }
        )
    }
}