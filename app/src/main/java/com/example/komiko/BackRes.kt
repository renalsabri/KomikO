// BackRes.kt
package com.example.komiko

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

// Define colors locally if needed, or reuse globals if available
private val BackResBgDark = Color(0xFF221A10)
private val BackResBgLight = Color(0xFFF8F7F6)
private val BackResTextDark = Color(0xFFF4F3F0)
private val BackResTextLight = Color(0xFF181511)
//private val KomikoOrange = Color(0xFFF0A500)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    isDarkTheme: Boolean,
    mangaList: List<Manga>,
    onRestore: (List<Manga>) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val bgColor = if (isDarkTheme) BackResBgDark else BackResBgLight
    val textColor = if (isDarkTheme) BackResTextDark else BackResTextLight

    // --- Export Launcher ---
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            saveCsvToUri(context, uri, mangaList)
        }
    }

    // --- Import Launcher ---
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val loadedList = loadCsvFromUri(context, uri)
            if (loadedList.isNotEmpty()) {
                onRestore(loadedList)
                Toast.makeText(context, "Restored ${loadedList.size} manga!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to load or file empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Backup & Restore", fontWeight = FontWeight.Bold, color = textColor) },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudUpload,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = KomikoOrange
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Export your library to a CSV file to save it safely or move it to another device.",
                color = textColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // EXPORT BUTTON
            Button(
                onClick = { exportLauncher.launch("komiko_backup_${System.currentTimeMillis()}.csv") },
                colors = ButtonDefaults.buttonColors(containerColor = KomikoOrange),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Export Backup", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(48.dp))
            Divider(color = textColor.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(48.dp))

            Icon(
                imageVector = Icons.Rounded.CloudDownload,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = KomikoOrange.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Import a previously exported CSV file to restore your library.",
                color = textColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // IMPORT BUTTON
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv")) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, KomikoOrange)
            ) {
                Text("Import Backup", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KomikoOrange)
            }
        }
    }
}

// --- CSV LOGIC ---

private fun saveCsvToUri(context: Context, uri: Uri, list: List<Manga>) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = OutputStreamWriter(outputStream)
            // Header
            writer.write("Title,Author,Status,ChaptersRead,TotalChapters,Rating,CoverUri\n")
            // Rows
            list.forEach { manga ->
                val line = buildString {
                    append(escapeCsv(manga.title)).append(",")
                    append(escapeCsv(manga.author)).append(",")
                    append(escapeCsv(manga.status)).append(",")
                    append(escapeCsv(manga.chaptersRead)).append(",")
                    append(escapeCsv(manga.totalChapters)).append(",")
                    append(manga.rating).append(",") // Int doesn't need escape
                    append(escapeCsv(manga.coverUri ?: ""))
                    append("\n")
                }
                writer.write(line)
            }
            writer.flush()
            Toast.makeText(context, "Backup Saved!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving file", Toast.LENGTH_SHORT).show()
    }
}

private fun loadCsvFromUri(context: Context, uri: Uri): List<Manga> {
    val restoredList = mutableListOf<Manga>()
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line = reader.readLine() // Skip Header

            while (reader.readLine().also { line = it } != null) {
                val tokens = parseCsvLine(line!!)
                if (tokens.size >= 6) {
                    val m = Manga(
                        title = tokens.getOrElse(0) { "" },
                        author = tokens.getOrElse(1) { "" },
                        status = tokens.getOrElse(2) { "Reading" },
                        chaptersRead = tokens.getOrElse(3) { "0" },
                        totalChapters = tokens.getOrElse(4) { "" },
                        rating = tokens.getOrElse(5) { "0" }.toIntOrNull() ?: 0,
                        coverUri = tokens.getOrElse(6) { "" }.takeIf { it.isNotEmpty() }
                    )
                    restoredList.add(m)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return emptyList()
    }
    return restoredList
}

// Simple Helper to handle quotes and commas in CSV
private fun escapeCsv(value: String): String {
    var text = value.replace("\"", "\"\"") // Escape double quotes
    if (text.contains(",") || text.contains("\n") || text.contains("\"")) {
        text = "\"$text\""
    }
    return text
}

// Simple CSV Line Parser
private fun parseCsvLine(line: String): List<String> {
    val tokens = mutableListOf<String>()
    var inQuotes = false
    val sb = StringBuilder()

    for (char in line) {
        if (char == '\"') {
            inQuotes = !inQuotes
        } else if (char == ',' && !inQuotes) {
            tokens.add(sb.toString())
            sb.clear()
        } else {
            sb.append(char)
        }
    }
    tokens.add(sb.toString()) // Last token

    // Remove wrapping quotes and unescape double quotes
    return tokens.map { token ->
        var t = token
        if (t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length - 1)
        }
        t.replace("\"\"", "\"")
    }
}