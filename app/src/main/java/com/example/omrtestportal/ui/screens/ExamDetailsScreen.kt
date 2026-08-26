package com.example.omrtestportal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

    // Filter states
    var selectedPaper by remember { mutableStateOf("All Papers") }
    var selectedMockType by remember { mutableStateOf("All") }
    var selectedMockSubject by remember { mutableStateOf("All") }
    
    // Expanded state for Option B class level card
    var expandedClassLevel by remember { mutableStateOf<String?>(null) }

    // Retrieve Mocks
    val examSeries = remember(examId) { MockDatabase.testSeries.filter { it.examId == examId } }
    val mockTests = remember(examSeries) { examSeries.flatMap { it.tests } }

    // Filtered Mocks list (Excluding PYQs, filtered by paper, test type, and subject)
    val filteredMockTests = remember(mockTests, selectedPaper, selectedMockType, selectedMockSubject, examId) {
        mockTests.filter { test ->
            if (test.isPyq) return@filter false
            
            // Paper filter (only for CTET)
            if (examId == "ctet" && selectedPaper != "All Papers") {
                if (test.paper != null && test.paper != selectedPaper && test.paper != "Both") {
                    return@filter false
                }
            }
            // Mock Type filter
            if (selectedMockType != "All") {
                if (test.testType != selectedMockType) return@filter false
            }
            // Subject filter
            if (selectedMockSubject != "All") {
                if (test.subject != selectedMockSubject) return@filter false
            }
            true
        }
    }

    // Filtered PYQs list (tests with isPyq == true, filtered by paper)
    val pyqTests = remember(mockTests, selectedPaper, examId) {
        val allPyqs = mockTests.filter { it.isPyq }
        val sourceList = if (allPyqs.isEmpty()) {
            // Fallback for legacy exams without isPyq database entries
            mockTests.mapIndexed { idx, test ->
                val year = if (examId == "ctet") "2024 Solved" else "2023 Solved"
                test.copy(
                    isPyq = true,
                    year = if (idx == 0) "2024" else "2023",
                    title = test.title.replace("Test 03", "PYQ $year").replace("Test 02", "PYQ $year").replace("Test 01", "PYQ $year")
                )
            }.take(2)
        } else {
            allPyqs
        }

        if (examId == "ctet" && selectedPaper != "All Papers") {
            sourceList.filter { it.paper == selectedPaper || it.paper == "Both" }
        } else {
            sourceList
        }
    }

    // Filtered Notes list
    val filteredNotesList = remember(examId, selectedPaper) {
        val notes = MockDatabase.pdfNotes
        val baseNotes = when (examId) {
            "ctet" -> notes.filter { it.paper == "Paper 1" || it.paper == "Paper 2" || it.paper == "Both" }
            "ugc_net", "ugc-net" -> notes.filter { it.subject in listOf("Political Science", "Maths", "Science") }
            else -> notes.filter { it.subject in listOf("History", "Geography", "Hindi") }
        }
        
        if (examId == "ctet" && selectedPaper != "All Papers") {
            baseNotes.filter { it.paper == selectedPaper || it.paper == "Both" }
        } else {
            baseNotes
        }
    }

    // NCERT Notes (Option B: Class-wise summaries)
    val ncertNotesGrouped = remember(filteredNotesList) {
        filteredNotesList.filter { it.noteType == "NCERT" && it.classLevel != null }
            .groupBy { it.classLevel!! }
    }

    // Core Subject-wise Notes
    val theoryNotes = remember(filteredNotesList) {
        filteredNotesList.filter { it.noteType != "NCERT" }
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
            // Top-Level Paper Selection (CTET specific)
            if (examId == "ctet") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Filter by Exam Paper",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val paperOptions = listOf("All Papers", "Paper 1", "Paper 2")
                            paperOptions.forEach { paperOpt ->
                                val isSelected = selectedPaper == paperOpt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { 
                                        selectedPaper = paperOpt
                                        // Reset filters when switching paper to avoid empty indices
                                        selectedMockSubject = "All"
                                        selectedMockType = "All"
                                        expandedClassLevel = null
                                    },
                                    label = { Text(if (paperOpt == "All Papers") "All Papers" else if (paperOpt == "Paper 1") "Paper 1 (Class 1-5)" else "Paper 2 (Class 6-8)") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // Exam Title Summary Card for standard non-paper exams
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
                    // Mock Tests Tab - Render sub-filters
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedMockType == "All",
                                onClick = { selectedMockType = "All" },
                                label = { Text("All Mocks") }
                            )
                            FilterChip(
                                selected = selectedMockType == "Full Syllabus",
                                onClick = { selectedMockType = "Full Syllabus" },
                                label = { Text("Full Syllabus") }
                            )
                            FilterChip(
                                selected = selectedMockType == "Subject-wise",
                                onClick = { selectedMockType = "Subject-wise" },
                                label = { Text("Subject-wise") }
                            )
                        }

                        // Subject sub-filter row (Only if subject-wise or All is selected)
                        val subjects = when (selectedPaper) {
                            "Paper 1" -> listOf("All", "CDP", "EVS", "Mathematics")
                            "Paper 2" -> listOf("All", "CDP", "Social Science", "Mathematics")
                            else -> listOf("All", "CDP", "EVS", "Mathematics", "Social Science")
                        }
                        
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            items(subjects) { subj ->
                                FilterChip(
                                    selected = selectedMockSubject == subj,
                                    onClick = { selectedMockSubject = subj },
                                    label = { Text(subj) }
                                )
                            }
                        }

                        if (filteredMockTests.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No Mock Tests match your selection.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredMockTests) { test ->
                                    IndividualTestCard(
                                        test = test,
                                        onClick = { onNavigate(TestDetails(test.id)) }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Notes Tab - Option B: Hierarchical Grouped Cards
                    if (ncertNotesGrouped.isEmpty() && theoryNotes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No Notes available for this selection.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (ncertNotesGrouped.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "NCERT Class-wise Summaries",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                
                                val sortedClasses = ncertNotesGrouped.keys.sortedBy { classKey ->
                                    classKey.replace("Class ", "").toIntOrNull() ?: 99
                                }
                                
                                items(sortedClasses) { classLevel ->
                                    val notesInClass = ncertNotesGrouped[classLevel] ?: emptyList()
                                    val isExpanded = expandedClassLevel == classLevel
                                    
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedClassLevel = if (isExpanded) null else classLevel
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Book,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column {
                                                        Text(
                                                            text = "$classLevel NCERT Book Notes",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = "${notesInClass.size} Notes Available",
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                                Icon(
                                                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                                                )
                                            }
                                            
                                            if (isExpanded) {
                                                Spacer(modifier = Modifier.height(12.dp))
                                                val notesBySubject = notesInClass.groupBy { it.subject }
                                                notesBySubject.forEach { (subj, notes) ->
                                                    Text(
                                                        text = subj.toUpperCase(),
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                    notes.forEach { note ->
                                                        val isDownloaded = downloadedNotes[note.id] ?: false
                                                        PDFNoteRowItem(
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
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            if (theoryNotes.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Core Subject-wise Notes",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                
                                items(theoryNotes) { note ->
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
                }
                2 -> {
                    // PYQs Tab
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

@Composable
fun PDFNoteRowItem(
    note: PDFNote,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${note.sizeMb} MB",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            when {
                isDownloading -> {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
                isDownloaded -> {
                    IconButton(onClick = onViewClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "View PDF",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                else -> {
                    IconButton(onClick = onDownloadClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Note",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
