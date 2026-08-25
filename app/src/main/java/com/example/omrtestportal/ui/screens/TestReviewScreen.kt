package com.example.omrtestportal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.shared.model.AttemptRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestReviewScreen(
    attemptId: String,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val record = remember(attemptId) {
        MockDatabase.attemptHistory.firstOrNull { it.id == attemptId }
    }

    if (record == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Attempt record not found.")
        }
        return
    }

    val test = remember(record.testId) {
        MockDatabase.testSeries.flatMap { it.tests }.firstOrNull { it.id == record.testId }
    }

    val totalQ = record.totalMarks.toInt()
    val isPedagogy = record.testTitle.contains("Pedagogy", ignoreCase = true) || 
                     record.testTitle.contains("TET", ignoreCase = true) || 
                     record.testTitle.contains("NET", ignoreCase = true)

    val doubleMarkedCount = remember(record) {
        record.bubbleMap.values.count { it == "MULTIPLE" }
    }
    var showPaperSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Review") },
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
            // Stats Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = record.testTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReviewChip(label = "Correct", count = record.correctAnswers, color = Color(0xFF4CAF50), modifier = Modifier.weight(1f))
                        ReviewChip(label = "Wrong", count = record.incorrectAnswers, color = Color(0xFFE53935), modifier = Modifier.weight(1f))
                        ReviewChip(label = "Skipped", count = record.skippedAnswers, color = Color.Gray, modifier = Modifier.weight(1f))
                        ReviewChip(label = "Double", count = doubleMarkedCount, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showPaperSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Question Paper", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(totalQ) { index ->
                    val qNum = index + 1
                    val correctAns = test?.answerKey?.get(qNum) ?: "A"
                    val submitted = record.bubbleMap[qNum] ?: ""

                    QuestionReviewCard(
                        qNum = qNum,
                        correctAns = correctAns,
                        submitted = submitted,
                        isPedagogy = isPedagogy
                    )
                }
            }
            
            if (showPaperSheet && test != null) {
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
                            "Which of the following is a primary agent of socialization?" to listOf("Family", "School", "Peer Group", "Media"),
                            "According to Jean Piaget, in which stage of cognitive development do children develop object permanence?" to listOf("Sensorimotor Stage", "Preoperational Stage", "Concrete Operational Stage", "Formal Operational Stage"),
                            "Vygotsky's concept of the 'Zone of Proximal Development' emphasizes:" to listOf("Cooperative learning", "Rote memorization", "Social interaction scaffolding", "Independent exploration"),
                            "Which learning style learns best through hands-on activities and physical motion?" to listOf("Visual learning", "Auditory learning", "Kinesthetic learning", "Read/Write learning"),
                            "Formative assessment is primarily used for?" to listOf("Grading students", "Comparing students", "Improving instruction and learning", "Conducting final examinations")
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
}

@Composable
fun ReviewChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = borderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = color)
        }
    }
}

private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = 
    androidx.compose.foundation.BorderStroke(width, color)

@Composable
fun QuestionReviewCard(
    qNum: Int,
    correctAns: String,
    submitted: String,
    isPedagogy: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val mockPedagogyQuestions = listOf(
        "Which of the following is a primary agent of socialization?",
        "According to Jean Piaget, in which stage of cognitive development do children develop object permanence?",
        "Vygotsky's concept of the 'Zone of Proximal Development' emphasizes:",
        "Which learning style learns best through hands-on activities and physical motion?",
        "Formative assessment is primarily used for:",
        "A teacher who believes in progressive education should prioritize:",
        "The concept of Multiple Intelligences was proposed by:",
        "Inclusive education refers to:",
        "Which of the following defines intrinsic motivation?",
        "When students encounter a cognitive conflict, they try to resolve it through:"
    )

    val mockGeneralQuestions = listOf(
        "Which layer of the atmosphere contains the ozone layer?",
        "The concept of 'Fundamental Rights' in the Indian Constitution was inspired by:",
        "Which planet in our solar system is known as the Red Planet?",
        "Photosynthesis in plants primarily takes place in which cell organelle?",
        "The standard unit of electrical resistance is:",
        "Which of the following is a renewable source of energy?",
        "The Great Barrier Reef is situated off the coast of which country?",
        "What is the chemical symbol for gold?",
        "Who was the author of the historical work 'Discovery of India'?",
        "The first session of the Indian National Congress was held in 1885 at:"
    )

    val questionBank = if (isPedagogy) mockPedagogyQuestions else mockGeneralQuestions
    val rawQuestionText = questionBank[(qNum - 1) % questionBank.size]
    val qText = "$rawQuestionText (Ref: Section Q-$qNum)"

    // Deciding badge state
    val (statusLabel, statusColor) = when {
        submitted == "MULTIPLE" -> "Double Marked" to MaterialTheme.colorScheme.tertiary
        submitted.isEmpty() || submitted == "None" -> "Skipped" to Color.Gray
        submitted.equals(correctAns, ignoreCase = true) -> "Correct" to Color(0xFF4CAF50)
        else -> "Incorrect" to Color(0xFFE53935)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $qNum",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.1f),
                    border = borderStroke(0.5.dp, statusColor)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Question prompt text
            Text(
                text = qText,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Options List
            val options = listOf("A", "B", "C", "D")
            options.forEach { opt ->
                val isCorrect = opt.equals(correctAns, ignoreCase = true)
                val isSelected = opt.equals(submitted, ignoreCase = true)
                val isDouble = submitted == "MULTIPLE"

                val itemColor = when {
                    isCorrect -> Color(0xFF4CAF50)
                    isSelected -> Color(0xFFE53935)
                    isDouble -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                }

                val itemBg = when {
                    isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.05f)
                    isSelected -> Color(0xFFE53935).copy(alpha = 0.05f)
                    isDouble -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                    else -> Color.Transparent
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = itemBg,
                    border = borderStroke(1.dp, itemColor)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Letter bubble
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    if (isCorrect || isSelected || isDouble) itemColor else Color.Transparent,
                                    CircleShape
                                )
                                .border(1.dp, if (isCorrect || isSelected || isDouble) Color.Transparent else itemColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect || isSelected || isDouble) Color.White else itemColor
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        val suffixText = when {
                            isCorrect -> " (Correct Answer)"
                            isSelected && !isCorrect -> " (Your Choice)"
                            else -> ""
                        }
                        Text(
                            text = "Option $opt$suffixText",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isCorrect || isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Solution Expander
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { expanded = !expanded },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Solution & Explanation",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                val explanation = if (isPedagogy) {
                    "Correct Answer: $correctAns\n\nAccording to educational psychology theories, option $correctAns provides the most scientifically supported response. This matches CBSE/NTA standard grading guidelines where the pedagogical outcome maximizes student agency and developmental milestones."
                } else {
                    "Correct Answer: $correctAns\n\nOption $correctAns represents the correct factual standard answer for this question. General knowledge assessments align this verification coordinate with standard NCERT curriculum profiles."
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = explanation,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
