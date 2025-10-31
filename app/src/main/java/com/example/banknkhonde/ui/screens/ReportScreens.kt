package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.NumberFormat
import java.util.*

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF1F2937),
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFFD1D5DB)
)

@Composable
fun FinancialReportTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColorScheme, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {
    FinancialReportTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Financial Report", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                    actions = { IconButton(onClick = { /* Download */ }) { Icon(Icons.Filled.Download, contentDescription = "Download") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    val items = listOf("Home", "Report", "Goals", "Wallet")
                    val icons = listOf(Icons.Default.Home, Icons.Default.Assessment, Icons.Default.Star, Icons.Default.AccountBalanceWallet)
                    var selectedItem by remember { mutableStateOf(1) }

                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item, modifier = Modifier.size(24.dp)) },
                            label = { Text(item, style = MaterialTheme.typography.labelSmall, fontWeight = if (selectedItem == index) FontWeight.Bold else FontWeight.Normal) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("My Expense Spending", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(formatCurrency(43287.06), style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+18.4%", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF22C55E), fontWeight = FontWeight.SemiBold)
                    }
                }

                item {
                    var selectedTimeRange by remember { mutableStateOf("Yearly") }
                    val timeRanges = listOf("Weekly", "Monthly", "Quarterly", "Yearly")
                    Row(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)).padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        timeRanges.forEach { range ->
                            Text(
                                range,
                                modifier = Modifier.weight(1f).clickable { selectedTimeRange = range }.background(if (selectedTimeRange == range) MaterialTheme.colorScheme.surface else Color.Transparent, RoundedCornerShape(10.dp)).padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                color = if (selectedTimeRange == range) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (selectedTimeRange == range) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(0.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(formatCurrency(43287.06), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Monthly", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            val dataPoints = listOf(0.2f, 0.4f, 0.3f, 0.6f, 0.5f, 0.8f, 0.7f, 0.9f)
                            val labels = listOf("Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb")
                            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                                LineChart(dataPoints, Modifier.fillMaxSize())
                                Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceAround) {
                                    labels.take(dataPoints.size).forEach { label ->
                                        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Income", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        IncomeCard("Salary", 13973.90, 26, Color(0xFF22C55E), Modifier.weight(1f), malawian = true)
                        Spacer(modifier = Modifier.width(10.dp))
                        IncomeCard("Business", 2964.16, 40, Color(0xFFEAB308), Modifier.weight(1f), malawian = true)
                        Spacer(modifier = Modifier.width(10.dp))
                        IncomeCard("Investments", 1240.00, 12, Color(0xFF3B82F6), Modifier.weight(1f), malawian = true)
                    }
                }

                item {
                    Text("Expense Category", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(0.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ExpenseItem("Subscription", 17630.00, 15, Color(0xFF3B82F6), malawian = true)
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            ExpenseItem("Employee Salary", 2000.00, 41, Color(0xFFEAB308), malawian = true)
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            ExpenseItem("Food", 1500.00, 20, Color(0xFFEF4444), malawian = true)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "MW"))
    format.currency = Currency.getInstance("MWK")
    return format.format(amount).replace("MWK", "MK ")
}

@Composable
fun LineChart(data: List<Float>, modifier: Modifier = Modifier, lineColor: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val path = Path()
        val fillPath = Path()
        val xStep = size.width / (data.size - 1)
        val yMax = data.maxOrNull() ?: 1f

        path.moveTo(0f, size.height * (1 - data[0] / yMax))
        fillPath.moveTo(0f, size.height)
        fillPath.lineTo(0f, size.height * (1 - data[0] / yMax))

        data.forEachIndexed { index, value ->
            val x = index * xStep
            val y = size.height * (1 - value / yMax)
            path.lineTo(x, y)
            fillPath.lineTo(x, y)
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()
        drawPath(path = fillPath, brush = Brush.verticalGradient(listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)))
        drawPath(path = path, color = lineColor, style = Stroke(width = 5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)))
    }
}

@Composable
fun IncomeCard(title: String, amount: Double, percentage: Int, color: Color, modifier: Modifier = Modifier, malawian: Boolean = false) {
    val displayAmount = if (malawian) formatCurrency(amount) else "$$amount"
    Card(modifier = modifier.height(100.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AttachMoney, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column {
                Text(displayAmount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text("$percentage%", style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ExpenseItem(title: String, amount: Double, percentage: Int, color: Color, malawian: Boolean = false) {
    val displayAmount = if (malawian) formatCurrency(amount) else "$$amount"
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(displayAmount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Text("$percentage%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7_pro")
@Composable
fun PreviewReportsScreen() {
    FinancialReportTheme {
        val navController = rememberNavController()
        ReportsScreen(navController)
    }
}
