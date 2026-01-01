// MainActivity.kt
package com.example.komiko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

// 1. Define the Data Model
data class Manga(
    val title: String,
    val author: String,
    val status: String,
    val chaptersRead: String,
    val totalChapters: String,
    val rating: Int
)

// Global Colors
val KomikoOrange = Color(0xFFF0A500)
val KomikoLightOrange = Color(0xFFFFF4E0)
val TextDark = Color(0xFF1A1A1A)
val TextGray = Color(0xFF666666)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KomikOTheme {
                val mangaList = remember { mutableStateListOf<Manga>() }
                var currentScreen by remember { mutableStateOf("dashboard") }

                // New state to hold the manga currently being viewed/edited
                var selectedManga by remember { mutableStateOf<Manga?>(null) }

                when (currentScreen) {
                    "dashboard" -> {
                        KomikoHomeScreen(onStartTracking = { currentScreen = "library" })
                    }
                    "library" -> {
                        LibraryScreen(
                            mangaList = mangaList,
                            onAddMangaClick = { currentScreen = "add_manga" },
                            onMangaClick = { manga ->
                                // Set the selected manga and navigate to details
                                selectedManga = manga
                                currentScreen = "details"
                            }
                        )
                    }
                    "add_manga" -> {
                        AddMangaManualScreen(
                            onBack = { currentScreen = "library" },
                            onSave = { newManga ->
                                mangaList.add(newManga)
                                currentScreen = "library"
                            }
                        )
                    }
                    "details" -> {
                        if (selectedManga != null) {
                            MangaDetailsScreen(
                                manga = selectedManga!!,
                                onBackClick = { currentScreen = "library" },
                                onSaveClick = { updatedManga ->
                                    // Update the list with the new details
                                    val index = mangaList.indexOf(selectedManga)
                                    if (index != -1) {
                                        mangaList[index] = updatedManga
                                    }
                                    currentScreen = "library"
                                }
                            )
                        } else {
                            // Fallback if something goes wrong
                            currentScreen = "library"
                        }
                    }
                }
            }
        }
    }
}

// ... Rest of the MainActivity.kt (KomikoHomeScreen, HeaderSection, etc.) remains the same ...
// You can keep the existing @Composable functions below this line as they were in your original file.
@Composable
fun KomikoHomeScreen(onStartTracking: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection()
        Spacer(modifier = Modifier.height(24.dp))
        TextSection()
        Spacer(modifier = Modifier.height(32.dp))
        FeaturesSection()
        Spacer(modifier = Modifier.height(48.dp))
        ActionSection(onStartClick = onStartTracking)
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
fun TextSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Your Manga,\nAnywhere.", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextDark, textAlign = TextAlign.Center, lineHeight = 36.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Keep track of your chapters without an internet connection. The ultimate companion for the on-the-go otaku.", fontSize = 16.sp, color = TextGray, textAlign = TextAlign.Center)
    }
}

@Composable
fun FeaturesSection() {
    val features = listOf(
        FeatureData("Offline First", "Read anytime", Icons.Rounded.WifiOff),
        FeatureData("Smart Sync", "Update later", Icons.Rounded.Sync),
        FeatureData("Ad-Free", "Pure reading", Icons.Rounded.Block),
    )
    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(features) { feature -> FeatureCard(feature) }
    }
}

@Composable
fun FeatureCard(feature: FeatureData) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
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
            Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text(feature.subtitle, fontSize = 12.sp, color = TextGray)
        }
    }
}

@Composable
fun ActionSection(onStartClick: () -> Unit) {
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
            Icon(Icons.Rounded.FileDownload, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import from Backup", color = TextGray, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("By continuing you agree to our Terms of Service & Privacy Policy.", fontSize = 10.sp, color = Color.LightGray, textAlign = TextAlign.Center)
    }
}

data class FeatureData(val title: String, val subtitle: String, val icon: ImageVector)