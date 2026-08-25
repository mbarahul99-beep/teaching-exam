package com.example.omrtestportal.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.omrtestportal.OMRResult
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.shared.model.Test
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

enum class ExamState {
    INSTRUCTIONS,
    ACTIVE
}

data class CompetitiveQuestion(
    val text: String,
    val options: List<String>,
    val correctOpt: String, // 'A', 'B', 'C', 'D'
    val explanation: String
)

val genericQuestions = listOf(
    CompetitiveQuestion(
        text = "Which of the following physical quantities has the same dimensional formula as that of impulse?",
        options = listOf("Force", "Linear Momentum", "Torque", "Pressure"),
        correctOpt = "B",
        explanation = "Impulse is Force * Time, which has dimensions [MLT^-1]. This is identical to the dimensional formula of linear momentum."
    ),
    CompetitiveQuestion(
        text = "A particle moves in a circle of radius R with constant speed v. The magnitude of average acceleration during a semi-circle turn is:",
        options = listOf("v^2 / R", "2v^2 / (pi * R)", "v^2 / (2 * R)", "Zero"),
        correctOpt = "B",
        explanation = "Average acceleration is change in velocity divided by time: 2v / (pi*R/v) = 2v^2 / (pi*R)."
    ),
    CompetitiveQuestion(
        text = "Which of the following organic compounds will show optical activity?",
        options = listOf("2-Chlorobutane", "1-Chlorobutane", "2-Chloropropane", "Butane"),
        correctOpt = "A",
        explanation = "2-Chlorobutane contains a chiral carbon atom bonded to four different groups (-H, -Cl, -CH3, -CH2CH3)."
    ),
    CompetitiveQuestion(
        text = "The primary structure of a protein refers to:",
        options = listOf("Helix configuration", "Sequence of amino acids", "Three dimensional foldings", "Aggregation of sub-units"),
        correctOpt = "B",
        explanation = "The primary structure is the linear sequence of amino acids joined by peptide bonds."
    ),
    CompetitiveQuestion(
        text = "Which cell organelle is responsible for cellular respiration and ATP generation?",
        options = listOf("Ribosome", "Mitochondria", "Chloroplast", "Lysosome"),
        correctOpt = "B",
        explanation = "Mitochondria are the site of aerobic respiration and generate ATP (energy currency of the cell)."
    ),
    CompetitiveQuestion(
        text = "In angiosperms, double fertilization is characterized by:",
        options = listOf("Fusion of two polar nuclei", "Syngamy and triple fusion", "Fertilization of two eggs", "Fusion of tube cell and egg"),
        correctOpt = "B",
        explanation = "Double fertilization involves syngamy (fusion of one male gamete with the egg) and triple fusion (fusion of second male gamete with secondary nucleus)."
    ),
    CompetitiveQuestion(
        text = "Which of the following is considered a primary agent of socialization for young children, especially during early childhood?",
        options = listOf("Mass media and community networks", "Family and immediate caregivers", "Formal school curriculum", "Peer groups and extra-curricular clubs"),
        correctOpt = "B",
        explanation = "Family is the primary agent of socialization that shapes early behavior and values in childhood."
    ),
    CompetitiveQuestion(
        text = "In the context of cognitive development, which stage of Jean Piaget's theory matches with the ability to perform conservation tasks?",
        options = listOf("Sensorimotor stage (0 to 2 years)", "Pre-operational stage (2 to 7 years)", "Concrete operational stage (7 to 11 years)", "Formal operational stage (11 years and above)"),
        correctOpt = "C",
        explanation = "Concrete operational stage matches with conservation and logical operations on concrete events."
    ),
    CompetitiveQuestion(
        text = "A teacher designs classroom tasks that require collaborative peer dialogues and scaffolding. This strategy aligns with:",
        options = listOf("B.F. Skinner", "Lev Vygotsky", "Jean Piaget", "Albert Bandura"),
        correctOpt = "B",
        explanation = "Lev Vygotsky's social constructivism emphasizes collaborative dialogue, ZPD, and scaffolding."
    )
)

