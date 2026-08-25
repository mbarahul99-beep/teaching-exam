package com.example.omrtestportal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
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
    val maxScore = totalQ * 4f
    val studentScore = record.correctAnswers * 4f - record.incorrectAnswers * 1f

    val accuracyPct = remember(record, totalQ) {
        val attempted = record.correctAnswers + record.incorrectAnswers
        if (attempted > 0) (record.correctAnswers.toFloat() / attempted) * 100f else 0f
    }

    val doubleMarkedCount = remember(record) {
        record.bubbleMap.values.count { it == "MULTIPLE" }
    }

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "CORRECT", "INCORRECT", "SKIPPED"
    var showPaperSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Scorecard") },
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
            // Main Dashboard stats card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = record.testTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Accuracy Gauge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { accuracyPct / 100f },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF4CAF50),
                                strokeWidth = 8.dp,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = String.format("%.0f%%", accuracyPct),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Accuracy",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Score metrics
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Score Obtained:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                Text("${studentScore.toInt()} / ${maxScore.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Percentile Rank:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                Text("94.2% (Top 5.8%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                ReviewChip(label = "Correct", count = record.correctAnswers, color = Color(0xFF4CAF50), modifier = Modifier.weight(1f))
                                ReviewChip(label = "Wrong", count = record.incorrectAnswers, color = Color(0xFFE53935), modifier = Modifier.weight(1f))
                                ReviewChip(label = "Skipped", count = record.skippedAnswers, color = Color.Gray, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Score Comparison Chart Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Class Performance Comparison", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))

                    ComparisonBar(label = "Your Score", score = studentScore, maxScore = maxScore, color = Color(0xFF4CAF50))
                    ComparisonBar(label = "Cutoff Score", score = maxScore * 0.5f, maxScore = maxScore, color = Color(0xFFFFB300))
                    ComparisonBar(label = "Topper Score", score = maxScore * 0.93f, maxScore = maxScore, color = Color(0xFF1E88E5))
                    ComparisonBar(label = "Class Average", score = maxScore * 0.61f, maxScore = maxScore, color = Color(0xFF8E24AA))
                }
            }

            // Filter Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All (${totalQ})",
                    "CORRECT" to "Correct (${record.correctAnswers})",
                    "INCORRECT" to "Incorrect (${record.incorrectAnswers})",
                    "SKIPPED" to "Skipped (${record.skippedAnswers})"
                )
                filters.forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Graded Questions List
            val filteredList = remember(selectedFilter, record, totalQ) {
                (1..totalQ).filter { qNum ->
                    val correctAns = test?.answerKey?.get(qNum) ?: "A"
                    val submitted = record.bubbleMap[qNum] ?: ""
                    when (selectedFilter) {
                        "CORRECT" -> submitted.equals(correctAns, ignoreCase = true)
                        "INCORRECT" -> !submitted.isEmpty() && submitted != "None" && !submitted.equals(correctAns, ignoreCase = true)
                        "SKIPPED" -> submitted.isEmpty() || submitted == "None"
                        else -> true
                    }
                }
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No questions matches this filter.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList.size) { index ->
                        val qNum = filteredList[index]
                        val correctAns = test?.answerKey?.get(qNum) ?: "A"
                        val submitted = record.bubbleMap[qNum] ?: ""

                        QuestionReviewCard(
                            qNum = qNum,
                            correctAns = correctAns,
                            submitted = submitted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonBar(label: String, score: Float, maxScore: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(text = String.format("%.0f / %.0f", score, maxScore), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (score / maxScore).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
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
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$count", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Medium, color = color)
        }
    }
}

@Composable
fun QuestionReviewCard(
    qNum: Int,
    correctAns: String,
    submitted: String
) {
    var expanded by remember { mutableStateOf(false) }

    val (qText, qOptions) = remember(qNum, correctAns) {
        getDynamicQuestion(qNum, correctAns)
    }

    val (statusLabel, statusColor) = when {
        submitted == "MULTIPLE" -> "Double Marked" to MaterialTheme.colorScheme.tertiary
        submitted.isEmpty() || submitted == "None" -> "Skipped" to Color.Gray
        submitted.equals(correctAns, ignoreCase = true) -> "Correct" to Color(0xFF4CAF50)
        else -> "Incorrect" to Color(0xFFE53935)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $qNum",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.1f),
                    border = BorderStroke(0.5.dp, statusColor)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Question prompt body
            Text(
                text = qText,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Render all choices (A, B, C, D)
            val options = listOf("A", "B", "C", "D")
            options.forEachIndexed { index, opt ->
                val isCorrect = opt.equals(correctAns, ignoreCase = true)
                val isSelected = opt.equals(submitted, ignoreCase = true)
                val isDouble = submitted == "MULTIPLE"

                val itemColor = when {
                    isCorrect -> Color(0xFF4CAF50)
                    isSelected -> Color(0xFFE53935)
                    isDouble -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }

                val itemBg = when {
                    isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.06f)
                    isSelected -> Color(0xFFE53935).copy(alpha = 0.06f)
                    isDouble -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f)
                    else -> Color.Transparent
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = itemBg,
                    border = BorderStroke(0.8.dp, itemColor)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                        Spacer(modifier = Modifier.width(10.dp))
                        val suffixText = when {
                            isCorrect -> " (Correct Answer)"
                            isSelected && !isCorrect -> " (Your Choice)"
                            else -> ""
                        }
                        Text(
                            text = qOptions[index] + suffixText,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isCorrect || isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Explanation drawer trigger
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { expanded = !expanded },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Detailed Explanation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                val explanation = remember(qNum) {
                    getDynamicExplanation(qNum)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Explanation:\n$explanation",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
