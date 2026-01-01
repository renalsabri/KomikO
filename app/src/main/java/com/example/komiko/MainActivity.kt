package com.example.komiko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.* // Import needed for var, by, remember
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
import androidx.compose.foundation.BorderStroke
import com.example.komiko.ui.theme.KomikOTheme

// Color Definitions
val KomikoOrange = Color(0xFFF0A500)
val KomikoLightOrange = Color(0xFFFFF4E0)
val TextDark = Color(0xFF1A1A1A)
val TextGray = Color(0xFF666666)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KomikOTheme {
                // This variable decides which screen to show
                var currentScreen by remember { mutableStateOf("dashboard") }

                if (currentScreen == "dashboard") {
                    // Pass a function to change the screen when button is clicked
                    KomikoHomeScreen(onStartTracking = { currentScreen = "library" })
                } else {
                    // Show the Empty Library screen (from HomeScreen.kt)
                    EmptyLibraryScreen()
                }
            }
        }
    }
}

// 1. Update KomikoHomeScreen to accept the click action
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
        // Pass the action down to ActionSection
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
            Text(
                text = "Kom!kO",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Track. Read. Enjoy.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun TextSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Your Manga,\nAnywhere.",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Keep track of your chapters without an internet connection. The ultimate companion for the on-the-go otaku.",
            fontSize = 16.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FeaturesSection() {
    val features = listOf(
        FeatureData("Offline First", "Read anytime", Icons.Rounded.WifiOff),
        FeatureData("Smart Sync", "Update later", Icons.Rounded.Sync),
        FeatureData("Ad-Free", "Pure reading", Icons.Rounded.Block),
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(features) { feature ->
            FeatureCard(feature)
        }
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
            Box(
                modifier = Modifier.size(40.dp).background(KomikoLightOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(feature.icon, contentDescription = null, tint = KomikoOrange)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text(feature.subtitle, fontSize = 12.sp, color = TextGray)
        }
    }
}

// 2. Update ActionSection to receive the click event
@Composable
fun ActionSection(onStartClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Button(
            // 3. Trigger the navigation here
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

        TextButton(onClick = { /* TODO: Import logic */ }) {
            Icon(Icons.Rounded.FileDownload, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import from Backup", color = TextGray, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "By continuing you agree to our Terms of Service & Privacy Policy.",
            fontSize = 10.sp, color = Color.LightGray, textAlign = TextAlign.Center
        )
    }
}

data class FeatureData(val title: String, val subtitle: String, val icon: ImageVector)