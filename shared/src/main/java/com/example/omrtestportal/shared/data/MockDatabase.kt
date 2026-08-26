package com.example.omrtestportal.shared.data

import com.example.omrtestportal.shared.model.*
import java.text.SimpleDateFormat
import java.util.*

object MockDatabase {

    val exams = listOf(
        Exam("ctet", "Central Teacher Eligibility Test", "CTET", "school"),
        Exam("kvs_nvs", "KVS / NVS Recruitment Exam", "KVS/NVS", "domain"),
        Exam("dsssb", "DSSSB Teacher Recruitment", "DSSSB", "location_city"),
        Exam("ugc_net", "University Grants Commission National Eligibility Test", "UGC NET", "school"),
        Exam("emrs", "Eklavya Model Resident School", "EMRS", "home")
    )

    val stateExams = listOf(
        Exam("uptet", "Uttar Pradesh Teacher Eligibility Test", "UP TET", "gavel"),
        Exam("bihar_stet", "Bihar Secondary Teacher Eligibility Test", "BIHAR STET", "school"),
        Exam("reet", "Rajasthan Eligibility Examination for Teacher", "REET", "location_city")
    )

    val pdfNotes = listOf(
        PDFNote("note_his_01", "Ancient Indian History Notes", "History", 2.4, "https://example.com/pdf/ancient_history.pdf"),
        PDFNote("note_his_02", "Modern Freedom Struggle Key Points", "History", 3.1, "https://example.com/pdf/modern_history.pdf"),
        PDFNote("note_geo_01", "Physical Geography of India", "Geography", 4.5, "https://example.com/pdf/physical_geography.pdf"),
        PDFNote("note_geo_02", "Solar System & Earth Movements", "Geography", 1.8, "https://example.com/pdf/solar_system.pdf"),
        PDFNote("note_pol_01", "Indian Constitution & Preamble", "Political Science", 3.0, "https://example.com/pdf/constitution.pdf"),
        PDFNote("note_hin_01", "Hindi Vyakaran (Grammar) Book", "Hindi", 5.2, "https://example.com/pdf/hindi_grammar.pdf"),
        PDFNote("note_mat_01", "Quantitative Aptitude Formulas", "Maths", 2.2, "https://example.com/pdf/math_formulas.pdf"),
        PDFNote("note_sci_01", "Important Physics & Chemistry Laws", "Science", 3.8, "https://example.com/pdf/science_laws.pdf")
    )

    // Generate mock answers for a test
    private fun generateMockAnswerKey(total: Int): Map<Int, String> {
        val options = listOf("A", "B", "C", "D")
        return (1..total).associateWith { options[it % 4] }
    }

    val testSeries = listOf(
        TestSeries(
            id = "ts_neet_demo",
            examId = "ugc_net",
            title = "NEET/JEE 180-Question Mock Test Series",
            description = "Standard full length mock test matching NEET format (180 questions).",
            numberOfTests = 1,
            tests = listOf(
                Test(
                    id = "neet_demo_01",
                    title = "NEET Demo Mock Test - 180 Questions",
                    durationMinutes = 180,
                    totalQuestions = 180,
                    questionPaperUrl = "https://example.com/papers/neet_demo_01.pdf",
                    omrSheetUrl = "https://example.com/omr/sheet_180_bubbles.pdf",
                    answerKey = generateMockAnswerKey(180)
                )
            )
        ),
        TestSeries(
            id = "ts_ctet_pedagogy",
            examId = "ctet",
            title = "CTET Child Development & Pedagogy Mock Tests",
            description = "10 Full length tests dedicated to child psychology and teaching methodology.",
            numberOfTests = 3,
            tests = listOf(
                Test(
                    id = "ctet_ped_01",
                    title = "Child Development and Pedagogy - Test 01",
                    durationMinutes = 30,
                    totalQuestions = 30,
                    questionPaperUrl = "https://example.com/papers/ctet_ped_01.pdf",
                    omrSheetUrl = "https://example.com/omr/sheet_30_bubbles.pdf",
                    answerKey = generateMockAnswerKey(30)
                ),
                Test(
                    id = "ctet_ped_02",
                    title = "Child Development and Pedagogy - Test 02",
                    durationMinutes = 30,
                    totalQuestions = 30,
                    questionPaperUrl = "https://example.com/papers/ctet_ped_02.pdf",
                    omrSheetUrl = "https://example.com/omr/sheet_30_bubbles.pdf",
                    answerKey = generateMockAnswerKey(30)
                ),
                Test(
                    id = "ctet_ped_03",
                    title = "Pedagogy Full length Syllabus - Test 03",
                    durationMinutes = 30,
                    totalQuestions = 30,
                    questionPaperUrl = "https://example.com/papers/ctet_ped_03.pdf",
                    omrSheetUrl = "https://example.com/omr/sheet_30_bubbles.pdf",
                    answerKey = generateMockAnswerKey(30)
                )
            )
        ),
        TestSeries(
            id = "ts_kvs_general",
            examId = "kvs_nvs",
            title = "KVS General Paper Mock Test Series",
            description = "Full length mock tests covering English, Hindi, Reasoning, and Pedagogy.",
            numberOfTests = 2,
            tests = listOf(
                Test(
                    id = "kvs_gen_01",
                    title = "KVS Mock Test 01 (General Aptitude)",
                    durationMinutes = 60,
                    totalQuestions = 50,
                    questionPaperUrl = "https://example.com/papers/kvs_gen_01.pdf",
                    omrSheetUrl = "https://example.com/omr/sheet_50_bubbles.pdf",
                    answerKey = generateMockAnswerKey(50)
                ),
                Test(
                    id = "kvs_gen_02",
                    title = "KVS Mock Test 02 (English & Reasoning)",
                    durationMinutes = 60,
                    totalQuestions = 50,
                    questionPaperUrl = "https://example.com/papers/kvs_gen_02.pdf",
                    omrSheetUrl = "https://example.com/omr/sheet_50_bubbles.pdf",
                    answerKey = generateMockAnswerKey(50)
                )
            )
        )
    )

