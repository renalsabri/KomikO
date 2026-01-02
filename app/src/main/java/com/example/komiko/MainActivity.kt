package com.example.komiko

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.WifiOff
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
import com.example.komiko.ui.theme.KomikOTheme
import java.io.File // ADDED for Persistence

// 1. Data Model
data class Manga(
    val title: String,
    val author: String,
    val status: String,
    val chaptersRead: String,
    val totalChapters: String,
    val rating: Int,
    val coverUri: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

// Global Colors
val KomikoOrange = Color(0xFFF0A500)
val KomikoLightOrange = Color(0xFFFFF4E0)
val TextDark = Color(0xFF1A1A1A)
val TextGray = Color(0xFF666666)

// Dark Mode Specific Colors
val DarkBackground = Color(0xFF221A10)
val DarkSurface = Color(0xFF2C241B)
val TextLightMain = Color(0xFFF4F3F0)
val TextLightSec = Color(0xFFA89C8A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Manage Theme State
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            // 2. Pass state to the Theme Wrapper
            KomikOTheme(darkTheme = isDarkTheme) {
                // Feature 5: Persistence State & Init
                val mangaList = remember { mutableStateListOf<Manga>() }
                val context = this@MainActivity

                // Load data on startup
                LaunchedEffect(Unit) {
                    val savedData = loadInternalData(context)
                    if (savedData.isNotEmpty()) {
                        mangaList.clear()
                        mangaList.addAll(savedData)
                    }
                }

                // Helper to save data
                fun saveData() {
                    saveInternalData(context, mangaList)
                }

                var currentScreen by remember { mutableStateOf("dashboard") }
                var selectedManga by remember { mutableStateOf<Manga?>(null) }

                when (currentScreen) {
                    "dashboard" -> {
                        KomikoHomeScreen(
                            isDarkTheme = isDarkTheme,
                            onStartTracking = { currentScreen = "library" }
                        )
                    }
                    "library" -> {
                        LibraryScreen(
                            isDarkTheme = isDarkTheme,
                            mangaList = mangaList,
                            onAddMangaClick = { currentScreen = "add_manga" },
                            onMangaClick = { manga ->
                                selectedManga = manga
                                currentScreen = "details"
                            },
                            onSettingsClick = { currentScreen = "settings" },
                            onIncrementClick = { manga ->
                                val index = mangaList.indexOf(manga)
                                if (index != -1) {
                                    val current = manga.chaptersRead.toIntOrNull() ?: 0
                                    mangaList[index] = manga.copy(
                                        chaptersRead = (current + 1).toString(),
                                        lastUpdated = System.currentTimeMillis()
                                    )
                                    saveData() // Save on Quick Increment
                                }
                            }
                        )
                    }
                    "add_manga" -> {
                        AddMangaManualScreen(
                            isDarkTheme = isDarkTheme,
                            onBack = { currentScreen = "library" },
                            onSave = { newManga ->
                                mangaList.add(newManga)
                                saveData() // Save on Add
                                currentScreen = "library"
                            }
                        )
                    }
                    "details" -> {
                        if (selectedManga != null) {
                            MangaDetailsScreen(
                                isDarkTheme = isDarkTheme,
                                manga = selectedManga!!,
                                onBackClick = { currentScreen = "library" },
                                onSaveClick = { updatedManga ->
                                    val index = mangaList.indexOf(selectedManga)
                                    if (index != -1) mangaList[index] = updatedManga
                                    saveData() // Save on Edit
                                    currentScreen = "library"
                                }
                            )
                        } else {
                            currentScreen = "library"
                        }
                    }
                    "settings" -> {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isDark -> isDarkTheme = isDark },
                            onBackClick = { currentScreen = "library" },
                            onBackupClick = { currentScreen = "backup_restore" },
                            onProfileClick = { currentScreen = "profile" } // Navigate to Profile
                        )
                    }
                    "profile" -> {
                        ProfileScreen(
                            isDarkTheme = isDarkTheme,
                            mangaList = mangaList,
                            onBack = { currentScreen = "settings" }
                        )
                    }
                    "backup_restore" -> {
                        BackupRestoreScreen(
                            isDarkTheme = isDarkTheme,
                            mangaList = mangaList,
                            onRestore = { restoredList ->
                                mangaList.clear()
                                mangaList.addAll(restoredList)
                                saveData() // Save on Restore
                            },
                            onBack = { currentScreen = "settings" }
                        )
                    }
                }
            }
        }
    }

    // --- Feature 5: Internal Storage Logic (CSV Based) ---
    private val INTERNAL_FILE_NAME = "komiko_internal.csv"

    private fun saveInternalData(context: Context, list: List<Manga>) {
        try {
            val file = File(context.filesDir, INTERNAL_FILE_NAME)
            file.printWriter().use { writer ->
                // Header
                writer.println("Title,Author,Status,ChaptersRead,TotalChapters,Rating,CoverUri,LastUpdated")
                list.forEach { manga ->
                    val line = buildString {
                        append(escapeCsv(manga.title)).append(",")
                        append(escapeCsv(manga.author)).append(",")
                        append(escapeCsv(manga.status)).append(",")
                        append(escapeCsv(manga.chaptersRead)).append(",")
                        append(escapeCsv(manga.totalChapters)).append(",")
                        append(manga.rating).append(",")
                        append(escapeCsv(manga.coverUri ?: "")).append(",")
                        append(manga.lastUpdated)
                    }
                    writer.println(line)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInternalData(context: Context): List<Manga> {
        val list = mutableListOf<Manga>()
        val file = File(context.filesDir, INTERNAL_FILE_NAME)
        if (!file.exists()) return list

        try {
            file.bufferedReader().use { reader ->
                var line = reader.readLine() // skip header
                while (reader.readLine().also { line = it } != null) {
                    val tokens = parseCsvLine(line!!)
                    if (tokens.size >= 7) {
                        list.add(Manga(
                            title = tokens.getOrElse(0) { "" },
                            author = tokens.getOrElse(1) { "" },
                            status = tokens.getOrElse(2) { "Reading" },
                            chaptersRead = tokens.getOrElse(3) { "0" },
                            totalChapters = tokens.getOrElse(4) { "" },
                            rating = tokens.getOrElse(5) { "0" }.toIntOrNull() ?: 0,
                            coverUri = tokens.getOrElse(6) { "" }.takeIf { it.isNotEmpty() },
                            lastUpdated = tokens.getOrElse(7) { "" }.toLongOrNull() ?: System.currentTimeMillis()
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // CSV Helpers
    private fun escapeCsv(value: String): String {
        var text = value.replace("\"", "\"\"")
        if (text.contains(",") || text.contains("\n") || text.contains("\"")) {
            text = "\"$text\""
        }
        return text
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        var inQuotes = false
        val sb = StringBuilder()
        for (char in line) {
            if (char == '\"') inQuotes = !inQuotes
            else if (char == ',' && !inQuotes) {
                tokens.add(sb.toString()); sb.clear()
            } else sb.append(char)
        }
        tokens.add(sb.toString())
        return tokens.map {
            if (it.startsWith("\"") && it.endsWith("\""))
                it.substring(1, it.length - 1).replace("\"\"", "\"")
            else it
        }
    }
}

// ... (Existing HomeScreen components remain here) ...
@Composable
fun KomikoHomeScreen(isDarkTheme: Boolean, onStartTracking: () -> Unit) {
    val backgroundColor = if (isDarkTheme) DarkBackground else Color.White
    val mainTextColor = if (isDarkTheme) TextLightMain else TextDark
    val secondaryTextColor = if (isDarkTheme) TextLightSec else TextGray
    val cardColor = if (isDarkTheme) DarkSurface else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection()
        Spacer(modifier = Modifier.height(24.dp))
        TextSection(mainTextColor, secondaryTextColor)
        Spacer(modifier = Modifier.height(32.dp))
        FeaturesSection(isDarkTheme, cardColor, mainTextColor, secondaryTextColor)
        Spacer(modifier = Modifier.height(48.dp))
        ActionSection(isDarkTheme, secondaryTextColor, onStartClick = onStartTracking)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFA726), Color(0xFFF57C00))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Kom!kO", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Track. Read. Enjoy.", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun TextSection(mainColor: Color, subColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Your Manga,\nAnywhere.", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = mainColor, textAlign = TextAlign.Center, lineHeight = 36.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Keep track of your chapters without an internet connection. The ultimate companion for the on-the-go otaku.", fontSize = 16.sp, color = subColor, textAlign = TextAlign.Center)
    }
}

@Composable
fun FeaturesSection(isDarkTheme: Boolean, cardColor: Color, mainColor: Color, subColor: Color) {
    val features = listOf(
        FeatureData("Offline First", "Read anytime", Icons.Rounded.WifiOff),
        FeatureData("Smart Sync", "Update later", Icons.Rounded.Sync),
        FeatureData("Ad-Free", "Pure reading", Icons.Rounded.Block),
    )
    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(features) { feature -> FeatureCard(feature, isDarkTheme, cardColor, mainColor, subColor) }
    }
}

@Composable
fun FeatureCard(feature: FeatureData, isDarkTheme: Boolean, cardColor: Color, mainColor: Color, subColor: Color) {
    val borderColor = if(isDarkTheme) Color.White.copy(alpha=0.1f) else Color(0xFFEEEEEE)
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.width(140.dp).height(140.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(modifier = Modifier.size(40.dp).background(KomikoLightOrange, CircleShape), contentAlignment = Alignment.Center) {
                Icon(feature.icon, contentDescription = null, tint = KomikoOrange)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = mainColor)
            Text(feature.subtitle, fontSize = 12.sp, color = subColor)
        }
    }
}

@Composable
fun ActionSection(isDarkTheme: Boolean, subColor: Color, onStartClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 24.dp)) {
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(containerColor = KomikoOrange),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Start Tracking", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.ArrowForward, contentDescription = null, tint = TextDark)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { }) {
            Icon(Icons.Rounded.FileDownload, contentDescription = null, tint = subColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import from Backup", color = subColor, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("By continuing you agree to our Terms of Service & Privacy Policy.", fontSize = 10.sp, color = subColor.copy(alpha=0.6f), textAlign = TextAlign.Center)
    }
}

data class FeatureData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)