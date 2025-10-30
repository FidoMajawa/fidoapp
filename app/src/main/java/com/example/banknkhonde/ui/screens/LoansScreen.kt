package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Loan(
    val memberName: String,
    val amount: Int,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Pending") }
    val loans = remember {
        mutableStateListOf(
            Loan("John Doe", 50000, "Pending"),
            Loan("Mary Banda", 75000, "Approved"),
            Loan("James Phiri", 100000, "Pending")
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF002147))
                    .padding(12.dp)
            ) {
                Text("Bank Nkhonde", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Loans", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton("Pending", selectedTab) { selectedTab = "Pending" }
                TabButton("Approved", selectedTab) { selectedTab = "Approved" }
                TabButton("Add Loan", selectedTab) { selectedTab = "Add Loan" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            when (selectedTab) {
                "Pending" -> LoanList(loans.filter { it.status == "Pending" })
                "Approved" -> LoanList(loans.filter { it.status == "Approved" })
                "Add Loan" -> AddLoanSection(loans)
            }
        }
    }
}

@Composable
fun RowScope.TabButton(title: String, selectedTab: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (selectedTab == title) Color(0xFF002147) else Color.White,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == title) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun LoanList(loans: List<Loan>) {
    if (loans.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No loans found", color = Color.Gray)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(loans) { loan ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(loan.memberName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Amount: MWK ${loan.amount}", color = Color.Gray)
                        Text(
                            "Status: ${loan.status}",
                            color = if (loan.status == "Approved") Color(0xFF2E7D32) else Color(0xFFFFA000)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddLoanSection(loans: MutableList<Loan>) {
    var memberName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = memberName,
            onValueChange = { memberName = it },
            label = { Text("Member Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Loan Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (memberName.isNotBlank() && amount.toIntOrNull() != null) {
                    loans.add(Loan(memberName, amount.toInt(), "Pending"))
                    memberName = ""
                    amount = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Loan")
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF002147)) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = Color.White)
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("members") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Members") },
            label = { Text("Members") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = Color.White)
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("reports") },
            icon = { Icon(Icons.Default.Description, contentDescription = "Reports") },
            label = { Text("Reports") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = Color.White)
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = Color.White)
        )
    }
}
