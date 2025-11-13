package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.banknkhonde.ui.models.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.*

/* -----------------------------------
   Reports Screen
----------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var contributions by remember { mutableStateOf(listOf<Contribution>()) }
    var loans by remember { mutableStateOf(listOf<Loan>()) }
    var members by remember { mutableStateOf(listOf<MemberModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch data from Firestore
    LaunchedEffect(Unit) {
        try {
            contributions = db.collection("contributions")
                .get().await().documents.mapNotNull { it.toObject(Contribution::class.java) }

            loans = db.collection("loans")
                .get().await().documents.mapNotNull { it.toObject(Loan::class.java) }

            members = db.collection("clubMembers")
                .get().await().documents.mapNotNull { it.toObject(MemberModel::class.java) }

            isLoading = false
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Failed to load data: ${e.message}"
            isLoading = false
        }
    }

    val totalContributions = contributions.sumOf { it.amount ?: 0.0 }
    val totalLoans = loans.sumOf { it.amount ?: 0.0 }
    val totalMembers = members.size.toDouble()

    // Monthly contributions chart
    val calendar = Calendar.getInstance()
    val monthlyData = MutableList(12) { 0f }
    contributions.forEach {
        val dateMillis = it.date?.toLongOrNull() ?: 0L
        calendar.timeInMillis = dateMillis
        val month = calendar.get(Calendar.MONTH)
        monthlyData[month] += it.amount?.toFloat() ?: 0f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> ReportsContent(
                    totalContributions,
                    totalLoans,
                    totalMembers,
                    monthlyData,
                    loans,
                    members
                )
            }
        }
    }
}

/* -----------------------------------
   Content
----------------------------------- */
@Composable
fun ReportsContent(
    totalContributions: Double,
    totalLoans: Double,
    totalMembers: Double,
    monthlyData: List<Float>,
    loans: List<Loan>,
    members: List<MemberModel>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Summary Cards
        item {
            Text("Summary", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ReportCard("Contributions", totalContributions, Color(0xFF22C55E), Modifier.weight(1f))
                ReportCard("Loans", totalLoans, Color(0xFFEAB308), Modifier.weight(1f))
                ReportCard("Members", totalMembers, Color(0xFF3B82F6), Modifier.weight(1f), isCurrency = false)
            }
        }

        // Monthly Contributions Chart
        item {
            Text("Monthly Contributions", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LineChart(monthlyData)
        }

        // Loans
        item {
            Text("Loans", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (loans.isEmpty()) {
                    Text("No loan data available")
                } else {
                    loans.forEach { loan ->
                        ExpenseItem(loan.status ?: "Unknown", loan.amount ?: 0.0, Color(0xFFEAB308))
                    }
                }
            }
        }

        // Members List
        item { Text("Club Members", style = MaterialTheme.typography.titleMedium) }
        items(members) { member -> MemberItem(member) }
    }
}

/* -----------------------------------
   Reusable Components
----------------------------------- */
@Composable
fun ReportCard(
    title: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier,
    isCurrency: Boolean = true
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, color = color)
            Text(
                if (isCurrency) formatCurrency(amount) else amount.toInt().toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ExpenseItem(title: String, amount: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(12.dp).background(color, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(title)
        }
        Text(formatCurrency(amount), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MemberItem(member: MemberModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Text("${member.firstName ?: ""} ${member.lastName ?: ""}", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            Text("ID: ${member.memberId ?: ""}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun LineChart(
    data: List<Float>,
    modifier: Modifier = Modifier.fillMaxWidth().height(200.dp)
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier.background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("No data for chart")
        }
        return
    }

    Canvas(modifier = modifier) {
        val path = Path()
        val fillPath = Path()
        val xStep = size.width / (data.size - 1).coerceAtLeast(1)
        val yMax = data.maxOrNull() ?: 1f
        val yMin = data.minOrNull() ?: 0f
        val yRange = (yMax - yMin).coerceAtLeast(1f)

        fun getY(value: Float) = (1 - (value - yMin) / yRange) * (size.height * 0.9f) + size.height * 0.05f

        val firstY = getY(data[0])
        path.moveTo(0f, firstY)
        fillPath.moveTo(0f, size.height)
        fillPath.lineTo(0f, firstY)

        data.forEachIndexed { i, value ->
            val x = i * xStep
            val y = getY(value)
            path.lineTo(x, y)
            fillPath.lineTo(x, y)
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        drawPath(
            fillPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFF22C55E).copy(alpha = 0.3f), Color.Transparent),
                startY = 0f,
                endY = size.height
            )
        )
        drawPath(path, color = Color.Black, style = Stroke(width = 4f))
    }
}

/* -----------------------------------
   Utilities
----------------------------------- */
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "MW"))
    format.currency = Currency.getInstance("MWK")
    return format.format(amount).replace("MWK", "MK ")
}

/* -----------------------------------
   Data Models (with defaults)
----------------------------------- */
data class Contribution(
    val memberName: String? = "",
    val amount: Double? = 0.0,
    val date: String? = "",
    val chairEmail: String? = ""
)

data class Loan(
    val memberName: String? = "",
    val amount: Double? = 0.0,
    val status: String? = "",
    val chairEmail: String? = ""
)

data class MemberModel(
    val firstName: String? = "",
    val lastName: String? = "",
    val memberId: String? = "",
    val email: String? = ""
)
