package com.example.omrtestportal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.shared.model.PDFNote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFNotesScreen(
    onBack: () -> Unit
) {
    val subjects = listOf("All", "History", "Geography", "Political Science", "Hindi", "Maths", "Science")
    var selectedSubject by remember { mutableStateOf("All") }
    var downloadingNoteId by remember { mutableStateOf<String?>(null) }
    var activePdfReaderNote by remember { mutableStateOf<PDFNote?>(null) }

    // Use mutableStateMap to track downloaded notes locally in-memory
    val downloadedNotes = remember { mutableStateMapOf<String, Boolean>() }

    val filteredNotes = remember(selectedSubject) {
        if (selectedSubject == "All") {
            MockDatabase.pdfNotes
        } else {
            MockDatabase.pdfNotes.filter { it.subject.equals(selectedSubject, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF Notes Library") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Subject selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { subject ->
                    FilterChip(
                        selected = selectedSubject == subject,
                        onClick = { selectedSubject = subject },
                        label = { Text(subject) }
                    )
                }
            }

            if (filteredNotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notes found for $selectedSubject", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes) { note ->
                        val isDownloaded = downloadedNotes[note.id] ?: note.isDownloaded
                        PDFNoteCard(
                            note = note,
                            isDownloaded = isDownloaded,
                            isDownloading = downloadingNoteId == note.id,
                            onDownloadClick = {
                                // Simulate download
                                downloadingNoteId = note.id
                                // Run a small timer representation
                                // In production this uses WorkManager / Flow
                                downloadedNotes[note.id] = true
                                downloadingNoteId = null
                            },
                            onViewClick = {
                                activePdfReaderNote = note
                            }
                        )
                    }
                }
            }
        }
    }

    // PDF Reader Dialog Simulator
    activePdfReaderNote?.let { note ->
        PdfReaderDialog(
            note = note,
            onClose = { activePdfReaderNote = null }
        )
    }
}

@Composable
fun PDFNoteCard(
    note: PDFNote,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(note.subject, fontSize = 10.sp) },
                        modifier = Modifier.height(24.dp)
                    )
                    Text(
                        text = "${note.sizeMb} MB",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            when {
                isDownloading -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
                isDownloaded -> {
                    Button(onClick = onViewClick) {
                        Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View")
                    }
                }
                else -> {
                    IconButton(
                        onClick = onDownloadClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download note")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderDialog(
    note: PDFNote,
    onClose: () -> Unit
) {
    var currentPage by remember { mutableStateOf(1) }
    val totalPages = 12

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(note.title, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close PDF")
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark")
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            enabled = currentPage > 1,
                            onClick = { currentPage-- }
                        ) {
                            Icon(Icons.Default.NavigateBefore, contentDescription = "Previous page")
                        }
                        Text("Page $currentPage of $totalPages", fontWeight = FontWeight.Medium)
                        IconButton(
                            enabled = currentPage < totalPages,
                            onClick = { currentPage++ }
                        ) {
                            Icon(Icons.Default.NavigateNext, contentDescription = "Next page")
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Mock PDF Content Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "${note.subject} Study Guide: Chapter $currentPage",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = getMockPdfPageContent(note.subject, currentPage),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

private fun getMockPdfPageContent(subject: String, page: Int): String {
    return """
        [PAGE $page - $subject STUDY MATERIAL]
        
        Section ${page}.1: Fundamentals of $subject
        This study material provides key concepts, facts, and explanations designed for CTET/UPTET preparation.
        
        Key Takeaways:
        1. Always analyze previous years' question (PYQ) distributions to prioritize sub-topics.
        2. Focus on conceptual clarity rather than rote memorization.
        3. Make short notes of formulas and core historical timelines.
        
        Detailed Explanations:
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
        
        Important facts for competitive exams:
        - Point A: Essential definition or statement of principles.
        - Point B: Frequently asked theories and contributions of key thinkers.
        - Point C: Applications, diagrams, and formulas.
        
        Review this section carefully. Try attempting the mock tests in the Test Series section related to $subject to measure your retention.
    """.trimIndent()
}
