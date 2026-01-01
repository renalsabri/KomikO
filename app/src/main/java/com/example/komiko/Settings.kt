package com.example.komiko

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val ColorTextMainLight = Color(0xFF181511)
private val ColorTextSecondaryLight = Color(0xFF897961)
private val ColorTextMainDark = Color(0xFFF4F3F0)
private val ColorTextSecondaryDark = Color(0xFFA89C8A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showThemeDialog by remember { mutableStateOf(false) }

    // Resolve current colors based on theme state
    val backgroundColor = if (isDarkTheme) ColorBackgroundDark else ColorBackgroundLight
    val surfaceColor = if (isDarkTheme) ColorSurfaceDark else ColorSurfaceLight
    val mainTextColor = if (isDarkTheme) ColorTextMainDark else ColorTextMainLight
    val secondaryTextColor = if (isDarkTheme) ColorTextSecondaryDark else ColorTextSecondaryLight

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = surfaceColor,
            title = { Text("Choose Theme", color = mainTextColor) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = !isDarkTheme,
                                onClick = { onThemeChange(false); showThemeDialog = false },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isDarkTheme,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = ColorPrimary, unselectedColor = secondaryTextColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Light Mode", color = mainTextColor)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isDarkTheme,
                                onClick = { onThemeChange(true); showThemeDialog = false },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isDarkTheme,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = ColorPrimary, unselectedColor = secondaryTextColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dark Mode", color = mainTextColor)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel", color = ColorPrimary)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = mainTextColor
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = mainTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
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
            CloudSyncCard(surfaceColor, mainTextColor, secondaryTextColor)

            Spacer(modifier = Modifier.height(8.dp))

            // --- Section: General ---
            SettingsSectionHeader(title = "General", textColor = secondaryTextColor)
            SettingsGroup(backgroundColor = surfaceColor) {
                SettingsTile(
                    icon = Icons.Default.Palette,
                    title = "Appearance",
                    textColor = mainTextColor,
                    iconBgColor = backgroundColor,
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isDarkTheme) "Dark Mode" else "Light Mode",
                                style = MaterialTheme.typography.bodyMedium,
                                color = secondaryTextColor
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = secondaryTextColor
                            )
                        }
                    },
                    onClick = { showThemeDialog = true }
                )
            }

            // --- Section: Data & Storage ---
            SettingsSectionHeader(title = "Data & Storage", textColor = secondaryTextColor)
            SettingsGroup(backgroundColor = surfaceColor) {
                SettingsTile(
                    icon = Icons.Default.Backup,
                    title = "Backup & Restore",
                    textColor = mainTextColor,
                    iconBgColor = backgroundColor,
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = secondaryTextColor
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
                color = secondaryTextColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Build 2023.10.24",
                style = MaterialTheme.typography.labelSmall,
                color = secondaryTextColor.copy(alpha = 0.6f)
            )
        }
    }
}

// --- Components ---

@Composable
fun CloudSyncCard(surfaceColor: Color, mainTextColor: Color, secondaryTextColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
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
                    color = mainTextColor
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
                    color = secondaryTextColor,
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
fun SettingsSectionHeader(title: String, textColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.05.sp.value.sp
        )
    }
}

@Composable
fun SettingsGroup(backgroundColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        content = content
    )
}

@Composable
fun SettingsTile(
    icon: ImageVector,
    title: String,
    textColor: Color,
    iconBgColor: Color,
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
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Trailing
        trailingContent()
    }
}