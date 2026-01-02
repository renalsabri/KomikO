// Profile.kt
package com.example.komiko

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

private val ProfileBgDark = Color(0xFF221A10)
private val ProfileBgLight = Color(0xFFF8F7F6)
private val ProfileSurfaceDark = Color(0xFF2C241B)
private val ProfileSurfaceLight = Color(0xFFFFFFFF)
private val ProfileTextDark = Color(0xFFF4F3F0)
private val ProfileTextLight = Color(0xFF181511)
private val AccentColor = Color(0xFFEE9D2B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    mangaList: List<Manga>,
    onBack: () -> Unit
) {
    val bgColor = if (isDarkTheme) ProfileBgDark else ProfileBgLight
    val surfaceColor = if (isDarkTheme) ProfileSurfaceDark else ProfileSurfaceLight
    val textColor = if (isDarkTheme) ProfileTextDark else ProfileTextLight

    // --- Statistics Calculations ---
    val totalManga = mangaList.size
    val totalChaptersRead = mangaList.sumOf { it.chaptersRead.toIntOrNull() ?: 0 }
    val completedManga = mangaList.count { it.status.equals("Completed", ignoreCase = true) }

    val ratedManga = mangaList.filter { it.rating > 0 }
    val averageRating = if (ratedManga.isNotEmpty()) {
        ratedManga.map { it.rating }.average()
    } else 0.0
    val df = DecimalFormat("#.##")

    // --- User State (Mocked for now, could be persisted in Prefs) ---
    var userName by remember { mutableStateOf("Otaku Reader") }
    var showNameDialog by remember { mutableStateOf(false) }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Edit Name") },
            text = {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = { showNameDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AccentColor)) {
                    Text("Save", color = Color.White)
                }
            },
            containerColor = surfaceColor,
            titleContentColor = textColor,
            textContentColor = textColor
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile & Stats", fontWeight = FontWeight.Bold, color = textColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. User Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(AccentColor, Color(0xFFFFCC80)))
                            )
                            .border(4.dp, surfaceColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        IconButton(onClick = { showNameDialog = true }) {
                            Icon(Icons.Rounded.Edit, null, tint = AccentColor, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(
                        text = "Member since 2026",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )
                }
            }

            // 2. Statistics Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(surfaceColor, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = "Reading Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Manga",
                        value = totalManga.toString(),
                        icon = Icons.Rounded.MenuBook,
                        color = Color(0xFF64B5F6),
                        textColor = textColor
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Chapters",
                        value = totalChaptersRead.toString(),
                        icon = Icons.Rounded.AutoStories,
                        color = Color(0xFF81C784),
                        textColor = textColor
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Completed",
                        value = completedManga.toString(),
                        icon = Icons.Rounded.CheckCircle,
                        color = Color(0xFFBA68C8),
                        textColor = textColor
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Avg Rating",
                        value = df.format(averageRating),
                        icon = Icons.Rounded.Star,
                        color = Color(0xFFFFB74D),
                        textColor = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Level/Rank Badge (Gamification)
            val rank = when {
                totalChaptersRead > 1000 -> "Manga God"
                totalChaptersRead > 500 -> "Otaku King"
                totalChaptersRead > 100 -> "Avid Reader"
                totalChaptersRead > 10 -> "Beginner"
                else -> "Newbie"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFF0A500), Color(0xFFCF8500))))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Current Rank", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(rank, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Icon(Icons.Rounded.Star, null, tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    textColor: Color
) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(title, fontSize = 12.sp, color = textColor.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}