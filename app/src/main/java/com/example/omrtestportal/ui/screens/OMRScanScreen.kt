package com.example.omrtestportal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.omrtestportal.Main
import com.example.omrtestportal.OMRResult
import com.example.omrtestportal.OMRScanner
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.shared.model.AttemptRecord
import com.example.omrtestportal.shared.model.Test
import kotlinx.coroutines.delay

// -------------------------------------------------------------
// Screen 1: OMR Scan Preparation (Instruction & Print Paper)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OMRScanPrepScreen(
    testId: String,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val test = remember(testId) {
        MockDatabase.testSeries.flatMap { it.tests }.firstOrNull { it.id == testId }
    }

    if (test == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Test not found.")
        }
        return
    }

    var isPaperDownloaded by remember { mutableStateOf(false) }
    var isOmrDownloaded by remember { mutableStateOf(false) }
    var showPaperSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OMR Attempt Mode") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = test.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Steps indicator card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "How it works:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        StepItem(number = 1, text = "Download & Print both the Question Paper PDF and the custom Bubble OMR sheet.")
                        StepItem(number = 2, text = "Attempt the test offline by marking bubbles on the printed OMR sheet with a black/blue pen.")
                        StepItem(number = 3, text = "Open this app's camera scanner, align the OMR sheet inside the frame, and scan.")
                        StepItem(number = 4, text = "The app immediately processes the markings and uploads details to your Profile.")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // File Downloads Section
                Text(
                    text = "Downloads Required",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                DownloadRow(
                    title = "Question Paper PDF",
                    isDownloaded = isPaperDownloaded,
                    onDownload = { isPaperDownloaded = true },
                    onPreview = { showPaperSheet = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DownloadRow(
                    title = "OMR Grid Sheet PDF (30 Bubbles)",
                    isDownloaded = isOmrDownloaded,
                    onDownload = { isOmrDownloaded = true }
                )
            }

            Button(
                onClick = { onNavigate(OMRScanner(test.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open OMR Camera Scanner", fontSize = 16.sp)
            }
        }

        if (showPaperSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPaperSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question Paper Reader",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showPaperSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    val mockPrompts = listOf(
                        "Which of the following is a primary agent of socialization for children?" to listOf("Family", "School", "Peer Group", "Media"),
                        "According to Jean Piaget, in which stage of cognitive development do children develop object permanence?" to listOf("Sensorimotor Stage", "Preoperational Stage", "Concrete Operational Stage", "Formal Operational Stage"),
                        "In the context of progressive education, which of the following statements is correct?" to listOf("Students should be active problem solvers.", "Classrooms should be democratic.", "Emphasis is on rote memory.", "Both A and B"),
                        "A child learns that a dog has four legs, fur, and barks. When he sees a cat, he calls it a dog. This is an example of:" to listOf("Assimilation", "Accommodation", "Schema", "Conservation"),
                        "Which learning theorist proposed the concept of 'Zone of Proximal Development' (ZPD)?" to listOf("Lev Vygotsky", "B.F. Skinner", "Albert Bandura", "Jerome Bruner")
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            Text(
                                text = test.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Full Marks: ${test.totalMarks} | Duration: ${test.durationMinutes} Mins",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        items(test.totalQuestions) { index ->
                            val prompt = mockPrompts[index % mockPrompts.size]
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Q.${index + 1} ${prompt.first}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                prompt.second.forEachIndexed { optIdx, opt ->
                                    val letter = ('A'.toInt() + optIdx).toChar()
                                    Text(
                                        text = "$letter. $opt",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepItem(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
fun DownloadRow(
    title: String,
    isDownloaded: Boolean,
    onDownload: () -> Unit,
    onPreview: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isDownloaded) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onPreview != null) {
                    IconButton(onClick = onPreview) {
                        Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (isDownloaded) {
                    Text("Downloaded", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                } else {
                    IconButton(onClick = onDownload) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Screen 2: OMR Camera Scanner Simulator
// -------------------------------------------------------------
@Composable
fun OMRScannerScreen(
    testId: String,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val test = remember(testId) {
        MockDatabase.testSeries.flatMap { it.tests }.firstOrNull { it.id == testId }
    }

    if (test == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Test not found.")
        }
        return
    }

    var isScanning by remember { mutableStateOf(false) }
    var scanStatusText by remember { mutableStateOf("Ready to scan OMR") }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            scanStatusText = "Analyzing OMR sheet coordinates..."
            delay(1000)
            scanStatusText = "Finding anchor points (4 corners)..."
            delay(1000)
            scanStatusText = "Extracting bubble fill ratios..."
            delay(1200)
            scanStatusText = "Evaluating scorecard..."
            delay(800)

            // Simulate student filling answers:
            // 80% answered correct, some incorrect, some skipped.
            val options = listOf("A", "B", "C", "D", "None", "MULTIPLE")
            val submission = test.answerKey.mapValues { (qNo, correctOpt) ->
                val roll = (1..100).random()
                when {
                    roll <= 75 -> correctOpt // Correct
                    roll <= 90 -> {
                        // Incorrect option
                        options.filter { it != correctOpt && it != "None" && it != "MULTIPLE" }.random()
                    }
                    roll <= 95 -> "MULTIPLE" // Double marked / Smudge!
                    else -> "None" // Skipped
                }
            }

            val record = MockDatabase.gradeTest(test, submission, "OMR")
            onNavigate(OMRResult(record.id))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Mock Camera Viewport
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Info overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "OMR Scanner",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(48.dp)) // Equalizer spacer
            }

            // Central scanning frame guide
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .border(3.dp, if (isScanning) Color.Green else Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // corner anchors simulation dots
                Box(modifier = Modifier.fillMaxSize()) {
                    CornerDot(Alignment.TopStart, isScanning)
                    CornerDot(Alignment.TopEnd, isScanning)
                    CornerDot(Alignment.BottomStart, isScanning)
                    CornerDot(Alignment.BottomEnd, isScanning)
                }

                if (!isScanning) {
                    Text(
                        text = "Position OMR paper inside the box\nEnsure good lighting",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    CircularProgressIndicator(color = Color.Green)
                }
            }

            // Bottom scan controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = scanStatusText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (!isScanning) {
                        Button(
                            onClick = { isScanning = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capture & Scan OMR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.CornerDot(alignment: Alignment, active: Boolean) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .align(alignment)
            .border(2.dp, if (active) Color.Green else Color.White, CircleShape)
            .background(if (active) Color.Green.copy(alpha = 0.5f) else Color.Transparent)
    )
}

// -------------------------------------------------------------
// Screen 3: OMR / Online Attempt Result Scorecard
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OMRResultScreen(
    attemptId: String,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val record = remember(attemptId) {
        MockDatabase.attemptHistory.firstOrNull { it.id == attemptId }
    }
    val doubleMarkedCount = remember(record) {
        record?.bubbleMap?.values?.count { it == "MULTIPLE" } ?: 0
    }

    if (record == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Scorecard record not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attempt Scorecard") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Score card summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = record.testTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Score Obtained",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${record.marksObtained.toInt()} / ${record.totalMarks.toInt()}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            val scorePct = (record.marksObtained / record.totalMarks) * 100
                            val percentile = (scorePct * 0.95 + 4.2).coerceIn(12.4, 99.6)
                            val topPercent = 100.0 - percentile
                            Text(
                                text = String.format("Percentage: %.1f%%", scorePct),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = String.format("Percentile: %.1f%% (Top %.1f%%)", percentile, topPercent),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Attempt Mode: ${record.attemptType}") }
                            )
                        }
                    }
                }

                // Stats breakdown
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Section Analysis",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            ScoreStatRow(
                                label = "Correct Answers",
                                value = "${record.correctAnswers}",
                                color = Color(0xFF4CAF50),
                                icon = Icons.Default.CheckCircle
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            ScoreStatRow(
                                label = "Incorrect Answers",
                                value = "${record.incorrectAnswers}",
                                color = Color(0xFFE53935),
                                icon = Icons.Default.Cancel
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            ScoreStatRow(
                                label = "Skipped Questions",
                                value = "${record.skippedAnswers}",
                                color = Color.Gray,
                                icon = Icons.Default.RemoveCircle
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            ScoreStatRow(
                                label = "Double Marked (Smudge)",
                                value = "$doubleMarkedCount",
                                color = MaterialTheme.colorScheme.tertiary,
                                icon = Icons.Default.Warning
                            )
                        }
                    }
                }

                // Date attempt metadata
                item {
                    Text(
                        text = "Exam date: ${record.dateString}",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(com.example.omrtestportal.TestReview(record.id)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Review Detailed Answers", fontSize = 16.sp)
                }

                Button(
                    onClick = { onNavigate(Main) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Return to Home", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ScoreStatRow(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontSize = 14.sp)
        }
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
