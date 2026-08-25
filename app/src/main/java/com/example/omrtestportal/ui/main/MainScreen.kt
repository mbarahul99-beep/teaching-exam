package com.example.omrtestportal.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.omrtestportal.*
import com.example.omrtestportal.ui.screens.HomeScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Exams & OMR Portal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Version 1.0.0",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(12.dp))

                // Drawer items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Book, contentDescription = null) },
                    label = { Text("PDF Notes") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate(PDFNotesList)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    label = { Text("Test Series") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate(TestSeriesCatalog)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Leaderboard") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate(Leaderboard)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Attempt History") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate(ProfileHistory)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "OMR Prep Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigate(ProfileHistory) }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(28.dp))
                        }
                    }
                )
            },
            bottomBar = {
                // Sticky bottom footer
                NavigationBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) },
                        selected = true,
                        onClick = { /* Already on Home */ }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.School, contentDescription = "Exams") },
                        label = { Text("Exams", fontSize = 11.sp) },
                        selected = false,
                        onClick = { onNavigate(ExamsList) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Description, contentDescription = "PDF Notes") },
                        label = { Text("Notes", fontSize = 11.sp) },
                        selected = false,
                        onClick = { onNavigate(PDFNotesList) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp) },
                        selected = false,
                        onClick = { onNavigate(ProfileHistory) }
                    )
                }
            }
        ) { innerPadding ->
            HomeScreen(
                onNavigate = onNavigate,
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}
