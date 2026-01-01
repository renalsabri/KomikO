// HomeScreen.kt
package com.example.komiko

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val KomikoLightOrangeBg = Color(0xFFFFF8E1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    isDarkTheme: Boolean, // Added Parameter
    mangaList: List<Manga>,
    onAddMangaClick: () -> Unit,
    onMangaClick: (Manga) -> Unit,
    onSettingsClick: () -> Unit
) {
    // Resolve Colors
    val backgroundColor = if (isDarkTheme) Color(0xFF221A10) else Color.White
    val surfaceColor = if (isDarkTheme) Color(0xFF2C241B) else Color.White
    val mainTextColor = if (isDarkTheme) Color(0xFFF4F3F0) else Color(0xFF1A1A1A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFFA89C8A) else Color(0xFF666666)
    val borderColor = if (isDarkTheme) Color.White.copy(alpha=0.1f) else Color(0xFFEEEEEE)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kom!kO", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = KomikoOrange) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = surfaceColor),
                navigationIcon = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = mainTextColor)
                    }
                },
                actions = {
                    if (mangaList.isNotEmpty()) {
                        IconButton(onClick = onAddMangaClick) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add", tint = KomikoOrange)
                        }
                    }
                }
            )
        },
        containerColor = backgroundColor,
        floatingActionButton = {
            if (mangaList.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onAddMangaClick,
                    containerColor = KomikoOrange,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Manga")
                }
            }
        }
    ) { innerPadding ->
        if (mangaList.isEmpty()) {
            // EMPTY STATE
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(160.dp).background(KomikoLightOrangeBg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.LibraryBooks, null, tint = KomikoOrange, modifier = Modifier.size(80.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Library is Empty", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = mainTextColor)
                Spacer(modifier = Modifier.height(16.dp))
                Text("It looks like you haven't added any manga yet. Start building your offline collection now!", fontSize = 16.sp, color = secondaryTextColor, textAlign = TextAlign.Center, lineHeight = 24.sp)
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = onAddMangaClick,
                    colors = ButtonDefaults.buttonColors(containerColor = KomikoOrange, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp).fillMaxWidth(0.8f)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text("Add your first manga", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mangaList) { manga ->
                    MangaListItem(
                        manga = manga,
                        cardColor = surfaceColor,
                        borderColor = borderColor,
                        mainTextColor = mainTextColor,
                        secondaryTextColor = secondaryTextColor,
                        onClick = { onMangaClick(manga) }
                    )
                }
            }
        }
    }
}

@Composable
fun MangaListItem(
    manga: Manga,
    cardColor: Color,
    borderColor: Color,
    mainTextColor: Color,
    secondaryTextColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp, 80.dp)
                    .background(Color.LightGray.copy(alpha=0.3f), RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = manga.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = mainTextColor)
                Text(text = manga.author, fontSize = 14.sp, color = secondaryTextColor)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${manga.chaptersRead}/${if(manga.totalChapters.isEmpty()) "?" else manga.totalChapters} Ch",
                        fontSize = 12.sp,
                        color = KomikoOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = KomikoLightOrangeBg,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = manga.status,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = KomikoOrange
                        )
                    }
                }
            }
        }
    }
}