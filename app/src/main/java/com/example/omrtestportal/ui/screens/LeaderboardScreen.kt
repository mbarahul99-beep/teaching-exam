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

data class LeaderboardUser(
    val name: String,
    val score: Double,
    val attempted: Int,
    val group: String,
    val isCurrentUser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Global", "CTET", "UGC NET")
    val filterGroups = listOf("global", "ctet", "ugc-net")

    // Retrieve active user's stats dynamically
    val attempts = MockDatabase.attemptHistory
    val count = attempts.size
    val correctCount = attempts.sumOf { it.correctAnswers }
    val amitXP = (count * 100) + (correctCount * 10) + (if (count > 0) 1200 else 850)

    // Leaderboard dataset
    val mockUsers = remember(amitXP, count) {
        mutableStateListOf(
            LeaderboardUser("Pooja Sharma", 3040.0, 28, "ctet"),
            LeaderboardUser("Rahul Verma", 3280.0, 30, "ugc-net"),
            LeaderboardUser("Siddharth Rao", 2680.0, 25, "ctet"),
            LeaderboardUser("Vikram Malhotra", 2350.0, 22, "ctet"),
            LeaderboardUser("Neha Deshmukh", 2910.0, 27, "ugc-net"),
            LeaderboardUser("Aditya Roy", 2120.0, 20, "ctet"),
            LeaderboardUser("Meera Nair", 2560.0, 24, "ugc-net"),
            LeaderboardUser("Suresh Patil", 1890.0, 18, "ugc-net"),
            LeaderboardUser("Kirti Sen", 1560.0, 15, "ctet"),
            LeaderboardUser(
                name = "Amit Sharma",
                score = amitXP.toDouble(),
                attempted = if (count > 0) count else 12,
                group = "ctet",
                isCurrentUser = true
            )
        )
    }

    // Filter and sort
    val filteredUsers = remember(selectedTab, mockUsers) {
        val groupFilter = filterGroups[selectedTab]
        val list = if (groupFilter == "global") {
            mockUsers.toList()
        } else {
            mockUsers.filter { it.group == groupFilter || it.isCurrentUser }
        }
        list.sortedByDescending { it.score }
    }

    val myUserIndex = filteredUsers.indexOfFirst { it.isCurrentUser }
    val myRank = if (myUserIndex != -1) myUserIndex + 1 else 10

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rankings & Leaderboard") },
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
                // Tab Selection Row
                TabRow(selectedTabIndex = selectedTab) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
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
                        PodiumLayout(filteredUsers.take(3))
                    }

                    // Table items for Ranks 4+
                    if (filteredUsers.size > 3) {
                        items(filteredUsers.drop(3)) { user ->
                            val rank = filteredUsers.indexOf(user) + 1
                            RankRowItem(rank = rank, user = user)
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
                            val userCount = if (count > 0) count else 12
                            Text(
                                text = "Total Score: ${amitXP} XP | $userCount Tests",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    val percentile = Math.round((myRank.toDouble() / filteredUsers.size.toDouble()) * 100)
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
fun PodiumLayout(podiumUsers: List<LeaderboardUser>) {
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
            PodiumCol(user = podiumUsers[1], rank = 2, height = 90.dp, color = Color(0xFFC0C0C0), modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // First Place
        if (podiumUsers.isNotEmpty()) {
            PodiumCol(user = podiumUsers[0], rank = 1, height = 120.dp, color = Color(0xFFFFD700), modifier = Modifier.weight(1.2f))
        }

        // Third Place
        if (podiumUsers.size > 2) {
            PodiumCol(user = podiumUsers[2], rank = 3, height = 75.dp, color = Color(0xFFCD7F32), modifier = Modifier.weight(1f))
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
            text = "${user.score.toInt()} XP",
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
fun RankRowItem(rank: Int, user: LeaderboardUser) {
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
                    Text(
                        text = "${user.attempted} tests attempted",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Text(
                text = "${user.score.toInt()} XP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
