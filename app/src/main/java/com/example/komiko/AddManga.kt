package com.example.komiko

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val ColorPrimary = Color(0xFFEE9D2B)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddMangaManualScreen(
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onSave: (Manga) -> Unit
) {
    val bgColor = if (isDarkTheme) Color(0xFF221A10) else Color(0xFFF8F7F6)
    val surfaceColor = if (isDarkTheme) Color(0xFF2C241B) else Color(0xFFFFFFFF)
    val textMain = if (isDarkTheme) Color(0xFFF4F3F0) else Color(0xFF181511)
    val textSec = if (isDarkTheme) Color(0xFFA89C8A) else Color(0xFF897961)
    val borderColor = if (isDarkTheme) Color(0xFF3A2E22) else Color(0xFFE6E1DB)

    var title by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Reading") }
    var chaptersRead by remember { mutableStateOf("0") }
    var totalChapters by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }

    // IMAGE PICKER STATE
    var coverUri by remember { mutableStateOf<String?>(null) }

    // Launcher for Photo Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        coverUri = uri?.toString()
    }

    val statusOptions = listOf("Reading", "Completed", "On Hold", "Dropped", "Plan to Read")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add New Manga", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textMain)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = textMain)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (title.isNotEmpty()) {
                            // Save with Cover URI
                            onSave(Manga(title, website, status, chaptersRead, totalChapters, rating, coverUri))
                        }
                    }) {
                        Text("Save", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ColorPrimary))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = bgColor.copy(alpha = 0.95f))
            )
        },
        containerColor = bgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Cover Image Upload Area
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(surfaceColor)
                        .border(2.dp, ColorPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable {
                            // Launch Picker
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (coverUri != null) {
                        // Display selected image
                        AsyncImage(
                            model = coverUri,
                            contentDescription = "Cover Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Placeholder
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.AddAPhoto, null, tint = textSec, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Tap to add cover", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textSec))
                        }
                    }
                }
            }

            // Inputs
            KomikoInput(label = "Title", value = title, onValueChange = { title = it }, placeholder = "e.g., One Piece", textMain, textSec, surfaceColor, borderColor)
            KomikoInput(label = "Reading website", value = website, onValueChange = { website = it }, placeholder = "ex : www.comicnice.com", textMain, textSec, surfaceColor, borderColor)

            // Status
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Status", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textMain))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { option ->
                        val isSelected = status == option
                        Surface(
                            onClick = { status = option },
                            shape = CircleShape,
                            color = if (isSelected) ColorPrimary else surfaceColor,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            shadowElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Text(option, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else textMain))
                        }
                    }
                }
            }

            // Progress
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Progress", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textMain))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chapters Read", color = textSec, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.height(56.dp).clip(RoundedCornerShape(8.dp)).background(surfaceColor).border(1.dp, borderColor, RoundedCornerShape(8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { val c = chaptersRead.toIntOrNull() ?: 0; if (c > 0) chaptersRead = (c - 1).toString() }) { Icon(Icons.Rounded.Remove, null, tint = textSec) }
                            BasicTextField(value = chaptersRead, onValueChange = { if (it.all { c -> c.isDigit() }) chaptersRead = it }, textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textMain, textAlign = TextAlign.Center), modifier = Modifier.weight(1f))
                            IconButton(onClick = { val c = chaptersRead.toIntOrNull() ?: 0; chaptersRead = (c + 1).toString() }) { Icon(Icons.Rounded.Add, null, tint = textSec) }
                        }
                    }
                    Text("/", fontSize = 24.sp, color = textSec, fontWeight = FontWeight.Light, modifier = Modifier.padding(top = 24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Chapters", color = textSec, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.height(56.dp).clip(RoundedCornerShape(8.dp)).background(surfaceColor).border(1.dp, borderColor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            BasicTextField(value = totalChapters, onValueChange = { if (it.all { c -> c.isDigit() }) totalChapters = it }, textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textMain, textAlign = TextAlign.Center), modifier = Modifier.fillMaxWidth())
                            if (totalChapters.isEmpty()) Text("?", color = Color.Gray, fontSize = 18.sp)
                        }
                    }
                }
            }

            // Rating
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Personal Rating", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textMain))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        val isFilled = i <= rating
                        Icon(if (isFilled) Icons.Filled.Star else Icons.Outlined.StarBorder, null, tint = if (isFilled) ColorPrimary else Color.Gray, modifier = Modifier.size(36.dp).clickable { rating = i })
                    }
                }
            }
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
fun KomikoInput(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, textMain: Color, textSec: Color, surface: Color, border: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = textMain, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = textSec) }, singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = surface, unfocusedContainerColor = surface,
                focusedBorderColor = ColorPrimary, unfocusedBorderColor = border,
                cursorColor = ColorPrimary,
                focusedTextColor = textMain, unfocusedTextColor = textMain
            ),
            shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}