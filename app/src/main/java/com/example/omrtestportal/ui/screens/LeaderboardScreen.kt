package com.example.omrtestportal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

data class LeaderboardUser(
    val name: String,
    val score: Double, // Marks obtained
    val group: String,
    val isCurrentUser: Boolean = false,
    val isAttempted: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    testId: String? = null,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val allTests = remember { MockDatabase.testSeries.flatMap { it.tests } }
    var selectedTestId by remember { mutableStateOf(testId ?: (allTests.firstOrNull()?.id ?: "")) }
    val selectedTest = remember(selectedTestId) { allTests.firstOrNull { it.id == selectedTestId } }
    
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Retrieve active user's attempts
    val attempts = MockDatabase.attemptHistory
    val userAttempt = remember(selectedTestId, attempts) {
        attempts.firstOrNull { it.testId == selectedTestId }
    }

    val testTotal = selectedTest?.totalQuestions ?: 30

    // Leaderboard dataset for the selected mock test
    val testRankings = remember(selectedTestId, userAttempt) {
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
        if (userAttempt != null) {
            list.add(LeaderboardUser("Amit Sharma", userAttempt.marksObtained, "ctet", isCurrentUser = true, isAttempted = true))
        } else {
            // Unattempted state showing simulated potential rank
            list.add(LeaderboardUser("Amit Sharma", (testTotal * 0.73).toInt().toDouble(), "ctet", isCurrentUser = true, isAttempted = false))
        }
        list.sortedByDescending { it.score }
    }

    val myUserIndex = testRankings.indexOfFirst { it.isCurrentUser }
    val myRank = if (myUserIndex != -1) myUserIndex + 1 else 10

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mock Test Rankings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Test Selection Dropdown Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedTest?.title ?: "Select Mock Test",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Active Mock Test Rankings") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            allTests.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.title, fontSize = 13.sp) },
                                    onClick = {
                                        selectedTestId = t.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Show warning banner if Amit hasn't taken the test
                if (userAttempt == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
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
                                text = "You have not attempted this test yet. Showing simulated leaderboard.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 76.dp)
                ) {
                    // Podium layout for Ranks 1, 2, 3
                    item {
                        PodiumLayout(testRankings.take(3), testTotal)
                    }

                    // Table items for Ranks 4+
                    if (testRankings.size > 3) {
                        items(testRankings.drop(3)) { user ->
                            val rank = testRankings.indexOf(user) + 1
                            RankRowItem(rank = rank, user = user, totalQuestions = testTotal)
                        }
                    }
                }
            }

            // Sticky bottom "My Position" card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(72.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AS",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Amit Sharma (Rank #$myRank)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            val scoreStr = if (userAttempt != null) {
                                "${userAttempt.marksObtained.toInt()} / $testTotal Marks"
                            } else {
                                "Not Attempted"
                            }
                            Text(
                                text = scoreStr,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    val percentile = Math.round(((testRankings.size - myRank + 1).toDouble() / testRankings.size.toDouble()) * 100)
                    Text(
                        text = "Top $percentile%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

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
