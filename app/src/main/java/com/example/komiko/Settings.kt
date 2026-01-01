// Settings.kt
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

// --- Color Definitions ---
private val ColorTextMainLight = Color(0xFF181511)
private val ColorTextSecondaryLight = Color(0xFF897961)
private val ColorTextMainDark = Color(0xFFF4F3F0)
private val ColorTextSecondaryDark = Color(0xFFA89C8A)
private val ColorBackgroundLight = Color(0xFFF8F7F6)
private val ColorBackgroundDark = Color(0xFF221A10)
private val ColorSurfaceLight = Color(0xFFFFFFFF)
private val ColorSurfaceDark = Color(0xFF2C241B)
private val ColorPrimary = Color(0xFFEE9D2B)

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
            // Spacer for top padding (since we removed the top card)
            Spacer(modifier = Modifier.height(16.dp))

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