fun getDynamicQuestion(qNum: Int, targetAnsChar: String): Pair<String, List<String>> {
    val baseQ = genericQuestions[(qNum - 1) % genericQuestions.size]
    val optionsList = baseQ.options.toMutableList()
    val baseCorrectIdx = when (baseQ.correctOpt) {
        "A" -> 0
        "B" -> 1
        "C" -> 2
        else -> 3
    }
    val targetIdx = when (targetAnsChar) {
        "A" -> 0
        "B" -> 1
        "C" -> 2
        else -> 3
    }
    if (baseCorrectIdx != targetIdx) {
        val temp = optionsList[targetIdx]
        optionsList[targetIdx] = optionsList[baseCorrectIdx]
        optionsList[baseCorrectIdx] = temp
    }
    return Pair("[Q.$qNum] ${baseQ.text}", optionsList)
}

fun getDynamicExplanation(qNum: Int): String {
    val baseQ = genericQuestions[(qNum - 1) % genericQuestions.size]
    return baseQ.explanation
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineTestScreen(
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

    var examState by remember { mutableStateOf(ExamState.INSTRUCTIONS) }
    var declarationChecked by remember { mutableStateOf(false) }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<Int, String>() }
    val markedForReview = remember { mutableStateMapOf<Int, Boolean>() }

    // Proctoring states
    var cheatWarnings by remember { mutableStateOf(0) }
    var showProctorWarningDialog by remember { mutableStateOf(false) }

    // Countdown Timer in seconds
    var secondsRemaining by remember { mutableStateOf(test.durationMinutes * 60) }
    var showPaletteDrawer by remember { mutableStateOf(false) }
    var showSubmitConfirmation by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // Lifecycle Proctoring observer
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, examState) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && examState == ExamState.ACTIVE) {
                // Focus lost (backgrounded, locked, split-screen or notification panel opened)
                cheatWarnings++
                showProctorWarningDialog = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Auto-submission logic on warnings limit
    LaunchedEffect(cheatWarnings) {
        if (cheatWarnings >= 3) {
            val record = MockDatabase.gradeTest(test, answers.toMap(), "ONLINE")
            onNavigate(OMRResult(record.id))
        }
    }

    // Timer loop
    LaunchedEffect(examState) {
        if (examState == ExamState.ACTIVE) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
            }
            // Auto-submit when time expires
            val record = MockDatabase.gradeTest(test, answers.toMap(), "ONLINE")
            onNavigate(OMRResult(record.id))
        }
    }

    val formatTime = remember(secondsRemaining) {
        val mins = secondsRemaining / 60
        val secs = secondsRemaining % 60
        String.format("%02d:%02d", mins, secs)
    }

    if (examState == ExamState.INSTRUCTIONS) {
        // Setup Instructions view
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Exam Instructions") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(test.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Please read the instructions carefully before starting.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Key Parameters:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Duration: ${test.durationMinutes} Minutes", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Total Questions: ${test.totalQuestions}", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Marking Scheme: +4 for Correct, -1 for Incorrect", fontSize = 13.sp)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Proctoring Guidelines:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        Text("• Leaving the test screen or backgrounding the app is strictly monitored.", fontSize = 12.sp)
                        Text("• Backgrounding the app logs a cheat warning.", fontSize = 12.sp)
                        Text("• Accumulating 3 warnings will result in immediate automatic test submission.", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = declarationChecked,
                        onCheckedChange = { declarationChecked = it }
                    )
                    Text(
                        text = "I declare that I have read the instructions and guidelines and will take the test honestly.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                Button(
                    onClick = { examState = ExamState.ACTIVE },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = declarationChecked,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Start Proctoring & Attempt Test", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        // Active Online Test Screen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(test.title, fontSize = 14.sp, maxLines = 1, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Timer: $formatTime",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (secondsRemaining < 300) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                if (cheatWarnings > 0) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Warnings: $cheatWarnings / 3",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { showExitConfirmation = true }) {
                            Icon(Icons.Default.Close, contentDescription = "Exit Test")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showPaletteDrawer = true }) {
                            Icon(Icons.Default.GridView, contentDescription = "Question Palette")
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { currentQuestionIndex = max(0, currentQuestionIndex - 1) },
                            enabled = currentQuestionIndex > 0
                        ) {
                            Text("PREV")
                        }

                        Button(
                            onClick = { showSubmitConfirmation = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("SUBMIT")
                        }

                        TextButton(
                            onClick = { currentQuestionIndex = min(test.totalQuestions - 1, currentQuestionIndex + 1) },
                            enabled = currentQuestionIndex < test.totalQuestions - 1
                        ) {
                            Text("NEXT")
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val qNum = currentQuestionIndex + 1
                val targetAnsChar = test.answerKey[qNum] ?: "A"
                val (qText, qOptions) = remember(qNum, targetAnsChar) {
                    getDynamicQuestion(qNum, targetAnsChar)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header (Question number and review check)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question $qNum of ${test.totalQuestions}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = markedForReview[qNum] == true,
                                onCheckedChange = { markedForReview[qNum] = it }
                            )
                            Text("Mark for Review", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Question Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = qText,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options list
                    val options = listOf("A", "B", "C", "D")
                    options.forEachIndexed { index, option ->
                        val isSelected = answers[qNum] == option
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable { answers[qNum] = option },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = qOptions[index],
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Clear Response Button
                    if (answers[qNum] != null) {
                        TextButton(
                            onClick = { answers.remove(qNum) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Clear Selection", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Question Palette Drawer Overlay
                if (showPaletteDrawer) {
                    Dialog(
                        onDismissRequest = { showPaletteDrawer = false }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.75f),
                            shape = RoundedCornerShape(16.dp)
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
                                    Text("Question Palette", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { showPaletteDrawer = false }) {
                                        Icon(Icons.Default.Close, contentDescription = "Close Palette")
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(5),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(test.totalQuestions) { index ->
                                        val qNo = index + 1
                                        val isAnswered = answers[qNo] != null
                                        val isMarked = markedForReview[qNo] == true
                                        
                                        // Color map: Answered (Green), Marked & Answered (Purple), Marked & Unanswered (Orange), Unvisited (Gray)
                                        val bgColor = when {
                                            isMarked && isAnswered -> Color(0xFF9C27B0) // Purple
                                            isMarked -> Color(0xFFFF9800) // Orange
                                            isAnswered -> Color(0xFF4CAF50) // Green
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                        val textColor = when {
                                            isMarked || isAnswered -> Color.White
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }

                                        Box(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .clip(CircleShape)
                                                .background(bgColor)
                                                .clickable {
                                                    currentQuestionIndex = index
                                                    showPaletteDrawer = false
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$qNo",
                                                color = textColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Color legend
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(12.dp).background(Color(0xFF4CAF50), CircleShape))
                                    Text("Answered", fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(modifier = Modifier.size(12.dp).background(Color(0xFF9C27B0), CircleShape))
                                    Text("Ans+Marked", fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(modifier = Modifier.size(12.dp).background(Color(0xFFFF9800), CircleShape))
                                    Text("Marked", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // Proctor Warning Alert Dialog
                if (showProctorWarningDialog) {
                    AlertDialog(
                        onDismissRequest = { showProctorWarningDialog = false },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Proctor Security Alert", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        text = {
                            Text("A window focus loss or screen navigation violation has been detected. This event has been logged.\n\nWarnings: $cheatWarnings / 3. \nReaching 3 warnings auto-submits the exam.")
                        },
                        confirmButton = {
                            Button(onClick = { showProctorWarningDialog = false }) {
                                Text("Acknowledge & Return")
                            }
                        }
                    )
                }

                // Exit Confirmation Dialog
                if (showExitConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showExitConfirmation = false },
                        title = { Text("Exit Mock Test?") },
                        text = { Text("Your ongoing attempt details and progress on this examination will be discarded and cannot be retrieved. Are you sure you want to exit?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showExitConfirmation = false
                                    onBack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Discard & Exit")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showExitConfirmation = false }) {
                                Text("Resume Test")
                            }
                        }
                    )
                }

                // Submit Confirmation Dialog
                if (showSubmitConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showSubmitConfirmation = false },
                        title = { Text("Submit Examination?") },
                        text = {
                            val answeredCount = answers.size
                            val unattemptedCount = test.totalQuestions - answeredCount
                            Text("You have answered $answeredCount questions and left $unattemptedCount questions unattempted. Are you sure you want to finish and submit the test?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showSubmitConfirmation = false
                                    val record = MockDatabase.gradeTest(test, answers.toMap(), "ONLINE")
                                    onNavigate(OMRResult(record.id))
                                }
                            ) {
                                Text("Yes, Submit")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSubmitConfirmation = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}
