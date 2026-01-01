package com.example.komiko

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val HtmlPrimary = Color(0xFFEE9D2B)
val HtmlBackground = Color(0xFFF8F7F6)
val HtmlSurface = Color(0xFFFFFFFF)
val HtmlBorder = Color(0xFFE6E1DB)
val HtmlTextMain = Color(0xFF181511)
val HtmlTextSec = Color(0xFF897961)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddMangaManualScreen(onBack: () -> Unit, onSave: (Manga) -> Unit) {
    var title by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Reading") }
    var chaptersRead by remember { mutableStateOf("0") }
    var totalChapters by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var description by remember { mutableStateOf("") }

    val statusOptions = listOf("Reading", "Completed", "On Hold", "Dropped", "Plan to Read")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add New Manga", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HtmlTextMain)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = HtmlTextMain)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (title.isNotEmpty()) {
                            // We pass 'website' into the author field so it shows up in the list
                            onSave(Manga(title, website, status, chaptersRead, totalChapters, rating))
                        }
                    }) {
                        Text("Save", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HtmlPrimary))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = HtmlBackground.copy(alpha = 0.95f))
            )
        },
        containerColor = HtmlBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Cover Image Placeholder
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .width(160.dp)
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(HtmlSurface)
                        .border(2.dp, HtmlPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.AddAPhoto, null, tint = HtmlTextSec, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Tap to add cover", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = HtmlTextSec))
                }
            }

            // Inputs
            KomikoInput(label = "Title", value = title, onValueChange = { title = it }, placeholder = "e.g., One Piece")

            // CHANGED: Label to "Reading website" and placeholder to the example URL
            KomikoInput(
                label = "Reading website",
                value = website,
                onValueChange = { website = it },
                placeholder = "ex : www.comicnice.com"
            )

            // Status
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Status", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HtmlTextMain))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { option ->
                        val isSelected = status == option
                        Surface(
                            onClick = { status = option },
                            shape = CircleShape,
                            color = if (isSelected) HtmlPrimary else HtmlSurface,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, HtmlBorder),
                            shadowElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Text(option, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else HtmlTextMain))
                        }
                    }
                }
            }

            // Progress
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Progress", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HtmlTextMain))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chapters Read", color = HtmlTextSec, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.height(56.dp).clip(RoundedCornerShape(8.dp)).background(HtmlSurface).border(1.dp, HtmlBorder, RoundedCornerShape(8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { val c = chaptersRead.toIntOrNull() ?: 0; if (c > 0) chaptersRead = (c - 1).toString() }) { Icon(Icons.Rounded.Remove, null, tint = HtmlTextSec) }
                            BasicTextField(value = chaptersRead, onValueChange = { if (it.all { c -> c.isDigit() }) chaptersRead = it }, textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = HtmlTextMain, textAlign = TextAlign.Center), modifier = Modifier.weight(1f))
                            IconButton(onClick = { val c = chaptersRead.toIntOrNull() ?: 0; chaptersRead = (c + 1).toString() }) { Icon(Icons.Rounded.Add, null, tint = HtmlTextSec) }
                        }
                    }
                    Text("/", fontSize = 24.sp, color = HtmlTextSec, fontWeight = FontWeight.Light, modifier = Modifier.padding(top = 24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Chapters", color = HtmlTextSec, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.height(56.dp).clip(RoundedCornerShape(8.dp)).background(HtmlSurface).border(1.dp, HtmlBorder, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            BasicTextField(value = totalChapters, onValueChange = { if (it.all { c -> c.isDigit() }) totalChapters = it }, textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = HtmlTextMain, textAlign = TextAlign.Center), modifier = Modifier.fillMaxWidth())
                            if (totalChapters.isEmpty()) Text("?", color = Color.Gray, fontSize = 18.sp)
                        }
                    }
                }
            }

            // Rating
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Personal Rating", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HtmlTextMain))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        val isFilled = i <= rating
                        Icon(if (isFilled) Icons.Filled.Star else Icons.Outlined.StarBorder, null, tint = if (isFilled) HtmlPrimary else Color.Gray, modifier = Modifier.size(36.dp).clickable { rating = i })
                    }
                }
            }
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
fun KomikoInput(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = HtmlTextMain, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = HtmlTextSec) }, singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = HtmlSurface, unfocusedContainerColor = HtmlSurface, focusedBorderColor = HtmlPrimary, unfocusedBorderColor = HtmlBorder, cursorColor = HtmlPrimary),
            shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}