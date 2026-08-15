package com.example.omrtestportal

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.omrtestportal.ui.main.MainScreen
import com.example.omrtestportal.ui.screens.*

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(
            onNavigate = { navKey -> backStack.add(navKey) }
          )
        }
        entry<ExamsList> {
          ExamsListScreen(
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<PDFNotesList> {
          PDFNotesScreen(
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<TestSeriesCatalog> {
          TestSeriesCatalogScreen(
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<TestSeriesDetails> { key ->
          TestSeriesDetailsScreen(
            seriesId = key.seriesId,
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<OnlineTestPlayer> { key ->
          OnlineTestScreen(
            testId = key.testId,
            onNavigate = { navKey ->
              // When starting result screen, pop player
              backStack.removeLastOrNull()
              backStack.add(navKey)
            },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<OMRScanPrep> { key ->
          OMRScanPrepScreen(
            testId = key.testId,
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<OMRScanner> { key ->
          OMRScannerScreen(
            testId = key.testId,
            onNavigate = { navKey ->
              // Pop scan prep & scanner, show result
              backStack.removeLastOrNull() // pop scanner
              backStack.removeLastOrNull() // pop scan prep
              backStack.add(navKey)
            },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<OMRResult> { key ->
          OMRResultScreen(
            attemptId = key.attemptId,
            onNavigate = { navKey ->
              if (navKey == Main) {
                // Return to home completely
                while (backStack.size > 1) {
                  backStack.removeLastOrNull()
                }
              } else {
                backStack.add(navKey)
              }
            },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<ProfileHistory> {
          ProfileHistoryScreen(
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
      },
  )
}
