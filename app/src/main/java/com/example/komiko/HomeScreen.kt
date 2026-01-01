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
    mangaList: List<Manga>,
    onAddMangaClick: () -> Unit,
    onMangaClick: (Manga) -> Unit // ADDED: Click handler for items
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kom!kO", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = KomikoOrange) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                actions = {
                    if (mangaList.isNotEmpty()) {
                        IconButton(onClick = onAddMangaClick) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add", tint = KomikoOrange)
                        }
                    }
                }
            )
        },
        containerColor = Color.White,
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
                Text("Library is Empty", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(16.dp))
                Text("It looks like you haven't added any manga yet. Start building your offline collection now!", fontSize = 16.sp, color = TextGray, textAlign = TextAlign.Center, lineHeight = 24.sp)
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
                        onClick = { onMangaClick(manga) } // Pass the click event
                    )
                }
            }
        }
    }
}

@Composable
fun MangaListItem(manga: Manga, onClick: () -> Unit) { // ADDED: onClick parameter
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // ADDED: Make Card Clickable
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp, 80.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = manga.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                Text(text = manga.author, fontSize = 14.sp, color = TextGray)
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