// Settings.kt
package com.example.komiko

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val ColorTextMain = Color(0xFF181511)
private val ColorTextSecondary = Color(0xFF897961)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ColorTextMain
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ColorTextMain
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorBackgroundLight
                )
            )
        },
        containerColor = ColorBackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Profile / Cloud Sync Section ---
            CloudSyncCard()

            Spacer(modifier = Modifier.height(8.dp))

            // --- Section: General ---
            SettingsSectionHeader(title = "General")
            SettingsGroup {
                SettingsTile(
                    icon = Icons.Default.Palette,
                    title = "Appearance",
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "System",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ColorTextSecondary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = ColorTextSecondary
                            )
                        }
                    },
                    onClick = { /* Handle Click */ }
                )
            }

            // REMOVED: Reader Section (Keep Screen On)

            // --- Section: Data & Storage ---
            SettingsSectionHeader(title = "Data & Storage")
            SettingsGroup {
                SettingsTile(
                    icon = Icons.Default.Backup,
                    title = "Backup & Restore",
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = ColorTextSecondary
                        )
                    },
                    onClick = { /* Handle Click */ }
                )
            }

            // --- Footer ---
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Manga Reader v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Build 2023.10.24",
                style = MaterialTheme.typography.labelSmall,
                color = ColorTextSecondary.copy(alpha = 0.6f)
            )
        }
    }
}

// --- Components ---

@Composable
fun CloudSyncCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar with border
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f)) // Placeholder bg
            ) {
                // In a real app, use AsyncImage here
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorPrimary.copy(alpha = 0.2f))
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cloud Sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextMain
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ColorPrimary,
                        trackColor = Color.Gray.copy(alpha = 0.2f),
                    )
                    Text(
                        text = "45%",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "1.2GB of 5GB used",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Sync Button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ColorPrimary.copy(alpha = 0.1f))
                    .clickable { /* Sync Action */ }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync",
                    tint = ColorPrimary
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.05.sp.value.sp // Approximate tracking
        )
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ColorSurfaceLight),
        content = content
    )
}

@Composable
fun SettingsTile(
    icon: ImageVector,
    title: String,
    trailingContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ColorBackgroundLight), // Light grey bg for icon
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ColorTextMain,
                modifier = Modifier.size(20.dp)
            )
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorTextMain,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Trailing (Arrow, Switch, or Text)
        trailingContent()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MaterialTheme {
        SettingsScreen()
    }
}