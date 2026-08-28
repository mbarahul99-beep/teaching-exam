package com.example.omrtestportal.shared.data

import com.example.omrtestportal.shared.model.*
import java.text.SimpleDateFormat
import java.util.*

object MockDatabase {

    val exams = listOf(
        Exam("neet", "National Eligibility cum Entrance Test", "NEET", "local_hospital"),
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
        // Paper 1 Notes
        PDFNote("note_ctet_p1_evs_c3", "Class 3 EVS NCERT Summary", "EVS", 2.4, "https://example.com/pdf/ctet_p1_evs_c3.pdf", paper = "Paper 1", noteType = "NCERT", classLevel = "Class 3"),
        PDFNote("note_ctet_p1_evs_c4", "Class 4 EVS NCERT Summary", "EVS", 3.1, "https://example.com/pdf/ctet_p1_evs_c4.pdf", paper = "Paper 1", noteType = "NCERT", classLevel = "Class 4"),
        PDFNote("note_ctet_p1_evs_c5", "Class 5 EVS NCERT Summary", "EVS", 4.5, "https://example.com/pdf/ctet_p1_evs_c5.pdf", paper = "Paper 1", noteType = "NCERT", classLevel = "Class 5"),
        PDFNote("note_ctet_p1_maths_c3", "Class 3 Maths Primary Notes", "Mathematics", 1.8, "https://example.com/pdf/ctet_p1_maths_c3.pdf", paper = "Paper 1", noteType = "NCERT", classLevel = "Class 3"),
        
        // Paper 2 Notes
        PDFNote("note_ctet_p2_sst_c6", "Class 6 SST Our Pasts History", "Social Science", 3.0, "https://example.com/pdf/ctet_p2_sst_c6.pdf", paper = "Paper 2", noteType = "NCERT", classLevel = "Class 6"),
        PDFNote("note_ctet_p2_sci_c7", "Class 7 Science NCERT Summary", "Science", 5.2, "https://example.com/pdf/ctet_p2_sci_c7.pdf", paper = "Paper 2", noteType = "NCERT", classLevel = "Class 7"),
        PDFNote("note_ctet_p2_math_c8", "Class 8 Maths NCERT Solutions", "Mathematics", 2.2, "https://example.com/pdf/ctet_p2_math_c8.pdf", paper = "Paper 2", noteType = "NCERT", classLevel = "Class 8"),
        PDFNote("note_ctet_p2_math_c6", "Class 6 Maths NCERT Formulas", "Mathematics", 1.5, "https://example.com/pdf/ctet_p2_math_c6.pdf", paper = "Paper 2", noteType = "NCERT", classLevel = "Class 6"),

        // Both / Core Subject Theory Notes
        PDFNote("note_ctet_cdp_core", "CDP Theories: Piaget & Vygotsky", "CDP", 3.8, "https://example.com/pdf/ctet_cdp_core.pdf", paper = "Both", noteType = "Subject Theory"),
        PDFNote("note_ctet_eng_ped", "English Pedagogy teaching methods", "English", 2.1, "https://example.com/pdf/ctet_eng_ped.pdf", paper = "Both", noteType = "Subject Theory"),

        // Legacy / Other Exam Notes
        PDFNote("note_his_01", "Ancient Indian History Notes", "History", 2.4, "https://example.com/pdf/ancient_history.pdf", paper = "Both", noteType = "Subject Theory"),
        PDFNote("note_his_02", "Modern Freedom Struggle Key Points", "History", 3.1, "https://example.com/pdf/modern_history.pdf", paper = "Both", noteType = "Subject Theory"),
        PDFNote("note_geo_01", "Physical Geography of India", "Geography", 4.5, "https://example.com/pdf/physical_geography.pdf", paper = "Both", noteType = "Subject Theory"),
        PDFNote("note_pol_01", "Indian Constitution & Preamble", "Political Science", 3.0, "https://example.com/pdf/constitution.pdf", paper = "Both", noteType = "Subject Theory")
    )

    // Generate mock answers for a test
    private fun generateMockAnswerKey(total: Int): Map<Int, String> {
        val options = listOf("A", "B", "C", "D")
        return (1..total).associateWith { options[it % 4] }
    }

