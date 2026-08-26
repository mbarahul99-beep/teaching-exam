package com.example.omrtestportal.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val id: String,
    val title: String,
    val shortName: String,
    val iconName: String
)

@Serializable
data class PDFNote(
    val id: String,
    val title: String,
    val subject: String,
    val sizeMb: Double,
    val pdfUrl: String,
    val isDownloaded: Boolean = false
)

@Serializable
data class Test(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val questionPaperUrl: String,
    val omrSheetUrl: String,
    val totalMarks: Double = totalQuestions.toDouble(),
    val answerKey: Map<Int, String>
)

@Serializable
data class TestSeries(
    val id: String,
    val examId: String,
    val title: String,
    val description: String,
    val numberOfTests: Int,
    val tests: List<Test>
)

@Serializable
data class ExamUpdate(
    val id: String,
    val title: String,
    val content: String,
    val dateString: String,
    val linkUrl: String? = null
)

@Serializable
data class AttemptRecord(
    val id: String,
    val testId: String,
    val testTitle: String,
    val attemptType: String, // "ONLINE" or "OMR"
    val dateString: String,
    val marksObtained: Double,
    val totalMarks: Double,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val skippedAnswers: Int,
    val bubbleMap: Map<Int, String>,
    val scannedOmrUrl: String? = null
)