    val examUpdates = listOf(
        ExamUpdate(
            id = "upd_01",
            title = "CTET December 2026 Application Dates Released",
            content = "The Central Board of Secondary Education (CBSE) has released the online application dates for CTET Dec 2026. The application portal will be live from September 1st, 2026, to October 5th, 2026. Check the syllabus and eligibility before applying.",
            dateString = "2026-08-15",
            linkUrl = "https://ctet.nic.in"
        ),
        ExamUpdate(
            id = "upd_02",
            title = "DSSSB PGT/TGT Exam Schedule Declared",
            content = "Delhi Subordinate Services Selection Board (DSSSB) has announced the exam dates for PGT and TGT examinations scheduled in September and October. Please download your admit cards from the official website 7 days prior to the exam.",
            dateString = "2026-08-14",
            linkUrl = "https://dsssb.delhi.gov.in"
        ),
        ExamUpdate(
            id = "upd_03",
            title = "UPTET 2026 Notification Expected in October",
            content = "Uttar Pradesh Basic Education Board (UPBEB) is expected to publish the official notification for UPTET 2026 by the end of October. Stay tuned for details regarding paper configurations and syllabus.",
            dateString = "2026-08-12"
        )
    )

    val attemptHistory = mutableListOf<AttemptRecord>(
        AttemptRecord(
            id = "att_mock_01",
            testId = "ctet_ped_01",
            testTitle = "Child Development and Pedagogy - Test 01",
            attemptType = "ONLINE",
            dateString = "2026-08-12 14:23",
            marksObtained = 24.0,
            totalMarks = 30.0,
            correctAnswers = 24,
            incorrectAnswers = 6,
            skippedAnswers = 0,
            bubbleMap = (1..24).associateWith { "A" } + (25..30).associateWith { "B" }
        ),
        AttemptRecord(
            id = "att_mock_02",
            testId = "kvs_gen_01",
            testTitle = "KVS Mock Test 01 (General Aptitude)",
            attemptType = "OMR",
            dateString = "2026-08-14 11:05",
            marksObtained = 38.0,
            totalMarks = 50.0,
            correctAnswers = 38,
            incorrectAnswers = 10,
            skippedAnswers = 2,
            bubbleMap = (1..38).associateWith { "A" } + (39..48).associateWith { "B" }
        )
    )

    fun addAttempt(record: AttemptRecord) {
        attemptHistory.add(0, record) // Insert at beginning
    }

    fun gradeTest(test: Test, submittedAnswers: Map<Int, String>, attemptType: String, scannedOmrUrl: String? = null): AttemptRecord {
        var correct = 0
        var incorrect = 0
        var skipped = 0

        for (q in 1..test.totalQuestions) {
            val submitted = submittedAnswers[q]
            val correctAns = test.answerKey[q]
            if (submitted == null || submitted.isEmpty() || submitted == "None") {
                skipped++
            } else if (submitted.uppercase() == correctAns?.uppercase()) {
                correct++
            } else {
                incorrect++
            }
        }

        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateStr = df.format(Date())

        val record = AttemptRecord(
            id = "att_" + UUID.randomUUID().toString().take(8),
            testId = test.id,
            testTitle = test.title,
            attemptType = attemptType,
            dateString = dateStr,
            marksObtained = correct.toDouble(), // Assuming 1 mark per correct answer, 0 for incorrect
            totalMarks = test.totalQuestions.toDouble(),
            correctAnswers = correct,
            incorrectAnswers = incorrect,
            skippedAnswers = skipped,
            bubbleMap = submittedAnswers,
            scannedOmrUrl = scannedOmrUrl
        )

        addAttempt(record)
        return record
    }
}
