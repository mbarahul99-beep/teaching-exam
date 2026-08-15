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
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.omrtestportal.OMRResult
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.shared.model.Test
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

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

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<Int, String>() }
    val markedForReview = remember { mutableStateMapOf<Int, Boolean>() }

    // Countdown Timer in seconds
    var secondsRemaining by remember { mutableStateOf(test.durationMinutes * 60) }
    var showPaletteDrawer by remember { mutableStateOf(false) }
    var showSubmitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        // Auto-submit when time expires
        val record = MockDatabase.gradeTest(test, answers.toMap(), "ONLINE")
        onNavigate(OMRResult(record.id))
    }

    val formatTime = remember(secondsRemaining) {
        val mins = secondsRemaining / 60
        val secs = secondsRemaining % 60
        String.format("%02d:%02d", mins, secs)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(test.title, fontSize = 15.sp, maxLines = 1, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Time Left: $formatTime",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (secondsRemaining < 300) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Confirm exit dialog could go here
                        onBack()
                    }) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header (Question number and bookmark/review)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question $qNum of ${test.totalQuestions}",
                        fontSize = 18.sp,
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

                Spacer(modifier = Modifier.height(16.dp))

                // Question Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = getMockQuestionBody(test.title, qNum),
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Options (A, B, C, D)
                val options = listOf("A", "B", "C", "D")
                val mockOptionTexts = getMockOptionTexts(test.title, qNum)
                options.forEachIndexed { index, option ->
                    val isSelected = answers[qNum] == option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
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
                                .padding(14.dp),
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
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = mockOptionTexts[index],
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
                                    
                                    val bgColor = when {
                                        isMarked -> Color(0xFFE040FB) // Magenta
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
                        }
                    }
                }
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

private fun getMockQuestionBody(testTitle: String, qNum: Int): String {
    return when (qNum % 3) {
        0 -> "Which of the following is considered a primary agent of socialization for young children, especially during early childhood?"
        1 -> "In the context of cognitive development, which stage of Jean Piaget's theory matches with the ability to perform conservation tasks and logical classification operations?"
        else -> "A teacher designs classroom tasks that require collaborative peer-to-peer dialogues and scaffolding. This pedagogical strategy is most strongly aligned with which learning theorist?"
    }
}

private fun getMockOptionTexts(testTitle: String, qNum: Int): List<String> {
    return when (qNum % 3) {
        0 -> listOf(
            "Mass media and community networks",
            "Family and immediate caregivers",
            "Formal school curriculum",
            "Peer groups and extra-curricular clubs"
        )
        1 -> listOf(
            "Sensorimotor stage (0 to 2 years)",
            "Pre-operational stage (2 to 7 years)",
            "Concrete operational stage (7 to 11 years)",
            "Formal operational stage (11 years and above)"
        )
        else -> listOf(
            "B.F. Skinner (Behaviorism)",
            "Lev Vygotsky (Social Constructivism)",
            "Jean Piaget (Cognitive Constructivism)",
            "Albert Bandura (Social Learning Theory)"
        )
    }
}
