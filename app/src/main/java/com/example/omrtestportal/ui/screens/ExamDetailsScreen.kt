package com.example.omrtestportal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.shared.model.Exam
import com.example.omrtestportal.shared.model.PDFNote
import com.example.omrtestportal.shared.model.Test
import com.example.omrtestportal.TestDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailsScreen(
    examId: String,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val exam = remember(examId) {
        (MockDatabase.exams + MockDatabase.stateExams).firstOrNull { it.id == examId }
    }

    if (exam == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Exam not found.")
        }
        return
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Mock Tests", "Notes", "PYQs")

    // Filter Mocks
    val examSeries = remember(examId) { MockDatabase.testSeries.filter { it.examId == examId } }
    val mockTests = remember(examSeries) { examSeries.flatMap { it.tests } }

    // Filter Notes
    val examNotes = remember(examId) {
        val notes = MockDatabase.pdfNotes
        when (examId) {
            "ctet" -> notes.filter { it.subject in listOf("Hindi", "History") }
            "ugc_net", "ugc-net" -> notes.filter { it.subject in listOf("Political Science", "Maths", "Science") }
            else -> notes.filter { it.subject in listOf("History", "Geography", "Hindi") }
        }
    }

    // State for local downloads
    val downloadedNotes = remember { mutableStateMapOf<String, Boolean>() }
    var downloadingNoteId by remember { mutableStateOf<String?>(null) }
    var activePdfReaderNote by remember { mutableStateOf<PDFNote?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exam.shortName + " Exam Prep") },
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
            // Exam Title Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = exam.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Access syllabus-based Mock Tests, verified PDF study notes, and Previous Year Questions (PYQs).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Tab layout switcher
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                    )
                }
            }

            // Swap lists based on active tab selection
            when (selectedTabIndex) {
                0 -> {
                    // Mock Tests Tab
                    if (mockTests.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No Mock Tests available for this exam.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(mockTests) { test ->
                                IndividualTestCard(
                                    test = test,
                                    onClick = { onNavigate(TestDetails(test.id)) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Notes Tab
                    if (examNotes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No Notes available for this exam.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(examNotes) { note ->
                                val isDownloaded = downloadedNotes[note.id] ?: false
                                PDFNoteCard(
                                    note = note,
                                    isDownloaded = isDownloaded,
                                    isDownloading = downloadingNoteId == note.id,
                                    onDownloadClick = {
                                        downloadingNoteId = note.id
                                        coroutineScope.launch {
                                            delay(1200)
                                            downloadedNotes[note.id] = true
                                            downloadingNoteId = null
                                        }
                                    },
                                    onViewClick = {
                                        activePdfReaderNote = note
                                    }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // PYQs Tab
                    val pyqTests = remember(mockTests) {
                        mockTests.mapIndexed { idx, test ->
                            val year = if (examId == "ctet") "2024 Solved" else "2023 Solved"
                            test.copy(title = test.title.replace("Test 03", "PYQ $year").replace("Test 02", "PYQ $year").replace("Test 01", "PYQ $year"))
                        }.take(2)
                    }

                    if (pyqTests.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No Solved PYQ papers available for this exam.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pyqTests) { test ->
                                IndividualTestCard(
                                    test = test,
                                    onClick = { onNavigate(TestDetails(test.id)) }
                                )
                            }
                        }
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
