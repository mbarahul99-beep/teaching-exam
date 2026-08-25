package com.example.omrtestportal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.omrtestportal.OMRResult
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.shared.model.AttemptRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHistoryScreen(
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val attempts = MockDatabase.attemptHistory
    val totalTests = attempts.size
    val averageScorePct = if (attempts.isNotEmpty()) {
        attempts.map { (it.marksObtained / it.totalMarks) * 100 }.average()
    } else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Progress") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Bio Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar Initial Box
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AS",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Amit Sharma",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "amit.sharma@example.com",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Stats breakdown
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total Attempts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$totalTests", fontSize = 24.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Average Score", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(String.format("%.1f%%", averageScorePct), fontSize = 24.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            item {
                ProfileAnalyticsSection(attempts = attempts)
            }

            // Attempt History Title
            item {
                Text(
                    text = "Attempt History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Attempt List Items
            if (attempts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tests attempted yet.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            } else {
                items(attempts) { record ->
                    AttemptHistoryItem(
                        record = record,
                        onClick = { onNavigate(OMRResult(record.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun AttemptHistoryItem(
    record: AttemptRecord,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.testTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = record.dateString,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    
                    // Attempt type badge (Online/OMR)
                    val badgeColor = if (record.attemptType == "OMR") Color(0xFF9C27B0) else Color(0xFF009688)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = record.attemptType,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${record.marksObtained.toInt()}/${record.totalMarks.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "View Score",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProfileAnalyticsSection(attempts: List<AttemptRecord>) {
    val trendData = remember(attempts) {
        attempts.reversed().takeLast(5)
    }
    
    // Scores percentages
    val scores = remember(trendData) {
        if (trendData.isNotEmpty()) {
            trendData.map { record: AttemptRecord -> (record.marksObtained / record.totalMarks) * 100 }
        } else {
            listOf(60.0, 70.0, 65.0, 80.0, 75.0) // mock defaults
        }
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Performance Analytics",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Line Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Score Trend (Attempts Progress)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Line chart drawing
                val primaryColor = MaterialTheme.colorScheme.primary
                val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val padding = 30.dp.toPx()
                    val usableW = w - 2 * padding
                    val usableH = h - 2 * padding
                    
                    // Draw horizontal grid lines
                    val ticks = listOf(0f, 25f, 50f, 75f, 100f)
                    ticks.forEach { tick ->
                        val y = padding + usableH - (tick / 100f) * usableH
                        drawLine(
                            color = outlineColor,
                            start = Offset(padding, y),
                            end = Offset(w - padding, y),
                            strokeWidth = 1f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
                    
                    // Coordinates mapping
                    val xStep = if (scores.size > 1) usableW / (scores.size - 1) else usableW
                    val coords = scores.mapIndexed { idx: Int, pct: Double ->
                        val x = padding + idx * xStep
                        val y = padding + usableH - (pct.toFloat() / 100f) * usableH
                        Offset(x, y)
                    }
                    
                    // Draw Line Path
                    if (coords.isNotEmpty()) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(coords[0].x, coords[0].y)
                            for (i in 1 until coords.size) {
                                lineTo(coords[i].x, coords[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )
                    }
                    
                    // Draw Points
                    coords.forEach { pt: Offset ->
                        drawCircle(
                            color = primaryColor,
                            center = pt,
                            radius = 5.dp.toPx()
                        )
                        drawCircle(
                            color = Color.White,
                            center = pt,
                            radius = 2.5.dp.toPx()
                        )
                    }
                }
            }
        }

        // Horizontal Bar Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Subject Accuracy (%)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                val subjects = listOf(
                    "Pedagogy" to 84,
                    "Hindi" to 78,
                    "English" to 68,
                    "Maths" to 72,
                    "Science" to 65
                )

                subjects.forEach { (name, accuracy) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(80.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(accuracy / 100f)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$accuracy%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
