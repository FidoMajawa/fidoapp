package com.example.banknkhonde.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- Data class for each setting item ---
data class SettingItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String? = null // Optional route for navigation
)

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsSections = mapOf(
        "Account" to listOf(
            SettingItem("Edit Profile", "Update your personal information", Icons.Default.Person, "profile"),
            SettingItem("Security", "Change password, enable 2FA", Icons.Default.Shield, "security")
        ),
        "Preferences" to listOf(
            // ✅ Notifications now points to your real Notifications / Attendance screen
            SettingItem("Notifications", "Manage attendance & alerts", Icons.Default.Notifications, "notifications"),
            SettingItem("Appearance", "Switch between light and dark themes", Icons.Default.Palette, "appearance")
        ),
        "Support" to listOf(
            SettingItem("Help & Support", "Get help and find FAQs", Icons.AutoMirrored.Filled.HelpOutline, "help")
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // --- Header ---
        item { SettingsHeader() }

        // --- Settings Sections ---
        settingsSections.forEach { (sectionTitle, items) ->
            item { SectionHeader(title = sectionTitle) }
            items(items) { item ->
                SettingsItemRow(item = item) {
                    item.route?.let { navController.navigate(it) }
                }
            }
        }

        // --- Logout Button ---
        item {
            Spacer(modifier = Modifier.height(24.dp))
            LogoutButton(onClick = {
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun SettingsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Manage your account and app preferences.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsItemRow(item: SettingItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Logout,
            contentDescription = "Logout",
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.width(8.dp))
        Text("Logout", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
    }
}
