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
    var selectedPaper by remember { mutableStateOf("Paper 1") }
    var selectedMockType by remember { mutableStateOf("Full Syllabus") }
    var selectedMockSubject by remember { mutableStateOf("") }
    var selectedNoteClass by remember { mutableStateOf("") }
    var selectedNoteSubject by remember { mutableStateOf("") }
    var selectedPyqYear by remember { mutableStateOf("All Years") }
    
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
            if (examId == "ctet") {
                if (test.paper != null && test.paper != selectedPaper && test.paper != "Both") {
                    return@filter false
                }
            }
            // Mock Type filter
            if (selectedMockType == "Full Syllabus") {
                if (test.testType != "Full Syllabus") return@filter false
            } else if (selectedMockType == "Subject-wise") {
                if (test.testType != "Subject-wise") return@filter false
                if (selectedMockSubject.isNotEmpty() && test.subject != selectedMockSubject) return@filter false
            }
            true
        }
    }

    // Filtered PYQs list (tests with isPyq == true, filtered by paper and year)
    val pyqTests = remember(mockTests, selectedPaper, selectedPyqYear, examId) {
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

        val filteredByPaper = if (examId == "ctet") {
            sourceList.filter { it.paper == selectedPaper || it.paper == "Both" }
        } else {
            sourceList
        }

        if (selectedPyqYear != "All Years") {
            filteredByPaper.filter { it.year == selectedPyqYear }
        } else {
            filteredByPaper
        }
    }

    // Filtered Notes list
    val filteredNotesList = remember(examId, selectedPaper, selectedNoteSubject) {
        val notes = MockDatabase.pdfNotes
        val baseNotes = when (examId) {
            "ctet" -> notes.filter { it.paper == "Paper 1" || it.paper == "Paper 2" || it.paper == "Both" }
            "ugc_net", "ugc-net" -> notes.filter { it.subject in listOf("Political Science", "Maths", "Science") }
            else -> notes.filter { it.subject in listOf("History", "Geography", "Hindi") }
        }
        
        val paperFiltered = if (examId == "ctet") {
            baseNotes.filter { it.paper == selectedPaper || it.paper == "Both" }
        } else {
            baseNotes
        }

        if (selectedNoteSubject.isNotEmpty()) {
            paperFiltered.filter { it.subject == selectedNoteSubject }
        } else {
            paperFiltered
        }
    }

    // NCERT Notes (Option B: Class-wise summaries)
    val ncertNotesGrouped = remember(filteredNotesList, selectedNoteClass) {
        val classFiltered = if (selectedNoteClass.isNotEmpty()) {
            filteredNotesList.filter { it.classLevel == selectedNoteClass }
        } else {
            filteredNotesList
        }
        classFiltered.filter { it.noteType == "NCERT" && it.classLevel != null }
            .groupBy { it.classLevel!! }
    }

    // Core Subject-wise Notes
    val theoryNotes = remember(filteredNotesList, selectedNoteClass) {
        if (selectedNoteClass.isNotEmpty()) {
            emptyList()
        } else {
            filteredNotesList.filter { it.noteType != "NCERT" }
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
            // Top-Level Paper Selection (CTET specific)
            if (examId == "ctet") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Paper:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            val paperOptions = listOf("Paper 1", "Paper 2")
                            paperOptions.forEach { paperOpt ->
                                val isSelected = selectedPaper == paperOpt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { 
                                        selectedPaper = paperOpt
                                        selectedMockSubject = ""
                                        selectedMockType = "Full Syllabus"
                                        expandedClassLevel = null
                                    },
                                    label = { Text(paperOpt, fontSize = 11.5.sp) },
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
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Full Syllabus Chip
                            val isFullSyllabus = selectedMockType == "Full Syllabus"
                            FilterChip(
                                selected = isFullSyllabus,
                                onClick = { 
                                    selectedMockType = "Full Syllabus"
                                    selectedMockSubject = ""
                                },
                                label = { Text("Full Syllabus", fontSize = 11.5.sp) }
                            )
                            
                            // Subject Dropdown Box
                            var dropdownExpanded by remember { mutableStateOf(false) }
                            val isSubjectWise = selectedMockType == "Subject-wise"
                            val subjects = if (selectedPaper == "Paper 1") {
                                listOf("CDP", "Hindi", "English", "EVS", "Mathematics")
                            } else {
                                listOf("CDP", "Hindi", "English", "Mathematics", "Social Science")
                            }
                            val subjectsLabels = if (selectedPaper == "Paper 1") {
                                listOf("CDP", "Language (Hindi)", "Language (English)", "EVS", "Mathematics")
                            } else {
                                listOf("CDP", "Language (Hindi)", "Language (English)", "Mathematics", "Social Science")
                            }
                            
                            val activeLabel = if (isSubjectWise && selectedMockSubject.isNotEmpty()) {
                                val index = subjects.indexOf(selectedMockSubject)
                                if (index >= 0) subjectsLabels[index] else "Subject-wise Dropdown"
                            } else {
                                "Subject-wise Dropdown"
                            }
                            
                            Box {
                                FilterChip(
                                    selected = isSubjectWise,
                                    onClick = { dropdownExpanded = true },
                                    label = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(activeLabel, fontSize = 11.5.sp)
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                
                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Clear Subject Filter") },
                                        onClick = {
                                            selectedMockType = "Full Syllabus"
                                            selectedMockSubject = ""
                                            dropdownExpanded = false
                                        }
                                    )
                                    subjects.forEachIndexed { index, sub ->
                                        val label = subjectsLabels[index]
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                selectedMockType = "Subject-wise"
                                                selectedMockSubject = sub
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
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
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // All Notes Chip
                            val isAllNotes = selectedNoteClass.isEmpty() && selectedNoteSubject.isEmpty()
                            FilterChip(
                                selected = isAllNotes,
                                onClick = { 
                                    selectedNoteClass = ""
                                    selectedNoteSubject = ""
                                },
                                label = { Text("All Notes", fontSize = 11.5.sp) }
                            )
                            
                            // Classes Dropdown Box
                            var classDropdownExpanded by remember { mutableStateOf(false) }
                            val isClassSelected = selectedNoteClass.isNotEmpty()
                            val classes = listOf("Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8")
                            
                            Box {
                                FilterChip(
                                    selected = isClassSelected,
                                    onClick = { classDropdownExpanded = true },
                                    label = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(if (isClassSelected) selectedNoteClass else "Classes Dropdown", fontSize = 11.5.sp)
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                
                                DropdownMenu(
                                    expanded = classDropdownExpanded,
                                    onDismissRequest = { classDropdownExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Clear Class Filter") },
                                        onClick = {
                                            selectedNoteClass = ""
                                            classDropdownExpanded = false
                                        }
                                    )
                                    classes.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c) },
                                            onClick = {
                                                selectedNoteClass = c
                                                classDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Subjects Dropdown Box
                            var subjectDropdownExpanded by remember { mutableStateOf(false) }
                            val isSubjectSelected = selectedNoteSubject.isNotEmpty()
                            val subjects = if (selectedPaper == "Paper 1") {
                                listOf("CDP", "Hindi", "English", "EVS", "Mathematics")
                            } else {
                                listOf("CDP", "Hindi", "English", "Mathematics", "Social Science")
                            }
                            val subjectsLabels = if (selectedPaper == "Paper 1") {
                                listOf("CDP", "Language (Hindi)", "Language (English)", "EVS", "Mathematics")
                            } else {
                                listOf("CDP", "Language (Hindi)", "Language (English)", "Mathematics", "Social Science")
                            }
                            
                            val activeLabel = if (isSubjectSelected) {
                                val index = subjects.indexOf(selectedNoteSubject)
                                if (index >= 0) subjectsLabels[index] else "Subjects Dropdown"
                            } else {
                                "Subjects Dropdown"
                            }
                            
                            Box {
                                FilterChip(
                                    selected = isSubjectSelected,
                                    onClick = { subjectDropdownExpanded = true },
                                    label = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(activeLabel, fontSize = 11.5.sp)
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                
                                DropdownMenu(
                                    expanded = subjectDropdownExpanded,
                                    onDismissRequest = { subjectDropdownExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Clear Subject Filter") },
                                        onClick = {
                                            selectedNoteSubject = ""
                                            subjectDropdownExpanded = false
                                        }
                                    )
                                    subjects.forEachIndexed { index, sub ->
                                        val label = subjectsLabels[index]
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                selectedNoteSubject = sub
                                                subjectDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

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
                                                            text = subj.uppercase(),
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
                }
                2 -> {
                    // PYQs Tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val pyqYearOptions = listOf("All Years", "2024", "2023")
                            pyqYearOptions.forEach { opt ->
                                val isSelected = selectedPyqYear == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedPyqYear = opt },
                                    label = { Text(opt, fontSize = 11.5.sp) }
                                )
                            }
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
