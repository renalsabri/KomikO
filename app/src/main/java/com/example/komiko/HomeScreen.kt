// HomeScreen.kt
package com.example.komiko

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val KomikoLightOrangeBg = Color(0xFFFFF8E1)

enum class SortOption {
    ALPHABETICAL,
    LAST_UPDATED,
    RATING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    isDarkTheme: Boolean,
    mangaList: List<Manga>,
    onAddMangaClick: () -> Unit,
    onMangaClick: (Manga) -> Unit,
    onSettingsClick: () -> Unit,
    onIncrementClick: (Manga) -> Unit // ADDED
) {
    // --- State ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var sortOption by remember { mutableStateOf(SortOption.LAST_UPDATED) }
    var showSortMenu by remember { mutableStateOf(false) }

    // --- Filter & Sort Logic ---
    val filteredManga = remember(mangaList, searchQuery, selectedFilter, sortOption) {
        val filtered = mangaList.filter { manga ->
            val matchesSearch = manga.title.contains(searchQuery, ignoreCase = true) ||
                    manga.author.contains(searchQuery, ignoreCase = true)
            val matchesStatus = if (selectedFilter == "All") true else manga.status.equals(selectedFilter, ignoreCase = true)
            matchesSearch && matchesStatus
        }

        when (sortOption) {
            SortOption.ALPHABETICAL -> filtered.sortedBy { it.title }
            SortOption.LAST_UPDATED -> filtered.sortedByDescending { it.lastUpdated }
            SortOption.RATING -> filtered.sortedByDescending { it.rating }
        }
    }

    // Resolve Colors
    val backgroundColor = if (isDarkTheme) Color(0xFF221A10) else Color.White
    val surfaceColor = if (isDarkTheme) Color(0xFF2C241B) else Color.White
    val mainTextColor = if (isDarkTheme) Color(0xFFF4F3F0) else Color(0xFF1A1A1A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFFA89C8A) else Color(0xFF666666)
    val borderColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFEEEEEE)

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
                    // Sort Button
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort", tint = mainTextColor)
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            containerColor = surfaceColor
                        ) {
                            DropdownMenuItem(
                                text = { Text("Last Updated", color = mainTextColor) },
                                onClick = { sortOption = SortOption.LAST_UPDATED; showSortMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Alphabetical (A-Z)", color = mainTextColor) },
                                onClick = { sortOption = SortOption.ALPHABETICAL; showSortMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Rating (High-Low)", color = mainTextColor) },
                                onClick = { sortOption = SortOption.RATING; showSortMenu = false }
                            )
                        }
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (mangaList.isEmpty()) {
                EmptyLibraryState(onAddMangaClick, mainTextColor, secondaryTextColor)
            } else {
                KomikoSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    isDarkTheme = isDarkTheme,
                    mainTextColor = mainTextColor,
                    secondaryTextColor = secondaryTextColor,
                    surfaceColor = surfaceColor,
                    borderColor = borderColor
                )

                StatusFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    isDarkTheme = isDarkTheme,
                    mainTextColor = mainTextColor,
                    surfaceColor = surfaceColor,
                    borderColor = borderColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredManga.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No manga found", color = secondaryTextColor)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredManga) { manga ->
                            MangaListItem(
                                manga = manga,
                                cardColor = surfaceColor,
                                borderColor = borderColor,
                                mainTextColor = mainTextColor,
                                secondaryTextColor = secondaryTextColor,
                                onClick = { onMangaClick(manga) },
                                onIncrementClick = { onIncrementClick(manga) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ... Components ...

@Composable
fun KomikoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isDarkTheme: Boolean,
    mainTextColor: Color,
    secondaryTextColor: Color,
    surfaceColor: Color,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(50.dp)
            .background(surfaceColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = secondaryTextColor)
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("Search title or author...", color = secondaryTextColor, fontSize = 14.sp)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(color = mainTextColor, fontSize = 14.sp),
                    cursorBrush = SolidColor(KomikoOrange),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = secondaryTextColor, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun StatusFilterRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    isDarkTheme: Boolean,
    mainTextColor: Color,
    surfaceColor: Color,
    borderColor: Color
) {
    val filters = listOf("All", "Reading", "Completed", "On Hold", "Dropped", "Plan to Read")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            val bg = if (isSelected) KomikoOrange else surfaceColor
            val border = if (isSelected) Color.Transparent else borderColor
            val text = if (isSelected) Color.White else mainTextColor

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(bg)
                    .border(1.dp, border, RoundedCornerShape(50))
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    color = text,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmptyLibraryState(
    onAddMangaClick: () -> Unit,
    mainTextColor: Color,
    secondaryTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(160.dp).background(KomikoLightOrange, CircleShape), contentAlignment = Alignment.Center) {
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
}

@Composable
fun MangaListItem(
    manga: Manga,
    cardColor: Color,
    borderColor: Color,
    mainTextColor: Color,
    secondaryTextColor: Color,
    onClick: () -> Unit,
    onIncrementClick: () -> Unit // ADDED
) {
    val read = manga.chaptersRead.toFloatOrNull() ?: 0f
    val total = manga.totalChapters.toFloatOrNull() ?: 1f
    val progress = if (total > 0f) (read / total).coerceIn(0f, 1f) else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor),
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha=0.3f))
            ) {
                if (manga.coverUri != null) {
                    AsyncImage(
                        model = manga.coverUri,
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
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
                        color = KomikoLightOrange.copy(alpha=0.5f),
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

            // Right Side: Progress + Quick Increment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(44.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = KomikoOrange,
                        trackColor = borderColor,
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = mainTextColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Quick Increment Button
                IconButton(
                    onClick = onIncrementClick,
                    modifier = Modifier.size(32.dp).background(KomikoOrange.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increment",
                        tint = KomikoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}