    val testSeries = listOf(
        TestSeries(
            id = "ts_neet_demo",
            examId = "neet",
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
                    answerKey = generateMockAnswerKey(180),
                    testType = "Full Syllabus"
                )
            )
        ),
        TestSeries(
            id = "ts_ctet_series",
            examId = "ctet",
            title = "CTET Complete Test Series (Paper 1 & 2)",
            description = "Syllabus mock tests and solved past papers.",
            numberOfTests = 12,
            tests = listOf(
                // Mocks Paper 1
                Test("ctet_m_p1_cdp", "Primary CDP Mock Test - 1", 30, 30, "https://example.com/p1_cdp.pdf", "https://example.com/omr_30.pdf", answerKey = generateMockAnswerKey(30), paper = "Paper 1", testType = "Subject-wise", subject = "CDP"),
                Test("ctet_m_p1_evs", "Primary EVS Mock Test - 1", 30, 30, "https://example.com/p1_evs.pdf", "https://example.com/omr_30.pdf", answerKey = generateMockAnswerKey(30), paper = "Paper 1", testType = "Subject-wise", subject = "EVS"),
                Test("ctet_m_p1_maths", "Primary Mathematics Mock - 1", 30, 30, "https://example.com/p1_maths.pdf", "https://example.com/omr_30.pdf", answerKey = generateMockAnswerKey(30), paper = "Paper 1", testType = "Subject-wise", subject = "Mathematics"),
                Test("ctet_m_p1_full", "CTET Paper 1 Full Mock Test", 150, 50, "https://example.com/p1_full.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 1", testType = "Full Syllabus"),
                
                // Mocks Paper 2
                Test("ctet_m_p2_cdp", "Junior CDP Mock Test - 1", 30, 30, "https://example.com/p2_cdp.pdf", "https://example.com/omr_30.pdf", answerKey = generateMockAnswerKey(30), paper = "Paper 2", testType = "Subject-wise", subject = "CDP"),
                Test("ctet_m_p2_sst", "Junior Social Science Mock - 1", 50, 50, "https://example.com/p2_sst.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 2", testType = "Subject-wise", subject = "Social Science"),
                Test("ctet_m_p2_math", "Junior Mathematics Mock - 1", 30, 30, "https://example.com/p2_math.pdf", "https://example.com/omr_30.pdf", answerKey = generateMockAnswerKey(30), paper = "Paper 2", testType = "Subject-wise", subject = "Mathematics"),
                Test("ctet_m_p2_full", "CTET Paper 2 Full Mock Test", 150, 50, "https://example.com/p2_full.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 2", testType = "Full Syllabus"),

                // PYQs Paper 1
                Test("ctet_pyq_2024_p1", "CTET Paper 1 Solved PYQ 2024", 150, 50, "https://example.com/pyq_2024_p1.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 1", isPyq = true, year = "2024"),
                Test("ctet_pyq_2023_p1", "CTET Paper 1 Solved PYQ 2023", 150, 50, "https://example.com/pyq_2023_p1.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 1", isPyq = true, year = "2023"),
                Test("ctet_pyq_2022_p1", "CTET Paper 1 Solved PYQ 2022 (Demo)", 150, 50, "https://example.com/pyq_2022_p1.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 1", isPyq = true, year = "2022"),

                // PYQs Paper 2
                Test("ctet_pyq_2024_p2", "CTET Paper 2 Solved PYQ 2024", 150, 50, "https://example.com/pyq_2024_p2.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 2", isPyq = true, year = "2024"),
                Test("ctet_pyq_2023_p2", "CTET Paper 2 Solved PYQ 2023", 150, 50, "https://example.com/pyq_2023_p2.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 2", isPyq = true, year = "2023"),
                Test("ctet_pyq_2022_p2", "CTET Paper 2 Solved PYQ 2022 (Demo)", 150, 50, "https://example.com/pyq_2022_p2.pdf", "https://example.com/omr_50.pdf", answerKey = generateMockAnswerKey(50), paper = "Paper 2", isPyq = true, year = "2022")
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
