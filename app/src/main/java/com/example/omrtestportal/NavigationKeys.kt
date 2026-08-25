package com.example.omrtestportal

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Serializable
data object ExamsList : NavKey

@Serializable
data object PDFNotesList : NavKey

@Serializable
data object TestSeriesCatalog : NavKey

@Serializable
data class TestSeriesDetails(val seriesId: String) : NavKey

@Serializable
data class OnlineTestPlayer(val testId: String) : NavKey

@Serializable
data class OMRScanPrep(val testId: String) : NavKey

@Serializable
data class OMRScanner(val testId: String) : NavKey

@Serializable
data class OMRResult(val attemptId: String) : NavKey

@Serializable
data object ProfileHistory : NavKey

@Serializable
data class TestReview(val attemptId: String) : NavKey

