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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.omrtestportal.shared.data.MockDatabase
import com.example.omrtestportal.OnlineTestPlayer
import com.example.omrtestportal.OMRScanPrep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDetailsScreen(
    testId: String,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val allTests = remember { MockDatabase.testSeries.flatMap { it.tests } }
    val test = remember(testId) { allTests.firstOrNull { it.id == testId } }

    if (test == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Mock Test not found.")
        }
        return
    }

    val attempts = MockDatabase.attemptHistory
    val testAttempts = remember(testId, attempts) {
        attempts.filter { it.testId == testId }
    }
    val hasAttempted = testAttempts.isNotEmpty()
    val latestAttempt = testAttempts.firstOrNull()

    val testTotal = test.totalQuestions
    val cutoffMarks = (testTotal * 0.70).toInt() // 70% expected cutoff

    // Simulated Rankings for this specific test
    val testRankings = remember(testId, latestAttempt) {
        val list = mutableListOf(
            LeaderboardUser("Pooja Sharma", (testTotal * 0.96).toInt().toDouble(), "ctet"),
            LeaderboardUser("Rahul Verma", (testTotal * 0.93).toInt().toDouble(), "ugc-net"),
            LeaderboardUser("Siddharth Rao", (testTotal * 0.90).toInt().toDouble(), "ctet"),
            LeaderboardUser("Vikram Malhotra", (testTotal * 0.86).toInt().toDouble(), "ctet"),
            LeaderboardUser("Neha Deshmukh", (testTotal * 0.83).toInt().toDouble(), "ugc-net"),
            LeaderboardUser("Aditya Roy", (testTotal * 0.80).toInt().toDouble(), "ctet"),
            LeaderboardUser("Meera Nair", (testTotal * 0.76).toInt().toDouble(), "ugc-net"),
            LeaderboardUser("Suresh Patil", (testTotal * 0.70).toInt().toDouble(), "ugc-net"),
            LeaderboardUser("Kirti Sen", (testTotal * 0.63).toInt().toDouble(), "ctet")
        )
        if (latestAttempt != null) {
            list.add(LeaderboardUser("Amit Sharma", latestAttempt.marksObtained, "ctet", isCurrentUser = true, isAttempted = true))
        } else {
            list.add(LeaderboardUser("Amit Sharma", (testTotal * 0.73).toInt().toDouble(), "ctet", isCurrentUser = true, isAttempted = false))
        }
        list.sortedByDescending { it.score }
    }

    val myUserIndex = testRankings.indexOfFirst { it.isCurrentUser }
    val myRank = if (myUserIndex != -1) myUserIndex + 1 else 10

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mock Test Details") },
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
            // 1. Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = test.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Syllabus Covered: Child Psychology, Developmental Stages, Pedagogy Principles, Hindi grammar concepts and Comprehension skills.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailStatItem("Duration", "${test.durationMinutes} min")
                            DetailStatItem("Questions", "$testTotal Qs")
                            DetailStatItem("Total Marks", "$testTotal Marks")
                            DetailStatItem("Cutoff", "$cutoffMarks Marks")
                        }
                    }
                }
            }

            // 2. Start Exam Options
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Attempt Preparation Options",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onNavigate(OMRScanPrep(testId)) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Attempt OMR", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { onNavigate(OnlineTestPlayer(testId)) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Attempt Online", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. Past Attempts List for this Specific Test
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Attempts History",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (testAttempts.isEmpty()) {
                            Text(
                                text = "You haven't attempted this mock test yet. Take a test to view your scorecard log.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        } else {
                            testAttempts.forEachIndexed { index, attempt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Attempt #${testAttempts.size - index} (${attempt.attemptType})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = attempt.dateString,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    Text(
                                        text = "${attempt.marksObtained.toInt()} / ${attempt.totalMarks.toInt()} Marks",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (attempt.marksObtained >= cutoffMarks) Color(0xFF4CAF50) else Color(0xFFE53935)
                                    )
                                }
                                if (index < testAttempts.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                }
                            }
                        }
                    }
                }
            }

            // 4. Rankings Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Test Rankings & Leaderboard",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Warnings if Amit has not attempted yet
            if (latestAttempt == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Take this test to calculate your official leaderboard percentile.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Top Podium
            item {
                PodiumLayout(testRankings.take(3), testTotal)
            }

            // Other Rankers
            if (testRankings.size > 3) {
                items(testRankings.drop(3)) { user ->
                    val rank = testRankings.indexOf(user) + 1
                    RankRowItem(rank = rank, user = user, totalQuestions = testTotal)
                }
            }
        }
    }
}

@Composable
fun DetailStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class LeaderboardUser(
    val name: String,
    val score: Double,
    val group: String,
    val isCurrentUser: Boolean = false,
    val isAttempted: Boolean = true
)

@Composable
fun PodiumLayout(podiumUsers: List<LeaderboardUser>, totalQuestions: Int) {
    if (podiumUsers.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // Second Place
        if (podiumUsers.size > 1) {
            PodiumCol(user = podiumUsers[1], rank = 2, height = 90.dp, color = Color(0xFFC0C0C0), totalQuestions = totalQuestions, modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // First Place
        if (podiumUsers.isNotEmpty()) {
            PodiumCol(user = podiumUsers[0], rank = 1, height = 120.dp, color = Color(0xFFFFD700), totalQuestions = totalQuestions, modifier = Modifier.weight(1.2f))
        }

        // Third Place
        if (podiumUsers.size > 2) {
            PodiumCol(user = podiumUsers[2], rank = 3, height = 75.dp, color = Color(0xFFCD7F32), totalQuestions = totalQuestions, modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PodiumCol(
    user: LeaderboardUser,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    val initials = user.name.split(" ").map { it[0] }.joinToString("")
    val highlightBorder = if (user.isCurrentUser) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            if (rank == 1) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier
                        .size(20.dp)
                        .offset(y = (-14).dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(if (rank == 1) 64.dp else 52.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .border(2.dp, color, CircleShape)
                    .then(highlightBorder),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (rank == 1) 18.sp else 15.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(80.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "${user.score.toInt()} / $totalQuestions",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = color.copy(alpha = 0.8f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$rank",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RankRowItem(rank: Int, user: LeaderboardUser, totalQuestions: Int) {
    val initials = user.name.split(" ").map { it[0] }.joinToString("")
    val cardBg = if (user.isCurrentUser) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderMod = if (user.isCurrentUser) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
    } else {
        Modifier
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .then(borderMod),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.width(28.dp)
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.name + if (user.isCurrentUser) " (You)" else "",
                        fontSize = 13.sp,
                        fontWeight = if (user.isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val statusText = if (user.isCurrentUser && !user.isAttempted) {
                        "Not Attempted"
                    } else {
                        "Attempted"
                    }
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Text(
                text = "${user.score.toInt()} / $totalQuestions",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
