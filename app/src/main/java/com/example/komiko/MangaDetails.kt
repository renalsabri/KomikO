// MangaDetails.kt
package com.example.komiko

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Color Definitions ---
val ColorPrimary = Color(0xFFEE9D2B)
val ColorBackgroundLight = Color(0xFFF8F7F6)
val ColorBackgroundDark = Color(0xFF221A10)
val ColorSurfaceLight = Color(0xFFFFFFFF)
val ColorSurfaceDark = Color(0xFF2D2418)
val ColorBorderLight = Color(0xFFE6E1DB)
val ColorBorderDark = Color(0xFF3A2E22)
val ColorTextLight = Color(0xFF181511)
val ColorTextDark = Color(0xFFF3F4F6)
val ColorPlaceholder = Color(0xFF897961)

@Composable
fun MangaDetailsScreen(
    manga: Manga, // Now accepts the manga object to display
    onBackClick: () -> Unit,
    onSaveClick: (Manga) -> Unit // Returns the modified manga
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) ColorBackgroundDark else ColorBackgroundLight
    val surfaceColor = if (isDark) ColorSurfaceDark else ColorSurfaceLight
    val textColor = if (isDark) ColorTextDark else ColorTextLight
    val borderColor = if (isDark) ColorBorderDark else ColorBorderLight

    // Initialize State with existing Manga data
    var title by remember { mutableStateOf(manga.title) }
    var author by remember { mutableStateOf(manga.author) }
    var selectedStatus by remember { mutableStateOf(manga.status) }

    // Safety check for integer conversion
    var chaptersRead by remember { mutableIntStateOf(manga.chaptersRead.toIntOrNull() ?: 0) }
    var totalChapters by remember { mutableStateOf(manga.totalChapters) }
    var rating by remember { mutableIntStateOf(manga.rating) }

    val statusOptions = listOf("Reading", "Completed", "On Hold", "Dropped", "Plan to Read")

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopBar(
                title = "Manga Details", // Changed title
                textColor = textColor,
                onBackClick = onBackClick,
                onSaveClick = {
                    // Return updated object
                    onSaveClick(
                        Manga(
                            title = title,
                            author = author,
                            status = selectedStatus,
                            chaptersRead = chaptersRead.toString(),
                            totalChapters = totalChapters,
                            rating = rating
                        )
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Cover Image Upload (Placeholder)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DashedImagePlaceholder(
                    isDark = isDark,
                    onClick = { /* Handle Image Picker */ }
                )
            }

            // 2. Title & Author
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MangaInputField(
                    label = "Title",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "e.g., One Piece",
                    backgroundColor = surfaceColor,
                    borderColor = borderColor,
                    textColor = textColor
                )
                MangaInputField(
                    label = "Author / Website",
                    value = author,
                    onValueChange = { author = it },
                    placeholder = "e.g., Eiichiro Oda",
                    backgroundColor = surfaceColor,
                    borderColor = borderColor,
                    textColor = textColor
                )
            }

            // 3. Status Selector
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Status",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                FlowRowLikeLayout(spacing = 8.dp) {
                    statusOptions.forEach { status ->
                        StatusChip(
                            text = status,
                            isSelected = status == selectedStatus,
                            onSelect = { selectedStatus = status },
                            surfaceColor = surfaceColor,
                            borderColor = borderColor,
                            textColor = textColor
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Progress",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chapters Read",
                            color = ColorPlaceholder,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        Row(
                            modifier = Modifier
                                .height(56.dp)
                                .shadow(1.dp, RoundedCornerShape(8.dp))
                                .background(surfaceColor, RoundedCornerShape(8.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (chaptersRead > 0) chaptersRead-- }) {
                                Icon(Icons.Default.Remove, null, tint = ColorPlaceholder)
                            }
                            Text(
                                text = chaptersRead.toString(),
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = textColor,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                            IconButton(onClick = { chaptersRead++ }) {
                                Icon(Icons.Default.Add, null, tint = ColorPlaceholder)
                            }
                        }
                    }

                    Text(
                        text = "/",
                        fontSize = 24.sp,
                        color = ColorPlaceholder,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(top = 24.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Total Chapters",
                            color = ColorPlaceholder,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .shadow(1.dp, RoundedCornerShape(8.dp))
                                .background(surfaceColor, RoundedCornerShape(8.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                value = totalChapters,
                                onValueChange = { if (it.all { char -> char.isDigit() }) totalChapters = it },
                                textStyle = LocalTextStyle.current.copy(
                                    color = textColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                decorationBox = { innerTextField ->
                                    if (totalChapters.isEmpty()) {
                                        Text(text = "?", color = ColorPlaceholder, fontSize = 18.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }
            }

            // 5. Personal Rating
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Personal Rating",
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Rate $i stars",
                            tint = if (i <= rating) ColorPrimary else if (isDark) Color.Gray else Color.LightGray,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = i }
                        )
                    }
                }
            }
        }
    }
}

// --- Components (Helper functions reused) ---

@Composable
fun TopBar(
    title: String,
    textColor: Color,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp).clip(CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            color = textColor
        )

        TextButton(
            onClick = onSaveClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.textButtonColors(contentColor = ColorPrimary)
        ) {
            Text("Save", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun MangaInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 15.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) Text(text = placeholder, color = ColorPlaceholder)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DashedImagePlaceholder(isDark: Boolean, onClick: () -> Unit) {
    val borderColor = ColorPrimary.copy(alpha = 0.4f)
    val contentColor = if (isDark) Color.Gray else Color(0xFF897961)

    Box(
        modifier = Modifier
            .width(160.dp)
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) ColorSurfaceDark else ColorSurfaceLight)
            .clickable { onClick() }
            .drawDashedBorder(2.dp, borderColor, 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.AddAPhoto, "Add Cover", tint = contentColor, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tap to add cover", color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StatusChip(text: String, isSelected: Boolean, onSelect: () -> Unit, surfaceColor: Color, borderColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) ColorPrimary else surfaceColor)
            .border(if (isSelected) 0.dp else 1.dp, if (isSelected) Color.Transparent else borderColor, RoundedCornerShape(50))
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = if (isSelected) Color.White else textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowLikeLayout(spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing), verticalArrangement = Arrangement.spacedBy(spacing)) { content() }
}

fun Modifier.drawDashedBorder(strokeWidth: androidx.compose.ui.unit.Dp, color: Color, cornerRadius: androidx.compose.ui.unit.Dp): Modifier = this.drawBehind {
    val stroke = Stroke(width = strokeWidth.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))
    drawRoundRect(color = color, style = stroke, cornerRadius = CornerRadius(cornerRadius.toPx()))
}