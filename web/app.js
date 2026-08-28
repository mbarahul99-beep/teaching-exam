// -------------------------------------------------------------
// App State & Database (Dynamic Load from LocalStorage)
// -------------------------------------------------------------

const DEFAULT_DB = {
    exams: [
        { id: "neet", title: "National Eligibility cum Entrance Test", shortName: "NEET", iconName: "fa-stethoscope" },
        { id: "ctet", title: "Central Teacher Eligibility Test", shortName: "CTET", iconName: "fa-school" },
        { id: "kvs_nvs", title: "KVS / NVS Recruitment Exam", shortName: "KVS/NVS", iconName: "fa-building-columns" },
        { id: "dsssb", title: "DSSSB Teacher Recruitment", shortName: "DSSSB", iconName: "fa-city" },
        { id: "ugc_net", title: "University Grants Commission National Eligibility Test", shortName: "UGC NET", iconName: "fa-school" },
        { id: "emrs", title: "Eklavya Model Resident School", shortName: "EMRS", iconName: "fa-house-user" }
    ],

    stateExams: [
        { id: "uptet", title: "Uttar Pradesh Teacher Eligibility Test", shortName: "UP TET", iconName: "fa-gavel" },
        { id: "bihar_stet", title: "Bihar Secondary Teacher Eligibility Test", shortName: "BIHAR STET", iconName: "fa-school" },
        { id: "reet", title: "Rajasthan Eligibility Examination for Teacher", shortName: "REET", iconName: "fa-city" }
    ],

    pdfNotes: [
        // Paper 1 Notes
        { id: "note_ctet_p1_evs_c3", title: "Class 3 EVS NCERT Summary", subject: "EVS", sizeMb: 2.4, pdfUrl: "https://example.com/pdf/ctet_p1_evs_c3.pdf", paper: "Paper 1", noteType: "NCERT", classLevel: "Class 3" },
        { id: "note_ctet_p1_evs_c4", title: "Class 4 EVS NCERT Summary", subject: "EVS", sizeMb: 3.1, pdfUrl: "https://example.com/pdf/ctet_p1_evs_c4.pdf", paper: "Paper 1", noteType: "NCERT", classLevel: "Class 4" },
        { id: "note_ctet_p1_evs_c5", title: "Class 5 EVS NCERT Summary", subject: "EVS", sizeMb: 4.5, pdfUrl: "https://example.com/pdf/ctet_p1_evs_c5.pdf", paper: "Paper 1", noteType: "NCERT", classLevel: "Class 5" },
        { id: "note_ctet_p1_maths_c3", title: "Class 3 Maths Primary Notes", subject: "Mathematics", sizeMb: 1.8, pdfUrl: "https://example.com/pdf/ctet_p1_maths_c3.pdf", paper: "Paper 1", noteType: "NCERT", classLevel: "Class 3" },
        
        // Paper 2 Notes
        { id: "note_ctet_p2_sst_c6", title: "Class 6 SST Our Pasts History", subject: "Social Science", sizeMb: 3.0, pdfUrl: "https://example.com/pdf/ctet_p2_sst_c6.pdf", paper: "Paper 2", noteType: "NCERT", classLevel: "Class 6" },
        { id: "note_ctet_p2_sci_c7", title: "Class 7 Science NCERT Summary", subject: "Science", sizeMb: 5.2, pdfUrl: "https://example.com/pdf/ctet_p2_sci_c7.pdf", paper: "Paper 2", noteType: "NCERT", classLevel: "Class 7" },
        { id: "note_ctet_p2_math_c8", title: "Class 8 Maths NCERT Solutions", subject: "Mathematics", sizeMb: 2.2, pdfUrl: "https://example.com/pdf/ctet_p2_math_c8.pdf", paper: "Paper 2", noteType: "NCERT", classLevel: "Class 8" },
        { id: "note_ctet_p2_math_c6", title: "Class 6 Maths NCERT Formulas", subject: "Mathematics", sizeMb: 1.5, pdfUrl: "https://example.com/pdf/ctet_p2_math_c6.pdf", paper: "Paper 2", noteType: "NCERT", classLevel: "Class 6" },

        // Both / Core Subject Theory Notes
        { id: "note_ctet_cdp_core", title: "CDP Theories: Piaget & Vygotsky", subject: "CDP", sizeMb: 3.8, pdfUrl: "https://example.com/pdf/ctet_cdp_core.pdf", paper: "Both", noteType: "Subject Theory" },
        { id: "note_ctet_eng_ped", title: "English Pedagogy teaching methods", subject: "English", sizeMb: 2.1, pdfUrl: "https://example.com/pdf/ctet_eng_ped.pdf", paper: "Both", noteType: "Subject Theory" },

        // Legacy / Other Exam Notes
        { id: "note_his_01", title: "Ancient Indian History Notes", subject: "History", sizeMb: 2.4, pdfUrl: "https://example.com/pdf/ancient_history.pdf", paper: "Both", noteType: "Subject Theory" },
        { id: "note_his_02", title: "Modern Freedom Struggle Key Points", subject: "History", sizeMb: 3.1, pdfUrl: "https://example.com/pdf/modern_history.pdf", paper: "Both", noteType: "Subject Theory" },
        { id: "note_geo_01", title: "Physical Geography of India", subject: "Geography", sizeMb: 4.5, pdfUrl: "https://example.com/pdf/physical_geography.pdf", paper: "Both", noteType: "Subject Theory" },
        { id: "note_pol_01", title: "Indian Constitution & Preamble", subject: "Political Science", sizeMb: 3.0, pdfUrl: "https://example.com/pdf/constitution.pdf", paper: "Both", noteType: "Subject Theory" }
    ],

    testSeries: [
        {
            id: "ts_neet_demo",
            examId: "neet",
            title: "NEET/JEE 180-Question Mock Test Series",
            description: "Standard full length mock test matching NEET format (180 questions).",
            numberOfTests: 1,
            tests: [
                { id: "neet_demo_01", title: "NEET Demo Mock Test - 180 Questions", durationMinutes: 180, totalQuestions: 180, answerKey: generateAnswerKey(180), testType: "Full Syllabus" }
            ]
        },
        {
            id: "ts_ctet_series",
            examId: "ctet",
            title: "CTET Complete Test Series (Paper 1 & 2)",
            description: "Syllabus mock tests and solved past papers.",
            numberOfTests: 12,
            tests: [
                // Mocks Paper 1
                { id: "ctet_m_p1_cdp", title: "Primary CDP Mock Test - 1", durationMinutes: 30, totalQuestions: 30, answerKey: generateAnswerKey(30), paper: "Paper 1", testType: "Subject-wise", subject: "CDP" },
                { id: "ctet_m_p1_evs", title: "Primary EVS Mock Test - 1", durationMinutes: 30, totalQuestions: 30, answerKey: generateAnswerKey(30), paper: "Paper 1", testType: "Subject-wise", subject: "EVS" },
                { id: "ctet_m_p1_maths", title: "Primary Mathematics Mock - 1", durationMinutes: 30, totalQuestions: 30, answerKey: generateAnswerKey(30), paper: "Paper 1", testType: "Subject-wise", subject: "Mathematics" },
                { id: "ctet_m_p1_full", title: "CTET Paper 1 Full Mock Test", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 1", testType: "Full Syllabus" },
                
                // Mocks Paper 2
                { id: "ctet_m_p2_cdp", title: "Junior CDP Mock Test - 1", durationMinutes: 30, totalQuestions: 30, answerKey: generateAnswerKey(30), paper: "Paper 2", testType: "Subject-wise", subject: "CDP" },
                { id: "ctet_m_p2_sst", title: "Junior Social Science Mock - 1", durationMinutes: 50, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 2", testType: "Subject-wise", subject: "Social Science" },
                { id: "ctet_m_p2_math", title: "Junior Mathematics Mock - 1", durationMinutes: 30, totalQuestions: 30, answerKey: generateAnswerKey(30), paper: "Paper 2", testType: "Subject-wise", subject: "Mathematics" },
                { id: "ctet_m_p2_full", title: "CTET Paper 2 Full Mock Test", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 2", testType: "Full Syllabus" },

                // PYQs Paper 1
                { id: "ctet_pyq_2024_p1", title: "CTET Paper 1 Solved PYQ 2024", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 1", isPyq: true, year: "2024" },
                { id: "ctet_pyq_2023_p1", title: "CTET Paper 1 Solved PYQ 2023", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 1", isPyq: true, year: "2023" },
                { id: "ctet_pyq_2022_p1", title: "CTET Paper 1 Solved PYQ 2022 (Demo)", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 1", isPyq: true, year: "2022" },

                // PYQs Paper 2
                { id: "ctet_pyq_2024_p2", title: "CTET Paper 2 Solved PYQ 2024", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 2", isPyq: true, year: "2024" },
                { id: "ctet_pyq_2023_p2", title: "CTET Paper 2 Solved PYQ 2023", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 2", isPyq: true, year: "2023" },
                { id: "ctet_pyq_2022_p2", title: "CTET Paper 2 Solved PYQ 2022 (Demo)", durationMinutes: 150, totalQuestions: 50, answerKey: generateAnswerKey(50), paper: "Paper 2", isPyq: true, year: "2022" }
            ]
        },
        {
            id: "ts_kvs_general",
            examId: "kvs_nvs",
            title: "KVS General Paper Mock Test Series",
            description: "Full length mock tests covering English, Hindi, Reasoning, and Pedagogy.",
            numberOfTests: 2,
            tests: [
                { id: "kvs_gen_01", title: "KVS Mock Test 01 (General Aptitude)", durationMinutes: 60, totalQuestions: 50, answerKey: generateAnswerKey(50) },
                { id: "kvs_gen_02", title: "KVS Mock Test 02 (English & Reasoning)", durationMinutes: 60, totalQuestions: 50, answerKey: generateAnswerKey(50) }
            ]
        }
    ],

    updates: [
        { id: "upd_01", title: "CTET December 2026 Application Dates Released", content: "The Central Board of Secondary Education (CBSE) has released the online application dates for CTET Dec 2026. Check the syllabus and eligibility before applying.", dateString: "2026-08-15" },
        { id: "upd_02", title: "DSSSB PGT/TGT Exam Schedule Declared", content: "DSSSB has announced the exam dates for PGT and TGT examinations. Download admit cards from official portal 7 days prior to exam.", dateString: "2026-08-14" }
    ],

    defaultAttempts: [
        { id: "att_01", testId: "ctet_ped_01", testTitle: "Child Development and Pedagogy - Test 01", attemptType: "ONLINE", dateString: "2026-08-12 14:23", marksObtained: 24, totalMarks: 30, correctAnswers: 24, incorrectAnswers: 6, skippedAnswers: 0 },
        { id: "att_02", testId: "kvs_gen_01", testTitle: "KVS Mock Test 01 (General Aptitude)", attemptType: "OMR", dateString: "2026-08-14 11:05", marksObtained: 38, totalMarks: 50, correctAnswers: 38, incorrectAnswers: 10, skippedAnswers: 2 }
    ]
};

function generateAnswerKey(total) {
    const options = ["A", "B", "C", "D"];
    const key = {};
    for (let i = 1; i <= total; i++) {
        key[i] = options[i % 4];
    }
    return key;
}

// Global active database object loaded from local storage or defaults
let DB = {};

function initDatabase() {
    // Migration: Update defaults to swap UP TET with UGC NET and add State Exams
    if (!localStorage.getItem("migration_ugc_net_v2")) {
        localStorage.removeItem("adm_exams");
        localStorage.removeItem("adm_state_exams");
        localStorage.setItem("migration_ugc_net_v2", "done");
    }
    
    // Migration: Add NEET 180 questions test series
    if (!localStorage.getItem("migration_neet_v2")) {
        localStorage.removeItem("adm_exams");
        localStorage.removeItem("adm_test_series");
        localStorage.setItem("migration_neet_v2", "done");
    }

    const parsedExams = JSON.parse(localStorage.getItem("adm_exams"));
    DB.exams = (parsedExams && parsedExams.length > 0) ? parsedExams : [...DEFAULT_DB.exams];
    
    const parsedStateExams = JSON.parse(localStorage.getItem("adm_state_exams"));
    DB.stateExams = (parsedStateExams && parsedStateExams.length > 0) ? parsedStateExams : [...DEFAULT_DB.stateExams];
    
    const parsedPdfNotes = JSON.parse(localStorage.getItem("adm_pdf_notes"));
    DB.pdfNotes = (parsedPdfNotes && parsedPdfNotes.length > 0) ? parsedPdfNotes : [...DEFAULT_DB.pdfNotes];
    
    const parsedTestSeries = JSON.parse(localStorage.getItem("adm_test_series"));
    DB.testSeries = (parsedTestSeries && parsedTestSeries.length > 0) ? parsedTestSeries : [...DEFAULT_DB.testSeries];
    
    const parsedUpdates = JSON.parse(localStorage.getItem("adm_updates"));
    DB.updates = (parsedUpdates && parsedUpdates.length > 0) ? parsedUpdates : [...DEFAULT_DB.updates];
    
    DB.defaultAttempts = [...DEFAULT_DB.defaultAttempts];
    
    // Save defaults back to storage if empty or not set
    localStorage.setItem("adm_exams", JSON.stringify(DB.exams));
    localStorage.setItem("adm_state_exams", JSON.stringify(DB.stateExams));
    localStorage.setItem("adm_pdf_notes", JSON.stringify(DB.pdfNotes));
    localStorage.setItem("adm_test_series", JSON.stringify(DB.testSeries));
    localStorage.setItem("adm_updates", JSON.stringify(DB.updates));
}

initDatabase();

// -------------------------------------------------------------
// State Management Variables
// -------------------------------------------------------------
let state = {
    history: [],
    currentScreen: "home",
    selectedSubject: "All",
    examSearchQuery: "",
    activeTest: null,
    activeSeries: null,
    
    // Exam Details Filtering states
    selectedPaper: "Paper 1",
    selectedMockType: "Full Syllabus",
    selectedMockSubject: "",
    selectedNoteClass: "",
    selectedNoteSubject: "",
    selectedPyqYear: "All Years",
    expandedClassLevel: null,
    
    // Online test state
    onlineAnswers: {},
    onlineReview: {},
    currentQuestionIndex: 0,
    timerInterval: null,
    secondsRemaining: 0,

    // PDF state
    activePdfNote: null,
    pdfCurrentPage: 1,
    pdfTotalPages: 12,
    downloadedNotes: {},

    // Camera Stream
    videoStream: null
};

// Load history from localStorage
function initAppState() {
    const stored = localStorage.getItem("omr_test_history");
    if (stored) {
        state.history = JSON.parse(stored);
    } else {
        state.history = [...(DB.defaultAttempts || DEFAULT_DB.defaultAttempts || [])];
        localStorage.setItem("omr_test_history", JSON.stringify(state.history));
    }
}

// Save attempts
function saveAttempt(record) {
    state.history.unshift(record);
    localStorage.setItem("omr_test_history", JSON.stringify(state.history));
    updateProfileStats();
}

// -------------------------------------------------------------
// Navigation Controller
// -------------------------------------------------------------
function navigateTo(screenId) {
    // Stop camera stream if we leave scanner screen
    if (state.currentScreen === "omr-scanner" && screenId !== "omr-scanner") {
        stopCamera();
    }
    // Stop timers if we leave online test player
    if (state.currentScreen === "online-test-player" && screenId !== "online-test-player") {
        clearInterval(state.timerInterval);
    }

    state.currentScreen = screenId;
    
    // Toggle main sticky app-footer visibility on full screen views
    const appFooter = document.querySelector(".app-footer");
    if (appFooter) {
        if (screenId === "omr-scanner" || screenId === "online-test-player") {
            appFooter.style.setProperty("display", "none", "important");
        } else {
            appFooter.style.setProperty("display", "flex", "important");
        }
    }

    // Toggle active screen visibility
    document.querySelectorAll(".app-screen").forEach(screen => {
        screen.classList.remove("active");
    });
    const targetScreen = document.getElementById(`screen-${screenId}`);
    if (targetScreen) targetScreen.classList.add("active");

    // Scroll main content to top
    document.querySelector(".content-container").scrollTop = 0;

    // Toggle footer active state
    document.querySelectorAll(".footer-nav-item").forEach(btn => {
        btn.classList.remove("active");
        if (btn.getAttribute("data-screen") === screenId) {
            btn.classList.add("active");
        }
    });

    // Toggle sidebar active state
    document.querySelectorAll(".nav-item").forEach(item => {
        item.classList.remove("active");
        if (item.getAttribute("data-screen") === screenId) {
            item.classList.add("active");
        }
    });

    // Header Title updates
    const titleMap = {
        "home": "OMR Prep Portal",
        "exams": "Available Exams",
        "notes": "PDF Notes Library",
        "test-series-catalog": "Test Series Catalog",
        "test-series-details": state.activeSeries ? state.activeSeries.title : "Series Details",
        "online-test-player": "Online Test Mode",
        "omr-scan-prep": "OMR Attempt Mode",
        "omr-scanner": "OMR Scanner Feed",
        "omr-result": "Grader Scorecard",
        "profile": "Student Dashboard",
        "test-details": "Mock Test Details",
        "exam-details": state.activeExam ? state.activeExam.shortName + " Exam Details" : "Exam Details"
    };
    
    document.getElementById("header-title").innerText = titleMap[screenId] || "OMR Prep Portal";
}

// -------------------------------------------------------------
// Component Render Functions
// -------------------------------------------------------------

// Render Home Screen Exam Buttons
function renderHomeExams() {
    const container = document.getElementById("home-exams-list");
    if (!container) return;
    container.innerHTML = "";

    DB.exams.forEach(exam => {
        const div = document.createElement("div");
        div.className = "compact-rect-item";
        div.innerHTML = `
            <div class="rect-icon-box"><i class="fa-solid ${exam.iconName}"></i></div>
            <div class="rect-text-box">
                <h4>${exam.shortName}</h4>
            </div>
        `;
        div.addEventListener("click", () => {
            showExamDetails(exam);
        });
        container.appendChild(div);
    });

    // View All Button card at the bottom of the list
    const viewAllCard = document.createElement("div");
    viewAllCard.className = "compact-rect-item";
    viewAllCard.innerHTML = `
        <div class="rect-icon-box" style="color: var(--secondary);"><i class="fa-solid fa-arrow-right-long"></i></div>
        <div class="rect-text-box">
            <h4>View All Exams</h4>
        </div>
    `;
    viewAllCard.addEventListener("click", () => navigateTo("exams"));
    container.appendChild(viewAllCard);
}

// Render Home Screen News Updates
function renderHomeUpdates() {
    const container = document.getElementById("home-updates-list");
    if (!container) return;
    container.innerHTML = "";

    DB.updates.forEach(upd => {
        const card = document.createElement("div");
        card.className = "news-update-card";
        card.innerHTML = `
            <div class="news-header">
                <span class="news-tag">LATEST UPDATE</span>
                <span class="news-date">${upd.dateString}</span>
            </div>
            <h4>${upd.title}</h4>
            <p>${upd.content}</p>
        `;
        container.appendChild(card);
    });
}

// Render Home Subject List (Compact rectangles, from up to down)
function renderHomeSubjects() {
    const container = document.getElementById("home-subjects-list");
    if (!container) return;
    container.innerHTML = "";

    const subjects = ["History", "Geography", "Political S", "Hindi", "Maths", "Science"];
    const icons = {
        "History": "fa-hourglass-empty",
        "Geography": "fa-earth-americas",
        "Political S": "fa-landmark",
        "Hindi": "fa-language",
        "Maths": "fa-calculator",
        "Science": "fa-flask-vial"
    };

    subjects.forEach(sub => {
        const div = document.createElement("div");
        div.className = "compact-rect-item pdf-note-subject";
        div.innerHTML = `
            <div class="rect-icon-box"><i class="fa-solid ${icons[sub] || 'fa-file-lines'}"></i></div>
            <div class="rect-text-box">
                <h4>${sub}</h4>
            </div>
        `;
        div.addEventListener("click", () => {
            state.selectedSubject = sub;
            navigateTo("notes");
            renderPDFNotes();
        });
        container.appendChild(div);
    });
}

// Render Searchable Exams Grid (Now compact list)
function renderExamsGrid() {
    const container = document.getElementById("exams-grid-container");
    if (!container) return;
    container.innerHTML = "";

    const query = state.examSearchQuery.toLowerCase();
    const filtered = DB.stateExams.filter(exam => 
        exam.title.toLowerCase().includes(query) || 
        exam.shortName.toLowerCase().includes(query)
    );

    if (filtered.length === 0) {
        container.innerHTML = `<p class="empty-text">No exams found matching "${state.examSearchQuery}"</p>`;
        return;
    }

    filtered.forEach(exam => {
        const card = document.createElement("div");
        card.className = "compact-rect-item";
        card.innerHTML = `
            <div class="rect-icon-box"><i class="fa-solid ${exam.iconName}"></i></div>
            <div class="rect-text-box">
                <h4>${exam.shortName}</h4>
            </div>
        `;
        card.addEventListener("click", () => {
            showExamDetails(exam);
        });
        container.appendChild(card);
    });
}

function showExamDetails(exam) {
    state.activeExam = exam;
    
    // Fill title
    document.getElementById("exam-details-title").innerText = exam.title;

    // Render top-level paper filter container
    const filtersContainer = document.getElementById("exam-details-filters-container");
    filtersContainer.innerHTML = "";
    
    if (exam.id === "ctet") {
        filtersContainer.style.display = "block";
        const paperDiv = document.createElement("div");
        paperDiv.style.display = "flex";
        paperDiv.style.alignItems = "center";
        paperDiv.style.gap = "8px";
        paperDiv.style.padding = "4px 0";
        
        paperDiv.innerHTML = `
            <span class="filter-section-title" style="margin: 0; font-size: 11px; font-weight: 800;">Paper:</span>
            <div class="filter-pills-row" style="gap: 6px;">
                <button class="filter-chip-pill ${state.selectedPaper === 'Paper 1' ? 'active' : ''}" data-paper="Paper 1" style="padding: 4px 10px; font-size: 11.5px; border-radius: 12px;">Paper 1</button>
                <button class="filter-chip-pill ${state.selectedPaper === 'Paper 2' ? 'active' : ''}" data-paper="Paper 2" style="padding: 4px 10px; font-size: 11.5px; border-radius: 12px;">Paper 2</button>
            </div>
        `;
        
        paperDiv.querySelectorAll(".filter-chip-pill").forEach(btn => {
            btn.addEventListener("click", () => {
                state.selectedPaper = btn.getAttribute("data-paper");
                state.selectedMockSubject = "";
                state.selectedMockType = "Full Syllabus";
                state.selectedNoteClass = "";
                state.selectedNoteSubject = "";
                state.selectedPyqYear = "All Years";
                state.expandedClassLevel = null;
                showExamDetails(exam);
            });
        });
        
        filtersContainer.appendChild(paperDiv);
    } else {
        filtersContainer.style.display = "none";
    }

    // Get tab content elements
    const tabMocks = document.getElementById("exam-tab-mocks");
    const tabNotes = document.getElementById("exam-tab-notes");
    const tabPyqs = document.getElementById("exam-tab-pyqs");

    // Default to mocks tab if none is active
    let activeTabName = "mocks";
    document.querySelectorAll(".exam-sub-tab").forEach(btn => {
        if (btn.classList.contains("active")) {
            activeTabName = btn.getAttribute("data-tab");
        }
    });

    const activateTab = (tabName) => {
        document.querySelectorAll(".exam-sub-tab").forEach(btn => {
            const isTarget = btn.getAttribute("data-tab") === tabName;
            if (isTarget) {
                btn.classList.add("active");
            } else {
                btn.classList.remove("active");
            }
        });
        tabMocks.style.display = tabName === "mocks" ? "block" : "none";
        tabNotes.style.display = tabName === "notes" ? "block" : "none";
        tabPyqs.style.display = tabName === "pyqs" ? "block" : "none";
    };
    
    activateTab(activeTabName);

    // Add tab click handlers
    document.querySelectorAll(".exam-sub-tab").forEach(btn => {
        btn.onclick = () => {
            const tabName = btn.getAttribute("data-tab");
            activateTab(tabName);
        };
    });

    // Utility for empty state HTML
    const getEmptyStateHtml = (message) => `
        <div class="empty-state-container" style="padding: 24px 16px;">
            <i class="fa-solid fa-folder-open" style="font-size: 24px; margin-bottom: 8px;"></i>
            <p style="font-size: 12.5px;">${message}</p>
        </div>
    `;

    // 1. Render Mock Tests for this exam
    const mocksContainer = document.getElementById("exam-details-mocks-list");
    mocksContainer.innerHTML = "";
    
    const examSeriesList = DB.testSeries.filter(ts => ts.examId === exam.id);
    let allMocks = [];
    examSeriesList.forEach(series => {
        series.tests.forEach(test => {
            if (!test.isPyq) {
                allMocks.push(test);
            }
        });
    });

    // Sub-filters row (Full Syllabus vs Subject-wise Dropdown)
    const mockFiltersDiv = document.createElement("div");
    mockFiltersDiv.className = "filter-pills-row";
    mockFiltersDiv.style.alignItems = "center";
    mockFiltersDiv.style.gap = "8px";
    mockFiltersDiv.style.marginBottom = "8px";
    mockFiltersDiv.style.padding = "4px 0";
    
    // Full Syllabus Button
    const isFullSyllabus = state.selectedMockType === "Full Syllabus";
    const fullSyllabusBtn = document.createElement("button");
    fullSyllabusBtn.className = `filter-chip-pill ${isFullSyllabus ? 'active' : ''}`;
    fullSyllabusBtn.innerText = "Full Syllabus";
    fullSyllabusBtn.style.padding = "5px 12px";
    fullSyllabusBtn.style.fontSize = "11.5px";
    fullSyllabusBtn.style.borderRadius = "12px";
    fullSyllabusBtn.addEventListener("click", () => {
        state.selectedMockType = "Full Syllabus";
        state.selectedMockSubject = "";
        showExamDetails(exam);
    });
    mockFiltersDiv.appendChild(fullSyllabusBtn);
    
    // Dropdown for Subject-wise
    const isSubjectWise = state.selectedMockType === "Subject-wise";
    const select = document.createElement("select");
    select.className = "filter-select-dropdown";
    select.style.padding = "5px 12px";
    select.style.fontSize = "11.5px";
    select.style.borderRadius = "12px";
    if (isSubjectWise) {
        select.style.borderColor = "var(--primary)";
        select.style.backgroundColor = "var(--primary-container)";
        select.style.color = "var(--primary)";
    }
    
    const optPlaceholder = document.createElement("option");
    optPlaceholder.value = "";
    optPlaceholder.innerText = "Subject-wise Dropdown";
    select.appendChild(optPlaceholder);
    
    const subjectsMap = state.selectedPaper === "Paper 1" 
        ? [
            { val: "CDP", label: "CDP" },
            { val: "Hindi", label: "Language (Hindi)" },
            { val: "English", label: "Language (English)" },
            { val: "EVS", label: "EVS" },
            { val: "Mathematics", label: "Mathematics" }
          ]
        : [
            { val: "CDP", label: "CDP" },
            { val: "Hindi", label: "Language (Hindi)" },
            { val: "English", label: "Language (English)" },
            { val: "Mathematics", label: "Mathematics" },
            { val: "Social Science", label: "Social Science" }
          ];
          
    subjectsMap.forEach(sub => {
        const opt = document.createElement("option");
        opt.value = sub.val;
        opt.innerText = sub.label;
        if (isSubjectWise && state.selectedMockSubject === sub.val) {
            opt.selected = true;
        }
        select.appendChild(opt);
    });
    
    select.addEventListener("change", (e) => {
        const val = e.target.value;
        if (val) {
            state.selectedMockType = "Subject-wise";
            state.selectedMockSubject = val;
        } else {
            state.selectedMockType = "Full Syllabus";
            state.selectedMockSubject = "";
        }
        showExamDetails(exam);
    });
    
    mockFiltersDiv.appendChild(select);
    mocksContainer.appendChild(mockFiltersDiv);

    // Filter mocks in memory
    let filteredMocks = allMocks.filter(test => {
        if (exam.id === "ctet") {
            if (test.paper && test.paper !== state.selectedPaper && test.paper !== "Both") return false;
        }
        if (state.selectedMockType === "Full Syllabus") {
            if (test.testType !== "Full Syllabus") return false;
        } else if (state.selectedMockType === "Subject-wise") {
            if (test.testType !== "Subject-wise") return false;
            if (state.selectedMockSubject && test.subject !== state.selectedMockSubject) return false;
        }
        return true;
    });

    if (filteredMocks.length === 0) {
        mocksContainer.innerHTML += getEmptyStateHtml("No mock tests match your selection.");
    } else {
        filteredMocks.forEach(test => {
            const card = document.createElement("div");
            card.className = "individual-test-card";
            card.style.cursor = "pointer";
            card.style.padding = "12px";
            card.style.gap = "8px";
            card.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <h4 style="margin: 0; font-size: 13.5px; font-weight: 700; color: var(--on-surface);">${test.title}</h4>
                    <i class="fa-solid fa-chevron-right text-primary" style="font-size: 11px;"></i>
                </div>
                <div class="test-stats-row" style="margin-top: 4px; display: flex; gap: 12px; font-size: 11px; opacity: 0.7;">
                    <span><i class="fa-regular fa-clock"></i> ${test.durationMinutes} Mins</span>
                    <span><i class="fa-regular fa-file-lines"></i> ${test.totalQuestions} Questions</span>
                </div>
            `;
            card.addEventListener("click", () => {
                showTestDetails(test);
            });
            mocksContainer.appendChild(card);
        });
    }

    // 2. Render PDF Notes for this exam (Option B: Collapsible Classwise cards + Core lists)
    const notesContainer = document.getElementById("exam-details-notes-list");
    notesContainer.innerHTML = "";
    
    // Sub-filters row
    const notesFiltersDiv = document.createElement("div");
    notesFiltersDiv.className = "filter-pills-row";
    notesFiltersDiv.style.alignItems = "center";
    notesFiltersDiv.style.gap = "8px";
    notesFiltersDiv.style.marginBottom = "8px";
    notesFiltersDiv.style.padding = "4px 0";
    
    // All Notes Button
    const isAllNotes = !state.selectedNoteClass && !state.selectedNoteSubject;
    const allNotesBtn = document.createElement("button");
    allNotesBtn.className = `filter-chip-pill ${isAllNotes ? 'active' : ''}`;
    allNotesBtn.innerText = "All Notes";
    allNotesBtn.style.padding = "5px 12px";
    allNotesBtn.style.fontSize = "11.5px";
    allNotesBtn.style.borderRadius = "12px";
    allNotesBtn.addEventListener("click", () => {
        state.selectedNoteClass = "";
        state.selectedNoteSubject = "";
        showExamDetails(exam);
    });
    notesFiltersDiv.appendChild(allNotesBtn);
    
    // Classes Dropdown
    const selectClass = document.createElement("select");
    selectClass.className = "filter-select-dropdown";
    selectClass.style.padding = "5px 12px";
    selectClass.style.fontSize = "11.5px";
    selectClass.style.borderRadius = "12px";
    if (state.selectedNoteClass) {
        selectClass.style.borderColor = "var(--primary)";
        selectClass.style.backgroundColor = "var(--primary-container)";
        selectClass.style.color = "var(--primary)";
    }
    
    const optClassPlaceholder = document.createElement("option");
    optClassPlaceholder.value = "";
    optClassPlaceholder.innerText = "Classes Dropdown";
    selectClass.appendChild(optClassPlaceholder);
    
    const classes = state.selectedPaper === "Paper 1" 
        ? ["Class 1", "Class 2", "Class 3", "Class 4", "Class 5"]
        : ["Class 6", "Class 7", "Class 8"];
    classes.forEach(c => {
        const opt = document.createElement("option");
        opt.value = c;
        opt.innerText = c;
        if (state.selectedNoteClass === c) {
            opt.selected = true;
        }
        selectClass.appendChild(opt);
    });
    
    selectClass.addEventListener("change", (e) => {
        state.selectedNoteClass = e.target.value;
        showExamDetails(exam);
    });
    notesFiltersDiv.appendChild(selectClass);
    
    // Subjects Dropdown
    const selectSub = document.createElement("select");
    selectSub.className = "filter-select-dropdown";
    selectSub.style.padding = "5px 12px";
    selectSub.style.fontSize = "11.5px";
    selectSub.style.borderRadius = "12px";
    if (state.selectedNoteSubject) {
        selectSub.style.borderColor = "var(--primary)";
        selectSub.style.backgroundColor = "var(--primary-container)";
        selectSub.style.color = "var(--primary)";
    }
    
    const optSubPlaceholder = document.createElement("option");
    optSubPlaceholder.value = "";
    optSubPlaceholder.innerText = "Subjects Dropdown";
    selectSub.appendChild(optSubPlaceholder);
    
    const notesSubjectsMap = state.selectedPaper === "Paper 1" 
        ? [
            { val: "CDP", label: "CDP" },
            { val: "Hindi", label: "Language (Hindi)" },
            { val: "English", label: "Language (English)" },
            { val: "EVS", label: "EVS" },
            { val: "Mathematics", label: "Mathematics" }
          ]
        : [
            { val: "CDP", label: "CDP" },
            { val: "Hindi", label: "Language (Hindi)" },
            { val: "English", label: "Language (English)" },
            { val: "Mathematics", label: "Mathematics" },
            { val: "Social Science", label: "Social Science" }
          ];
          
    notesSubjectsMap.forEach(sub => {
        const opt = document.createElement("option");
        opt.value = sub.val;
        opt.innerText = sub.label;
        if (state.selectedNoteSubject === sub.val) {
            opt.selected = true;
        }
        selectSub.appendChild(opt);
    });
    
    selectSub.addEventListener("change", (e) => {
        state.selectedNoteSubject = e.target.value;
        showExamDetails(exam);
    });
    notesFiltersDiv.appendChild(selectSub);
    
    notesContainer.appendChild(notesFiltersDiv);

    // Filter base notes matching this exam's syllabus criteria
    let relatedNotes = [];
    if (exam.id === "ctet") {
        relatedNotes = DB.pdfNotes.filter(n => n.paper === "Paper 1" || n.paper === "Paper 2" || n.paper === "Both");
        if (state.selectedPaper !== "All Papers") {
            relatedNotes = relatedNotes.filter(n => n.paper === state.selectedPaper || n.paper === "Both");
        }
    } else if (exam.id === "ugc_net" || exam.id === "ugc-net") {
        relatedNotes = DB.pdfNotes.filter(n => ["Political Science", "Maths", "Science"].includes(n.subject));
    } else {
        relatedNotes = DB.pdfNotes.filter(n => ["History", "Geography", "Hindi"].includes(n.subject));
    }

    // Filter by subject if chosen
    if (state.selectedNoteSubject) {
        relatedNotes = relatedNotes.filter(n => n.subject === state.selectedNoteSubject);
    }

    // NCERT Notes (NCERT type and classLevel present)
    let ncertNotes = relatedNotes.filter(n => n.noteType === "NCERT" && n.classLevel);
    // Theory Notes (not NCERT)
    let theoryNotes = relatedNotes.filter(n => n.noteType !== "NCERT");

    // Filter NCERT notes by class if chosen
    if (state.selectedNoteClass) {
        ncertNotes = ncertNotes.filter(n => n.classLevel === state.selectedNoteClass);
        // If a class is selected, general theory notes are hidden
        theoryNotes = [];
    }

    if (ncertNotes.length === 0 && theoryNotes.length === 0) {
        notesContainer.innerHTML += getEmptyStateHtml("No notes available for this selection.");
    } else {
        // Group NCERT Notes by Class Level
        if (ncertNotes.length > 0) {
            const heading = document.createElement("h3");
            heading.style.fontSize = "12px";
            heading.style.fontWeight = "800";
            heading.style.color = "var(--primary)";
            heading.style.margin = "6px 0 6px 0";
            heading.innerText = "NCERT Class-wise Summaries";
            notesContainer.appendChild(heading);

            // Group notes by classLevel
            const grouped = {};
            ncertNotes.forEach(n => {
                if (!grouped[n.classLevel]) grouped[n.classLevel] = [];
                grouped[n.classLevel].push(n);
            });

            // Sort keys Class 3 to Class 8
            const sortedClasses = Object.keys(grouped).sort((a, b) => {
                const numA = parseInt(a.replace(/\D/g, '')) || 0;
                const numB = parseInt(b.replace(/\D/g, '')) || 0;
                return numA - numB;
            });

            sortedClasses.forEach(classLevel => {
                const notesInClass = grouped[classLevel];
                const isExpanded = state.expandedClassLevel === classLevel;

                const card = document.createElement("div");
                card.className = `class-notes-card ${isExpanded ? 'expanded' : ''}`;
                card.style.padding = "10px 12px";
                card.style.marginBottom = "6px";

                card.innerHTML = `
                    <div class="class-notes-header">
                        <div class="class-notes-title-box" style="gap: 8px;">
                            <i class="fa-solid fa-book-open-reader text-primary" style="font-size: 15px;"></i>
                            <div style="text-align: left;">
                                <h4 style="font-size: 13px; font-weight: 700;">${classLevel} NCERT Book Notes</h4>
                                <span style="font-size: 10px;">${notesInClass.length} Notes Available</span>
                            </div>
                        </div>
                        <i class="fa-solid ${isExpanded ? 'fa-chevron-up' : 'fa-chevron-down'} text-primary" style="opacity: 0.8; font-size: 11px;"></i>
                    </div>
                `;

                // Add expand / collapse toggle
                card.addEventListener("click", () => {
                    state.expandedClassLevel = isExpanded ? null : classLevel;
                    showExamDetails(exam);
                });

                if (isExpanded) {
                    const expandedContainer = document.createElement("div");
                    expandedContainer.className = "class-notes-subject-group";
                    expandedContainer.style.marginTop = "8px";
                    expandedContainer.style.gap = "6px";

                    // Group notes by subject within this class
                    const notesBySubj = {};
                    notesInClass.forEach(n => {
                        if (!notesBySubj[n.subject]) notesBySubj[n.subject] = [];
                        notesBySubj[n.subject].push(n);
                    });

                    Object.keys(notesBySubj).forEach(subj => {
                        const subHeading = document.createElement("span");
                        subHeading.className = "subject-label-tag";
                        subHeading.style.fontSize = "9px";
                        subHeading.style.marginTop = "4px";
                        subHeading.style.pointerEvents = "none";
                        subHeading.innerText = subj;
                        expandedContainer.appendChild(subHeading);

                        notesBySubj[subj].forEach(note => {
                            const isDownloaded = state.downloadedNotes[note.id];
                            const row = document.createElement("div");
                            row.className = "note-item-row";
                            row.style.padding = "6px 8px";
                            
                            const btnHtml = isDownloaded 
                                ? `<button class="btn-view-note" style="padding: 4px 8px; font-size: 10px;"><i class="fa-solid fa-book-open"></i> View</button>`
                                : `<button class="btn-download-note" style="width: 28px; height: 28px; font-size: 12px;"><i class="fa-solid fa-download"></i></button>`;

                            row.innerHTML = `
                                <div style="flex: 1; min-width: 0; padding-right: 8px; text-align: left;">
                                    <h5 style="margin: 0; font-size: 12px; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${note.title}</h5>
                                    <span style="font-size: 9px; opacity: 0.6;">${note.sizeMb} MB</span>
                                </div>
                                <div>${btnHtml}</div>
                            `;

                            row.querySelector("button").addEventListener("click", (e) => {
                                e.stopPropagation();
                                if (isDownloaded) {
                                    openPdfReader(note);
                                } else {
                                    const actionBtn = row.querySelector("button");
                                    actionBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i>`;
                                    actionBtn.disabled = true;
                                    setTimeout(() => {
                                        state.downloadedNotes[note.id] = true;
                                        showExamDetails(exam); // refresh
                                    }, 1200);
                                }
                            });

                            expandedContainer.appendChild(row);
                        });
                    });

                    card.appendChild(expandedContainer);
                }

                notesContainer.appendChild(card);
            });
        }

        // Render Subject Theory notes (core syllabus notes)
        if (theoryNotes.length > 0) {
            const heading = document.createElement("h3");
            heading.style.fontSize = "12px";
            heading.style.fontWeight = "800";
            heading.style.color = "var(--primary)";
            heading.style.margin = "10px 0 6px 0";
            heading.innerText = "Core Subject-wise Notes";
            notesContainer.appendChild(heading);

            theoryNotes.forEach(note => {
                const isDownloaded = state.downloadedNotes[note.id];
                const card = document.createElement("div");
                card.className = "note-item-card";
                card.style.margin = "0";
                card.style.padding = "10px 12px";
                card.style.marginBottom = "6px";
                
                const btnHtml = isDownloaded 
                    ? `<button class="btn-view-note" style="padding: 4px 8px; font-size: 10px;"><i class="fa-solid fa-book-open"></i> View</button>`
                    : `<button class="btn-download-note" style="width: 28px; height: 28px; font-size: 12px;"><i class="fa-solid fa-download"></i></button>`;

                card.innerHTML = `
                    <div class="note-info" style="flex: 1; text-align: left;">
                        <h4 style="font-size: 12.5px; margin: 0 0 2px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 180px;">${note.title}</h4>
                        <div class="note-metadata" style="display: flex; gap: 6px; font-size: 9px;">
                            <span class="note-tag" style="background-color: var(--surface-variant); color: var(--primary); padding: 1px 4px; border-radius: 4px;">${note.subject}</span>
                            <span class="note-size">${note.sizeMb} MB</span>
                        </div>
                    </div>
                    <div class="note-action" style="margin-left: 8px;">
                        ${btnHtml}
                    </div>
                `;

                card.querySelector("button").addEventListener("click", (e) => {
                    e.stopPropagation();
                    if (isDownloaded) {
                        openPdfReader(note);
                    } else {
                        const actionBtn = card.querySelector("button");
                        actionBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i>`;
                        actionBtn.disabled = true;
                        setTimeout(() => {
                            state.downloadedNotes[note.id] = true;
                            showExamDetails(exam); // refresh
                        }, 1200);
                    }
                });
                notesContainer.appendChild(card);
            });
        }
    }

    // 3. Render PYQs for this exam
    const pyqsContainer = document.getElementById("exam-details-pyqs-list");
    pyqsContainer.innerHTML = "";
    
    // Sub-filters row
    const pyqFiltersDiv = document.createElement("div");
    pyqFiltersDiv.className = "filter-pills-row";
    pyqFiltersDiv.style.alignItems = "center";
    pyqFiltersDiv.style.gap = "8px";
    pyqFiltersDiv.style.marginBottom = "8px";
    pyqFiltersDiv.style.padding = "4px 0";
    
    const selectYear = document.createElement("select");
    selectYear.className = "filter-select-dropdown";
    selectYear.style.padding = "5px 12.5px";
    selectYear.style.fontSize = "11.5px";
    selectYear.style.borderRadius = "12px";
    if (state.selectedPyqYear !== "All Years") {
        selectYear.style.borderColor = "var(--primary)";
        selectYear.style.backgroundColor = "var(--primary-container)";
        selectYear.style.color = "var(--primary)";
    }
    
    const pyqYearOptions = ["All Years", "2024", "2023", "2022"];
    pyqYearOptions.forEach(opt => {
        const optEl = document.createElement("option");
        optEl.value = opt;
        optEl.innerText = opt;
        if (state.selectedPyqYear === opt) {
            optEl.selected = true;
        }
        selectYear.appendChild(optEl);
    });
    selectYear.addEventListener("change", (e) => {
        state.selectedPyqYear = e.target.value;
        showExamDetails(exam);
    });
    pyqFiltersDiv.appendChild(selectYear);
    pyqsContainer.appendChild(pyqFiltersDiv);

    // Find all tests of any test series belonging to this exam
    let pyqs = [];
    examSeriesList.forEach(series => {
        series.tests.forEach(test => {
            if (test.isPyq) {
                pyqs.push(test);
            }
        });
    });

    // Apply paper filters (for CTET)
    if (exam.id === "ctet") {
        pyqs = pyqs.filter(test => test.paper === state.selectedPaper || test.paper === "Both");
    }

    // Apply year filters
    if (state.selectedPyqYear !== "All Years") {
        pyqs = pyqs.filter(test => test.year === state.selectedPyqYear);
    }

    if (pyqs.length === 0) {
        pyqsContainer.innerHTML += getEmptyStateHtml("No Solved PYQ papers available for this exam.");
    } else {
        pyqs.forEach(test => {
            const card = document.createElement("div");
            card.className = "individual-test-card";
            card.style.cursor = "pointer";
            card.style.padding = "12px";
            card.style.gap = "8px";
            
            card.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <h4 style="margin: 0; font-size: 13.5px; font-weight: 700; color: var(--on-surface);">${test.title}</h4>
                    <i class="fa-solid fa-chevron-right text-primary" style="font-size: 11px;"></i>
                </div>
                <div class="test-stats-row" style="margin-top: 4px; display: flex; gap: 12px; font-size: 11px; opacity: 0.7;">
                    <span style="background-color: #e6f4ea; color: #137333; font-size: 9.5px; font-weight: 800; padding: 1px 4px; border-radius: 4px;"><i class="fa-solid fa-circle-check"></i> Solved PYQ</span>
                    <span><i class="fa-regular fa-clock"></i> ${test.durationMinutes} Mins</span>
                    <span><i class="fa-regular fa-file-lines"></i> ${test.totalQuestions} Questions</span>
                </div>
            `;
            card.addEventListener("click", () => {
                showTestDetails(test);
            });
            pyqsContainer.appendChild(card);
        });
    }

    navigateTo("exam-details");
}

// Render PDF Notes Library
function renderPDFNotes() {
    const filterContainer = document.getElementById("notes-filter-container");
    const listContainer = document.getElementById("notes-list-container");
    if (!filterContainer || !listContainer) return;

    // Render Filter Chips
    filterContainer.innerHTML = "";
    const subjects = ["All", "History", "Geography", "Political S", "Hindi", "Maths", "Science"];
    subjects.forEach(sub => {
        const chip = document.createElement("div");
        chip.className = `subject-chip ${state.selectedSubject === sub ? 'active' : ''}`;
        chip.innerText = sub;
        chip.addEventListener("click", () => {
            state.selectedSubject = sub;
            renderPDFNotes();
        });
        filterContainer.appendChild(chip);
    });

    // Render List
    listContainer.innerHTML = "";
    const filtered = state.selectedSubject === "All" 
        ? DB.pdfNotes 
        : DB.pdfNotes.filter(n => n.subject === state.selectedSubject);

    if (filtered.length === 0) {
        listContainer.innerHTML = `<p class="empty-text">No notes available for "${state.selectedSubject}"</p>`;
        return;
    }

    filtered.forEach(note => {
        const isDownloaded = state.downloadedNotes[note.id];
        const card = document.createElement("div");
        card.className = "note-item-card";

        const btnHtml = isDownloaded 
            ? `<button class="btn-view-note" data-id="${note.id}"><i class="fa-solid fa-book-open"></i> View</button>`
            : `<button class="btn-download-note" data-id="${note.id}"><i class="fa-solid fa-download"></i></button>`;

        card.innerHTML = `
            <div class="note-info">
                <h4>${note.title}</h4>
                <div class="note-metadata">
                    <span class="note-tag">${note.subject}</span>
                    <span class="note-size">${note.sizeMb} MB</span>
                </div>
            </div>
            <div class="note-action">
                ${btnHtml}
            </div>
        `;

        // Bind download/view action
        const actionBtn = card.querySelector("button");
        actionBtn.addEventListener("click", () => {
            if (isDownloaded) {
                openPdfReader(note);
            } else {
                // Simulate download
                actionBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i>`;
                actionBtn.disabled = true;
                setTimeout(() => {
                    state.downloadedNotes[note.id] = true;
                    renderPDFNotes();
                }, 1200);
            }
        });

        listContainer.appendChild(card);
    });
}

// Render Test Series Catalog
function renderTestSeriesCatalog() {
    const container = document.getElementById("test-series-list-container");
    if (!container) return;
    container.innerHTML = "";

    DB.testSeries.forEach(series => {
        const card = document.createElement("div");
        card.className = "test-series-card";
        const examName = DB.exams.find(e => e.id === series.examId)?.shortName || "Exam";
        
        card.innerHTML = `
            <div class="ts-header">
                <span class="exam-badge">${examName}</span>
                <span class="test-count-badge">${series.numberOfTests} Tests</span>
            </div>
            <h3>${series.title}</h3>
            <p>${series.description}</p>
            <div class="ts-action-link">View Tests <i class="fa-solid fa-chevron-right"></i></div>
        `;
        card.addEventListener("click", () => {
            state.activeSeries = series;
            navigateTo("test-series-details");
            renderTestSeriesDetails();
        });
        container.appendChild(card);
    });
}

// Render Test Series Details Screen
function renderTestSeriesDetails() {
    if (!state.activeSeries) return;
    document.getElementById("details-series-title").innerText = state.activeSeries.title;
    document.getElementById("details-series-desc").innerText = state.activeSeries.description;

    const container = document.getElementById("details-tests-list");
    container.innerHTML = "";

    state.activeSeries.tests.forEach(test => {
        const card = document.createElement("div");
        card.className = "individual-test-card";
        card.style.cursor = "pointer";
        card.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h4 style="margin: 0;">${test.title}</h4>
                <i class="fa-solid fa-chevron-right text-primary"></i>
            </div>
            <div class="test-stats-row" style="margin-top: 8px;">
                <span><i class="fa-regular fa-clock"></i> ${test.durationMinutes} Mins</span>
                <span><i class="fa-regular fa-file-lines"></i> ${test.totalQuestions} Questions</span>
            </div>
            <div style="font-size: 11px; font-weight: 700; color: var(--primary); margin-top: 10px;">View details, attempts & rankings</div>
        `;

        card.addEventListener("click", () => {
            showTestDetails(test);
        });

        container.appendChild(card);
    });
}

// -------------------------------------------------------------
// PDF Reader Simulator
// -------------------------------------------------------------
function openPdfReader(note) {
    state.activePdfNote = note;
    state.pdfCurrentPage = 1;
    document.getElementById("pdf-reader-title").innerText = note.title;
    updatePdfPageContent();
    document.getElementById("pdf-reader-modal").classList.add("active");
}

function updatePdfPageContent() {
    if (!state.activePdfNote) return;
    document.getElementById("pdf-page-indicator").innerText = `Page ${state.pdfCurrentPage} of ${state.pdfTotalPages}`;
    document.getElementById("pdf-page-header").innerText = `${state.activePdfNote.subject} Study Guide: Chapter ${state.pdfCurrentPage}`;
    
    document.getElementById("pdf-page-body").innerHTML = `
        This is page <strong>${state.pdfCurrentPage}</strong> of the study material for <strong>${state.activePdfNote.title}</strong>.<br><br>
        <strong>Key Pedagogy Principles:</strong><br>
        1. Conceptual learning should precede factual evaluation.<br>
        2. Leverage graphical representations and summaries for revision.<br>
        3. Solve practice questions in the Test Series section to verify retention.<br><br>
        <em>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam finibus diam at dolor sollicitudin tincidunt. Proin accumsan lorem sed magna molestie tempor.</em>
    `;
    
    document.getElementById("pdf-prev-btn").disabled = state.pdfCurrentPage === 1;
    document.getElementById("pdf-next-btn").disabled = state.pdfCurrentPage === state.pdfTotalPages;
}

// -------------------------------------------------------------
// Online Test Player Logic
// -------------------------------------------------------------
const genericQuestionsWeb = [
    {
        text: "Which of the following physical quantities has the same dimensional formula as that of impulse?",
        options: ["Force", "Linear Momentum", "Torque", "Pressure"],
        correctOpt: "B",
        explanation: "Impulse is Force * Time, which has dimensions [MLT^-1]. This is identical to the dimensional formula of linear momentum."
    },
    {
        text: "A particle moves in a circle of radius R with constant speed v. The magnitude of average acceleration during a semi-circle turn is:",
        options: ["v^2 / R", "2v^2 / (pi * R)", "v^2 / (2 * R)", "Zero"],
        correctOpt: "B",
        explanation: "Average acceleration is change in velocity divided by time: 2v / (pi*R/v) = 2v^2 / (pi*R)."
    },
    {
        text: "Which of the following organic compounds will show optical activity?",
        options: ["2-Chlorobutane", "1-Chlorobutane", "2-Chloropropane", "Butane"],
        correctOpt: "A",
        explanation: "2-Chlorobutane contains a chiral carbon atom bonded to four different groups (-H, -Cl, -CH3, -CH2CH3)."
    },
    {
        text: "The primary structure of a protein refers to:",
        options: ["Helix configuration", "Sequence of amino acids", "Three dimensional foldings", "Aggregation of sub-units"],
        correctOpt: "B",
        explanation: "The primary structure is the linear sequence of amino acids joined by peptide bonds."
    },
    {
        text: "Which cell organelle is responsible for cellular respiration and ATP generation?",
        options: ["Ribosome", "Mitochondria", "Chloroplast", "Lysosome"],
        correctOpt: "B",
        explanation: "Mitochondria are the site of aerobic respiration and generate ATP (energy currency of the cell)."
    },
    {
        text: "In angiosperms, double fertilization is characterized by:",
        options: ["Fusion of two polar nuclei", "Syngamy and triple fusion", "Fertilization of two eggs", "Fusion of tube cell and egg"],
        correctOpt: "B",
        explanation: "Double fertilization involves syngamy (fusion of one male gamete with the egg) and triple fusion (fusion of second male gamete with secondary nucleus)."
    },
    {
        text: "Which of the following is considered a primary agent of socialization for young children, especially during early childhood?",
        options: ["Mass media and community networks", "Family and immediate caregivers", "Formal school curriculum", "Peer groups and extra-curricular clubs"],
        correctOpt: "B",
        explanation: "Family is the primary agent of socialization that shapes early behavior and values in childhood."
    },
    {
        text: "In the context of cognitive development, which stage of Jean Piaget's theory matches with the ability to perform conservation tasks?",
        options: ["Sensorimotor stage (0 to 2 years)", "Pre-operational stage (2 to 7 years)", "Concrete operational stage (7 to 11 years)", "Formal operational stage (11 years and above)"],
        correctOpt: "C",
        explanation: "Concrete operational stage matches with conservation and logical operations on concrete events."
    },
    {
        text: "A teacher designs classroom tasks that require collaborative peer dialogues and scaffolding. This strategy aligns with:",
        options: ["B.F. Skinner", "Lev Vygotsky", "Jean Piaget", "Albert Bandura"],
        correctOpt: "B",
        explanation: "Lev Vygotsky's social constructivism emphasizes collaborative dialogue, ZPD, and scaffolding."
    }
];

function getDynamicQuestionWeb(qNum, targetAns) {
    const baseQ = genericQuestionsWeb[(qNum - 1) % genericQuestionsWeb.length];
    const options = ["A", "B", "C", "D"];
    const optionsList = [...baseQ.options];
    const baseCorrectIdx = options.indexOf(baseQ.correctOpt);
    const targetIdx = options.indexOf(targetAns);
    
    if (baseCorrectIdx !== -1 && targetIdx !== -1 && baseCorrectIdx !== targetIdx) {
        const temp = optionsList[targetIdx];
        optionsList[targetIdx] = optionsList[baseCorrectIdx];
        optionsList[baseCorrectIdx] = temp;
    }
    return {
        text: `[Q.${qNum}] ${baseQ.text}`,
        options: optionsList,
        explanation: baseQ.explanation
    };
}

let proctorBlurListener = null;
let proctorFullscreenListener = null;

function startProctoring() {
    state.proctorWarnings = 0;
    
    if (proctorBlurListener) window.removeEventListener("blur", proctorBlurListener);
    if (proctorFullscreenListener) document.removeEventListener("fullscreenchange", proctorFullscreenListener);
    
    proctorBlurListener = () => {
        if (state.currentScreen === "online-test-player" && document.getElementById("player-setup-panel").style.display === "none") {
            state.proctorWarnings++;
            alert(`PROCTOR SECURITY NOTICE:\nWindow focus lost or tab switched! This activity has been logged. Warnings: ${state.proctorWarnings} / 3.\nReaching 3 warnings auto-submits the exam.`);
            updateTimerDisplay();
            if (state.proctorWarnings >= 3) {
                alert("Security warnings limit exceeded. Automatically submitting your exam now.");
                submitTest("ONLINE");
            }
        }
    };
    
    proctorFullscreenListener = () => {
        const isFull = document.fullscreenElement || document.webkitFullscreenElement || document.msFullscreenElement;
        if (!isFull && state.currentScreen === "online-test-player" && document.getElementById("player-setup-panel").style.display === "none") {
            state.proctorWarnings++;
            alert(`PROCTOR SECURITY NOTICE:\nFullscreen mode exited! This activity has been logged. Warnings: ${state.proctorWarnings} / 3.\nReaching 3 warnings auto-submits the exam.`);
            updateTimerDisplay();
            if (state.proctorWarnings >= 3) {
                alert("Security warnings limit exceeded. Automatically submitting your exam now.");
                submitTest("ONLINE");
            }
        }
    };
    
    window.addEventListener("blur", proctorBlurListener);
    document.addEventListener("fullscreenchange", proctorFullscreenListener);
}

function stopProctoring() {
    if (proctorBlurListener) window.removeEventListener("blur", proctorBlurListener);
    if (proctorFullscreenListener) document.removeEventListener("fullscreenchange", proctorFullscreenListener);
    
    try {
        const isFull = document.fullscreenElement || document.webkitFullscreenElement || document.mozFullScreenElement || document.msFullscreenElement;
        if (isFull) {
            const exit = document.exitFullscreen || document.webkitExitFullscreen || document.mozCancelFullScreen || document.msExitFullscreen;
            if (exit) {
                exit.call(document).catch(err => console.log("Exit fullscreen error:", err));
            }
        }
    } catch (e) {
        console.log("Exit fullscreen catch:", e);
    }
}

function startOnlineTest(test) {
    state.activeTest = test;
    state.onlineAnswers = {};
    state.onlineReview = {};
    state.currentQuestionIndex = 0;
    state.secondsRemaining = test.durationMinutes * 60;
    state.proctorWarnings = 0;
    
    // Set Setup Instructions parameters
    document.getElementById("setup-exam-title").innerText = test.title;
    document.getElementById("setup-duration-rule").innerHTML = `Total duration is <strong>${test.durationMinutes} minutes</strong>. The countdown cannot be paused.`;
    document.getElementById("setup-declaration-checkbox").checked = false;
    document.getElementById("player-setup-start-btn").disabled = true;
    
    // Display instructions setup panel first
    document.getElementById("player-setup-panel").style.display = "flex";
    navigateTo("online-test-player");
    
    // Bind Start Button click
    const startBtn = document.getElementById("player-setup-start-btn");
    startBtn.onclick = () => {
        document.getElementById("player-setup-panel").style.display = "none";
        
        // Request fullscreen
        const elem = document.documentElement;
        if (elem.requestFullscreen) elem.requestFullscreen();
        else if (elem.webkitRequestFullscreen) elem.webkitRequestFullscreen();
        
        // Start proctor listeners & timer interval
        startProctoring();
        updateTimerDisplay();
        
        clearInterval(state.timerInterval);
        state.timerInterval = setInterval(() => {
            state.secondsRemaining--;
            updateTimerDisplay();
            if (state.secondsRemaining <= 0) {
                clearInterval(state.timerInterval);
                submitTest("ONLINE");
            }
        }, 1000);
        
        renderQuestion();
    };

    // Bind Back Button
    document.getElementById("player-setup-back-btn").onclick = () => {
        showTestDetails(test);
    };
}

function updateTimerDisplay() {
    const el = document.getElementById("player-timer");
    if (!el) return;
    
    const m = Math.floor(state.secondsRemaining / 60);
    const s = state.secondsRemaining % 60;
    const formatted = `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    const warningsText = state.proctorWarnings > 0 ? ` | Warnings: ${state.proctorWarnings}/3` : "";
    
    el.innerText = `Time Left: ${formatted}${warningsText}`;
    
    if (state.secondsRemaining < 300) { // less than 5 minutes
        el.classList.add("red");
    } else {
        el.classList.remove("red");
    }
}

function renderQuestion() {
    if (!state.activeTest) return;
    const qNum = state.currentQuestionIndex + 1;
    const targetAns = state.activeTest.answerKey[qNum] || "A";
    const qData = getDynamicQuestionWeb(qNum, targetAns);
    
    document.getElementById("player-question-number").innerText = `Question ${qNum} of ${state.activeTest.totalQuestions}`;
    document.getElementById("mark-review-checkbox").checked = !!state.onlineReview[qNum];
    document.getElementById("player-question-body").innerText = qData.text;

    // Options
    const options = ["A", "B", "C", "D"];
    const optionsContainer = document.getElementById("player-options-list");
    optionsContainer.innerHTML = "";

    options.forEach((opt, idx) => {
        const card = document.createElement("div");
        const isSelected = state.onlineAnswers[qNum] === opt;
        card.className = `option-card ${isSelected ? 'selected' : ''}`;
        card.innerHTML = `
            <div class="option-circle">${opt}</div>
            <span class="option-text">${qData.options[idx]}</span>
        `;
        card.addEventListener("click", () => {
            state.onlineAnswers[qNum] = opt;
            renderQuestion();
        });
        optionsContainer.appendChild(card);
    });

    // Add Clear Selection Button
    if (state.onlineAnswers[qNum]) {
        const clearBtn = document.createElement("button");
        clearBtn.innerText = "Clear Selection";
        clearBtn.style.background = "none";
        clearBtn.style.border = "none";
        clearBtn.style.color = "var(--danger)";
        clearBtn.style.fontWeight = "700";
        clearBtn.style.cursor = "pointer";
        clearBtn.style.marginTop = "8.dp";
        clearBtn.style.float = "right";
        clearBtn.onclick = () => {
            delete state.onlineAnswers[qNum];
            renderQuestion();
        };
        optionsContainer.appendChild(clearBtn);
    }

    // Prev/Next button states
    document.getElementById("player-prev-btn").disabled = state.currentQuestionIndex === 0;
    document.getElementById("player-next-btn").disabled = state.currentQuestionIndex === state.activeTest.totalQuestions - 1;
}

function submitTest(attemptType) {
    if (!state.activeTest) return;
    
    clearInterval(state.timerInterval);
    
    // Grade the test
    let correct = 0;
    let incorrect = 0;
    let skipped = 0;
    let doubleMarked = 0;
    const test = state.activeTest;

    for (let q = 1; q <= test.totalQuestions; q++) {
        const submitted = state.onlineAnswers[q];
        const correctAns = test.answerKey[q];
        
        if (!submitted || submitted === '') {
            skipped++;
        } else if (submitted === 'MULTIPLE') {
            doubleMarked++;
            incorrect++;
        } else if (submitted.toUpperCase() === correctAns.toUpperCase()) {
            correct++;
        } else {
            incorrect++;
        }
    }

    const now = new Date();
    const dateStr = `${now.getFullYear()}-${(now.getMonth()+1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;

    const record = {
        id: "att_" + Math.random().toString(36).substring(2, 10),
        testId: test.id,
        testTitle: test.title,
        attemptType: attemptType, // "ONLINE" or "OMR"
        dateString: dateStr,
        marksObtained: correct,
        totalMarks: test.totalQuestions,
        correctAnswers: correct,
        incorrectAnswers: incorrect,
        skippedAnswers: skipped,
        doubleMarkedAnswers: doubleMarked,
        answers: { ...state.onlineAnswers },
        scannedOmrUrl: attemptType === "OMR" ? (state.scannedOmrUrl || null) : null
    };

    state.scannedOmrUrl = null; // Clear state variable

    saveAttempt(record);
    showScorecard(record);
}

function showScorecard(record) {
    stopProctoring();

    // Restore activeTest reference matching record
    if (record.testId) {
        let foundTest = null;
        for (let series of (DB.testSeries || [])) {
            for (let t of (series.tests || [])) {
                if (t.id === record.testId) {
                    foundTest = t;
                    break;
                }
            }
            if (foundTest) break;
        }
        if (foundTest) {
            state.activeTest = foundTest;
        }
    }

    // Restore onlineAnswers state for detailed review mode
    if (record.answers) {
        state.onlineAnswers = record.answers;
    } else {
        // Fallback: populate simulated answers based on correct/incorrect counts
        const test = state.activeTest || { totalQuestions: record.totalMarks || 30, answerKey: {} };
        const simulated = {};
        let correctLeft = record.correctAnswers || 0;
        let incorrectLeft = record.incorrectAnswers || 0;
        const options = ["A", "B", "C", "D"];
        for (let q = 1; q <= test.totalQuestions; q++) {
            const correctAns = test.answerKey[q] || "A";
            if (correctLeft > 0) {
                simulated[q] = correctAns;
                correctLeft--;
            } else if (incorrectLeft > 0) {
                simulated[q] = options.find(o => o !== correctAns);
                incorrectLeft--;
            } else {
                simulated[q] = "";
            }
        }
        state.onlineAnswers = simulated;
    }

    document.getElementById("result-test-title").innerText = record.testTitle;
    document.getElementById("result-score-value").innerText = `${record.marksObtained} / ${record.totalMarks}`;
    const pct = ((record.marksObtained / record.totalMarks) * 100).toFixed(1);
    document.getElementById("result-percentage").innerText = `Percentage: ${pct}%`;
    
    // Percentile calculations
    const pctNum = parseFloat(pct);
    const percentile = Math.max(12.4, Math.min(99.6, (pctNum * 0.95 + 4.2))).toFixed(1);
    const topPercent = (100 - parseFloat(percentile)).toFixed(1);
    document.getElementById("result-percentile-box").innerText = `Percentile: ${percentile}% (Top ${topPercent}%)`;

    // Visual Accuracy Gauge Update
    const attempted = record.correctAnswers + record.incorrectAnswers;
    const accuracy = attempted > 0 ? (record.correctAnswers / attempted) * 100 : 0;
    document.getElementById("scorecard-accuracy-fill").setAttribute("stroke-dasharray", `${accuracy.toFixed(0)}, 100`);
    document.getElementById("scorecard-accuracy-pct").innerText = `${accuracy.toFixed(1)}%`;

    // Score Comparison Bar Chart Update
    const scoredMarks = record.correctAnswers * 4 - record.incorrectAnswers * 1;
    const maxScore = record.totalMarks * 4;
    const compScoreRatio = Math.max(0, Math.min(100, (scoredMarks / maxScore) * 100));
    document.getElementById("comp-student-score").innerText = `${scoredMarks} / ${maxScore}`;
    document.getElementById("comp-student-bar").style.width = `${compScoreRatio}%`;

    document.getElementById("result-mode-badge").innerText = `${record.attemptType} Attempt`;
    document.getElementById("result-mode-badge").className = `mode-badge ${record.attemptType.toLowerCase()}`;
    document.getElementById("result-correct-count").innerText = record.correctAnswers;
    document.getElementById("result-incorrect-count").innerText = record.incorrectAnswers;
    document.getElementById("result-skipped-count").innerText = record.skippedAnswers;
    document.getElementById("result-double-count").innerText = record.doubleMarkedAnswers || 0;
    document.getElementById("result-timestamp").innerText = `Exam Date: ${record.dateString}`;
    
    // Render Scanned OMR image preview if available
    const previewCard = document.getElementById("result-omr-preview-card");
    const previewImg = document.getElementById("result-omr-preview-img");
    if (previewCard && previewImg) {
        if (record.attemptType === "OMR" && record.scannedOmrUrl) {
            previewImg.src = record.scannedOmrUrl;
            previewCard.style.display = "block";
        } else {
            previewImg.src = "";
            previewCard.style.display = "none";
        }
    }
    
    navigateTo("omr-result");
}

function renderTestReview() {
    const test = state.activeTest;
    if (!test) return;

    // Set stats & title
    document.getElementById("review-title").innerText = test.title;
    
    // Count stats again from state.onlineAnswers
    let correct = 0;
    let incorrect = 0;
    let skipped = 0;
    let doubleMarked = 0;

    for (let q = 1; q <= test.totalQuestions; q++) {
        const submitted = state.onlineAnswers[q];
        const correctAns = test.answerKey[q];
        if (!submitted || submitted === '') {
            skipped++;
        } else if (submitted === 'MULTIPLE') {
            doubleMarked++;
            incorrect++;
        } else if (submitted.toUpperCase() === correctAns.toUpperCase()) {
            correct++;
        } else {
            incorrect++;
        }
    }

    document.getElementById("rev-correct-count").innerText = correct;
    document.getElementById("rev-incorrect-count").innerText = incorrect;
    document.getElementById("rev-skipped-count").innerText = skipped;
    document.getElementById("rev-double-count").innerText = doubleMarked;

    // Render Filter Chips dynamically
    const filterContainer = document.getElementById("review-filter-chips");
    filterContainer.innerHTML = "";
    
    const filters = [
        { key: "ALL", label: `All (${test.totalQuestions})` },
        { key: "CORRECT", label: `Correct (${correct})` },
        { key: "INCORRECT", label: `Incorrect (${incorrect})` },
        { key: "SKIPPED", label: `Skipped (${skipped})` }
    ];

    state.activeReviewFilter = state.activeReviewFilter || "ALL";

    filters.forEach(f => {
        const btn = document.createElement("button");
        const isActive = state.activeReviewFilter === f.key;
        btn.className = `filter-chip ${isActive ? 'active' : ''}`;
        btn.innerText = f.label;
        btn.style.padding = "6px 14px";
        btn.style.fontSize = "11.5px";
        btn.style.fontWeight = "700";
        btn.style.border = "none";
        btn.style.borderRadius = "20px";
        btn.style.cursor = "pointer";
        btn.style.backgroundColor = isActive ? "var(--primary)" : "var(--surface-variant)";
        btn.style.color = isActive ? "var(--on-primary)" : "var(--on-surface-variant)";
        btn.onclick = () => {
            state.activeReviewFilter = f.key;
            renderTestReview(); // Re-render to refresh highlights and filtered list
        };
        filterContainer.appendChild(btn);
    });

    const listContainer = document.getElementById("review-questions-list");
    listContainer.innerHTML = "";

    for (let q = 1; q <= test.totalQuestions; q++) {
        const correctAns = test.answerKey[q] || "A";
        const submitted = state.onlineAnswers[q] || "";
        
        // Filter check
        let matchesFilter = true;
        if (state.activeReviewFilter === "CORRECT") {
            matchesFilter = submitted.toUpperCase() === correctAns.toUpperCase();
        } else if (state.activeReviewFilter === "INCORRECT") {
            matchesFilter = submitted !== "" && submitted !== "None" && submitted.toUpperCase() !== correctAns.toUpperCase();
        } else if (state.activeReviewFilter === "SKIPPED") {
            matchesFilter = submitted === "" || submitted === "None";
        }

        if (!matchesFilter) continue;

        const qData = getDynamicQuestionWeb(q, correctAns);

        // Status badge
        let badgeClass = "skipped";
        let badgeText = "Skipped";
        if (submitted === 'MULTIPLE') {
            badgeClass = "double";
            badgeText = "Double Marked";
        } else if (submitted !== "") {
            if (submitted.toUpperCase() === correctAns.toUpperCase()) {
                badgeClass = "correct";
                badgeText = "Correct";
            } else {
                badgeClass = "incorrect";
                badgeText = "Incorrect";
            }
        }

        // Options rendering
        const optionsList = ["A", "B", "C", "D"];
        let optionsHTML = "";
        optionsList.forEach((opt, idx) => {
            let optClass = "";
            let optLabelText = qData.options[idx];

            if (opt === correctAns) {
                optClass = "correct-answer";
                optLabelText = `${qData.options[idx]} (Correct Answer)`;
            }
            if (submitted !== 'MULTIPLE' && opt === submitted && submitted !== correctAns) {
                optClass = "wrong-selected";
                optLabelText = `${qData.options[idx]} (Your Selection)`;
            }
            if (submitted === 'MULTIPLE') {
                optClass = "double-selected";
            }

            optionsHTML += `
                <div class="review-option-item ${optClass}">
                    <span class="review-opt-letter">${opt}</span>
                    <span>${optLabelText}</span>
                </div>
            `;
        });

        const card = document.createElement("div");
        card.className = "review-q-card";
        card.innerHTML = `
            <div class="review-q-header">
                <span class="review-q-num" style="color: var(--primary);">Question ${q}</span>
                <span class="review-status-badge ${badgeClass}">${badgeText}</span>
            </div>
            <div class="review-q-text">${qData.text}</div>
            <div class="review-options-list">
                ${optionsHTML}
            </div>
            <button class="review-explanation-btn" data-q="${q}">
                <span><i class="fa-solid fa-circle-info"></i> View Solution & Explanation</span>
                <i class="fa-solid fa-chevron-down" style="transition: transform 0.2s;"></i>
            </button>
            <div class="review-explanation-content" id="exp-content-${q}">
                <strong>Correct Answer: ${correctAns}</strong><br>
                Explanation: ${qData.explanation}
            </div>
        `;
        listContainer.appendChild(card);
    }

    // Bind expansion toggles
    document.querySelectorAll(".review-explanation-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const qNum = btn.getAttribute("data-q");
            const expContent = document.getElementById(`exp-content-${qNum}`);
            const arrow = btn.querySelector(".fa-chevron-down");
            if (expContent.classList.contains("active")) {
                expContent.classList.remove("active");
                arrow.style.transform = "rotate(0deg)";
            } else {
                expContent.classList.add("active");
                arrow.style.transform = "rotate(180deg)";
            }
        });
    });

    // Populate question paper reader inside Review screen
    const reviewViewer = document.getElementById("review-pdf-mock-viewer");
    if (reviewViewer) {
        document.getElementById("review-pdf-viewer-container").style.display = "none";
        
        let paperHtml = `<div style="text-align:center; border-bottom:1.5px solid var(--outline); padding-bottom:12px; margin-bottom:16px;">
            <h3 style="margin:0; font-size:14px; color:var(--primary); font-weight:700;">Question Paper: ${test.title}</h3>
            <span style="font-size:10px; color:var(--on-surface-variant); opacity:0.8;">Full Marks: ${test.totalQuestions} | Duration: ${test.durationMinutes} Mins</span>
        </div>`;
        
        for (let i = 1; i <= test.totalQuestions; i++) {
            const correctAns = test.answerKey[i] || "A";
            const qData = getDynamicQuestionWeb(i, correctAns);
            paperHtml += `<div style="margin-bottom:16px; border-bottom: 1px dashed var(--outline); padding-bottom:10px;">
                <p style="font-weight:700; margin:0 0 6px 0; color: var(--on-surface);">${qData.text}</p>
                <ul style="margin:0; padding-left:20px; list-style-type: upper-alpha; color: var(--on-surface-variant); font-size:11px;">
                    <li style="margin-bottom:2px;">${qData.options[0]}</li>
                    <li style="margin-bottom:2px;">${qData.options[1]}</li>
                    <li style="margin-bottom:2px;">${qData.options[2]}</li>
                    <li style="margin-bottom:2px;">${qData.options[3]}</li>
                </ul>
            </div>`;
        }
        reviewViewer.innerHTML = paperHtml;
    }
}

function getLeaderboardData() {
    const allTests = [];
    DB.testSeries.forEach(series => {
        series.tests.forEach(test => {
            allTests.push(test);
        });
    });

    if (!state.selectedLeaderboardTestId && allTests.length > 0) {
        state.selectedLeaderboardTestId = allTests[0].id;
    }

    const testId = state.selectedLeaderboardTestId;
    const test = allTests.find(t => t.id === testId);
    const testTotal = test ? test.totalQuestions : 30;

    // Find active user's attempts for this specific test
    const attempts = state.attempts || [];
    const userAttempt = attempts.find(a => a.testId === testId);

    // Generate test-specific ranks
    const mockUsers = [
        { name: "Pooja Sharma", score: Math.round(testTotal * 0.96), group: "ctet", isAttempted: true },
        { name: "Rahul Verma", score: Math.round(testTotal * 0.93), group: "ugc-net", isAttempted: true },
        { name: "Siddharth Rao", score: Math.round(testTotal * 0.90), group: "ctet", isAttempted: true },
        { name: "Vikram Malhotra", score: Math.round(testTotal * 0.86), group: "ctet", isAttempted: true },
        { name: "Neha Deshmukh", score: Math.round(testTotal * 0.83), group: "ugc-net", isAttempted: true },
        { name: "Aditya Roy", score: Math.round(testTotal * 0.80), group: "ctet", isAttempted: true },
        { name: "Meera Nair", score: Math.round(testTotal * 0.76), group: "ugc-net", isAttempted: true },
        { name: "Suresh Patil", score: Math.round(testTotal * 0.70), group: "ugc-net", isAttempted: true },
        { name: "Kirti Sen", score: Math.round(testTotal * 0.63), group: "ctet", isAttempted: true }
    ];

    if (userAttempt) {
        mockUsers.push({
            name: "Amit Sharma",
            score: userAttempt.marksObtained,
            group: "ctet",
            isCurrentUser: true,
            isAttempted: true
        });
    } else {
        // Show simulated rank for current user
        mockUsers.push({
            name: "Amit Sharma",
            score: Math.round(testTotal * 0.73),
            group: "ctet",
            isCurrentUser: true,
            isAttempted: false
        });
    }

    // Sort descending
    mockUsers.sort((a, b) => b.score - a.score);

    // Add rank numbers
    return mockUsers.map((user, idx) => ({
        ...user,
        rank: idx + 1
    }));
}

function showTestDetails(test) {
    state.activeTest = test;
    state.selectedLeaderboardTestId = test.id;

    // Fill metadata
    document.getElementById("mock-detail-title").innerText = test.title;
    document.getElementById("mock-detail-duration").innerText = `${test.durationMinutes} Mins`;
    document.getElementById("mock-detail-questions").innerText = `${test.totalQuestions} Qs`;
    document.getElementById("mock-detail-marks").innerText = `${test.totalQuestions} Marks`;
    document.getElementById("mock-detail-cutoff").innerText = `${Math.round(test.totalQuestions * 0.7)} Marks`;

    // Bind attempt buttons
    const btnOmr = document.getElementById("detail-btn-omr");
    const btnOnline = document.getElementById("detail-btn-online");
    const btnDownload = document.getElementById("detail-btn-download-omr");

    // Remove old listeners by cloning
    const newBtnOmr = btnOmr.cloneNode(true);
    const newBtnOnline = btnOnline.cloneNode(true);
    const newBtnDownload = btnDownload.cloneNode(true);
    btnOmr.parentNode.replaceChild(newBtnOmr, btnOmr);
    btnOnline.parentNode.replaceChild(newBtnOnline, btnOnline);
    btnDownload.parentNode.replaceChild(newBtnDownload, btnDownload);

    newBtnOnline.addEventListener("click", () => {
        startOnlineTest(test);
    });

    newBtnOmr.addEventListener("click", () => {
        openOmrPrep(test);
    });

    newBtnDownload.addEventListener("click", () => {
        window.open(`printOMR.html?testId=${test.id}&qCount=${test.totalQuestions}&title=${encodeURIComponent(test.title)}`, "_blank");
    });

    // Populate attempts list
    const attempts = state.attempts || [];
    const testAttempts = attempts.filter(a => a.testId === test.id);
    const attemptsContainer = document.getElementById("mock-detail-attempts-list");
    attemptsContainer.innerHTML = "";

    if (testAttempts.length === 0) {
        attemptsContainer.innerHTML = `<span style="color: var(--on-surface-variant); opacity: 0.65;">You haven't attempted this mock test yet. Take a test to view your scorecard log.</span>`;
    } else {
        testAttempts.forEach((attempt, index) => {
            const row = document.createElement("div");
            row.style.display = "flex";
            row.style.justifyContent = "space-between";
            row.style.padding = "8px 0";
            row.style.borderBottom = index < testAttempts.length - 1 ? "1px solid var(--outline)" : "none";
            row.innerHTML = `
                <div>
                    <strong style="color: var(--on-surface);">Attempt #${testAttempts.length - index} (${attempt.attemptType})</strong>
                    <div style="font-size: 10px; color: var(--on-surface-variant); opacity: 0.6;">${attempt.dateString}</div>
                </div>
                <strong style="color: ${attempt.marksObtained >= test.totalQuestions * 0.7 ? '#2e7d32' : '#c62828'};">${attempt.marksObtained} / ${attempt.totalMarks} Marks</strong>
            `;
            attemptsContainer.appendChild(row);
        });
    }

    // Populate warning banner visibility
    const warningEl = document.getElementById("mock-detail-unattempted-warning");
    if (warningEl) {
        warningEl.style.display = testAttempts.length > 0 ? "none" : "block";
    }

    // Render ranks inline
    renderTestDetailsRankings();

    navigateTo("test-details");
}

function renderTestDetailsRankings() {
    const testId = state.selectedLeaderboardTestId;
    const allTests = [];
    DB.testSeries.forEach(series => {
        series.tests.forEach(test => {
            allTests.push(test);
        });
    });

    const test = allTests.find(t => t.id === testId);
    const testTotal = test ? test.totalQuestions : 30;

    const data = getLeaderboardData();

    // 1. Render podium (Ranks 1, 2, 3)
    const podiumContainer = document.getElementById("leaderboard-podium");
    podiumContainer.innerHTML = "";

    const podiumUsers = data.slice(0, 3);
    const podiumOrder = [];
    const secondUser = podiumUsers.find(u => u.rank === 2);
    const firstUser = podiumUsers.find(u => u.rank === 1);
    const thirdUser = podiumUsers.find(u => u.rank === 3);

    if (secondUser) podiumOrder.push(secondUser);
    if (firstUser) podiumOrder.push(firstUser);
    if (thirdUser) podiumOrder.push(thirdUser);

    podiumOrder.forEach(user => {
        const initials = user.name.split(" ").map(n => n[0]).join("");
        const isCurrentUser = user.isCurrentUser ? "highlighted" : "";
        const crown = user.rank === 1 ? `<div class="podium-crown"><i class="fa-solid fa-crown"></i></div>` : "";
        const rankClass = user.rank === 1 ? "first" : user.rank === 2 ? "second" : "third";

        const podiumDiv = document.createElement("div");
        podiumDiv.className = `podium-item ${rankClass} ${isCurrentUser}`;
        podiumDiv.innerHTML = `
            <div class="podium-avatar-box">
                ${crown}
                <span>${initials}</span>
            </div>
            <div class="podium-name">${user.name}</div>
            <div class="podium-score" style="font-weight:800; color: var(--primary);">${user.score} / ${testTotal} Marks</div>
            <div class="podium-pill">${user.rank}</div>
        `;
        podiumContainer.appendChild(podiumDiv);
    });

    // 2. Render ranking list (Ranks 4-10)
    const listContainer = document.getElementById("leaderboard-list");
    listContainer.innerHTML = "";

    const listUsers = data.slice(3);
    listUsers.forEach(user => {
        const initials = user.name.split(" ").map(n => n[0]).join("");
        const isCurrentUser = user.isCurrentUser ? "highlighted" : "";

        const row = document.createElement("div");
        row.className = `rank-row ${isCurrentUser}`;
        row.innerHTML = `
            <div class="rank-number">${user.rank}</div>
            <div class="rank-avatar">${initials}</div>
            <div class="rank-details">
                <div class="rank-name">${user.name} ${user.isCurrentUser ? '<span class="badge self" style="background-color: var(--primary); color: #fff; font-size:9px; padding: 2px 6px; border-radius: 4px; margin-left: 6px;">You</span>' : ''}</div>
                <div class="rank-sub-details">${user.isAttempted ? 'Attempted' : 'Not Attempted'}</div>
            </div>
            <div class="rank-score-val" style="font-weight: 800; color: var(--primary);">${user.score} / ${testTotal}</div>
        `;
        listContainer.appendChild(row);
    });

    // 3. Render sticky my rank banner
    const myUser = data.find(u => u.isCurrentUser);
    const myRankBanner = document.getElementById("leaderboard-my-rank-banner");
    if (myUser) {
        const myInitials = myUser.name.split(" ").map(n => n[0]).join("");
        const userAttempt = (state.attempts || []).find(a => a.testId === testId);
        const scoreStr = userAttempt ? `${myUser.score} / ${testTotal} Marks` : "Not Attempted";
        myRankBanner.innerHTML = `
            <div class="my-rank-banner-info">
                <div class="rank-avatar" style="background-color: var(--primary); color: #fff; width:36px; height:36px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-weight:700;">${myInitials}</div>
                <div class="my-rank-banner-details" style="margin-left: 10px;">
                    <h4 style="margin:0; font-size:13px; color: var(--on-primary-container);">Amit Sharma (Rank #${myUser.rank})</h4>
                    <span style="font-size:10px; color: var(--on-primary-container); opacity:0.8;">Score: ${scoreStr}</span>
                </div>
            </div>
            <div class="rank-score-val" style="font-size: 16px; color: var(--on-primary-container);">Top ${Math.round(((data.length - myUser.rank + 1) / data.length) * 100)}%</div>
        `;
    }
}

// -------------------------------------------------------------
// OMR Attempt Mode (Prep, Scanner, Cam Access)
// -------------------------------------------------------------
function openOmrPrep(test) {
    state.activeTest = test;
    document.getElementById("omr-prep-title").innerText = test.title;
    
    // Reset file downloaded states and hide PDF panel
    document.getElementById("btn-download-paper").innerHTML = `<i class="fa-solid fa-download"></i>`;
    document.getElementById("btn-download-omr").innerHTML = `<i class="fa-solid fa-download"></i>`;
    document.getElementById("omr-pdf-viewer-container").style.display = "none";
    
    const isPyq = !!test.isPyq;
    const btnDownloadPaper = document.getElementById("btn-download-paper");
    const btnPreviewPaper = document.getElementById("btn-preview-paper");
    const stepsListEl = document.querySelector("#screen-omr-scan-prep .steps-list");
    
    if (isPyq) {
        btnDownloadPaper.style.display = "none";
        btnPreviewPaper.style.width = "100%";
        btnPreviewPaper.innerHTML = `<i class="fa-solid fa-eye"></i> Open secure paper PDF (In-App Only)`;
        btnPreviewPaper.style.padding = "0 16px";
        btnPreviewPaper.style.fontSize = "11px";
        btnPreviewPaper.style.fontWeight = "bold";
        btnPreviewPaper.style.borderRadius = "8px";
        
        stepsListEl.innerHTML = `
            <li><span class="step-num">1</span> Click 'Open secure paper PDF' above to read the PYQ paper in the app's secure viewer (cannot be saved or printed).</li>
            <li><span class="step-num">2</span> Download and print the generic Bubble OMR sheet.</li>
            <li><span class="step-num">3</span> Attempt the test offline by marking bubbles on the printed OMR sheet.</li>
            <li><span class="step-num">4</span> Open this app's camera scanner, align the OMR sheet inside the frame, and scan.</li>
        `;
    } else {
        btnDownloadPaper.style.display = "block";
        btnPreviewPaper.style.width = "";
        btnPreviewPaper.innerHTML = `<i class="fa-solid fa-eye"></i>`;
        btnPreviewPaper.style.padding = "";
        btnPreviewPaper.style.fontSize = "";
        btnPreviewPaper.style.fontWeight = "";
        btnPreviewPaper.style.borderRadius = "";
        
        stepsListEl.innerHTML = `
            <li><span class="step-num">1</span> Download & Print both the Question Paper PDF and the custom Bubble OMR sheet.</li>
            <li><span class="step-num">2</span> Attempt the test offline by marking bubbles on the printed OMR sheet with a black/blue pen.</li>
            <li><span class="step-num">3</span> Open this app's camera scanner, align the OMR sheet inside the frame, and scan.</li>
            <li><span class="step-num">4</span> The app immediately processes the markings and uploads details to your Profile.</li>
        `;
    }
    
    // Pre-render the question list
    renderMockQuestionPaper(test);
    
    navigateTo("omr-scan-prep");
}

function renderMockQuestionPaper(test) {
    const viewer = document.getElementById("pdf-mock-viewer");
    if (!viewer) return;
    viewer.innerHTML = "";

    const questionsCount = test.totalQuestions || 30;
    
    let html = `<div style="text-align:center; border-bottom:1.5px solid var(--outline); padding-bottom:12px; margin-bottom:16px;">
        <h3 style="margin:0; font-size:14px; color:var(--primary); font-weight: 700;">${test.title}</h3>
        <span style="font-size:10px; color:var(--on-surface-variant); opacity:0.8;">Full Marks: ${test.totalMarks} | Duration: ${test.durationMins} Mins | Questions: ${questionsCount}</span>
    </div>`;

    const mockPrompts = [
        { q: "Which of the following is a primary agent of socialization for children?", a: ["Family", "School", "Peer Group", "Media"] },
        { q: "According to Jean Piaget, in which stage of cognitive development do children develop object permanence?", a: ["Sensorimotor Stage", "Preoperational Stage", "Concrete Operational Stage", "Formal Operational Stage"] },
        { q: "In the context of progressive education, which of the following statements is correct?", a: ["Students should be active problem solvers.", "Classrooms should be democratic.", "Emphasis is on rote memory.", "Both A and B"] },
        { q: "A child learns that a dog has four legs, fur, and barks. When he sees a cat, he calls it a dog. This is an example of:", a: ["Assimilation", "Accommodation", "Schema", "Conservation"] },
        { q: "Which learning theorist proposed the concept of 'Zone of Proximal Development' (ZPD)?", a: ["Lev Vygotsky", "B.F. Skinner", "Albert Bandura", "Jerome Bruner"] }
    ];

    for (let i = 1; i <= questionsCount; i++) {
        const prompt = mockPrompts[(i - 1) % mockPrompts.length];
        html += `<div class="mock-question-item" style="margin-bottom:16px;">
            <p style="font-weight:700; margin:0 0 6px 0;">Q.${i} ${prompt.q}</p>
            <ol style="margin:0; padding-left:20px; list-style-type: upper-alpha;">
                <li style="margin-bottom:4px; padding-left:4px;">${prompt.a[0]}</li>
                <li style="margin-bottom:4px; padding-left:4px;">${prompt.a[1]}</li>
                <li style="margin-bottom:4px; padding-left:4px;">${prompt.a[2]}</li>
                <li style="margin-bottom:4px; padding-left:4px;">${prompt.a[3]}</li>
            </ol>
        </div>`;
    }
    viewer.innerHTML = html;
}

// Camera Access Setup
let stableFrames = 0;
let lockStartTime = null;
let prevCorners = null;
let isAnalysisRunning = false;
let analysisFrameId = null;

function runCameraAnalysisLoop() {
    if (!state.videoStream) {
        isAnalysisRunning = false;
        return;
    }

    const video = document.getElementById("webcam-feed");
    const canvas = document.getElementById("scanner-canvas");
    if (!video || !canvas) {
        analysisFrameId = requestAnimationFrame(runCameraAnalysisLoop);
        return;
    }

    if (video.readyState >= 2 && video.videoWidth > 0 && video.videoHeight > 0) {
        const vW = video.videoWidth;
        const vH = video.videoHeight;

        if (canvas.width !== vW || canvas.height !== vH) {
            canvas.width = vW;
            canvas.height = vH;
        }

        const ctx = canvas.getContext("2d");
        if (ctx) {
            ctx.clearRect(0, 0, vW, vH);

            // Run OMR corner detection if OpenCV and helper are loaded
            if (window.cv && typeof window.cv === 'object' && typeof window.findOMRSheetCornersLive === 'function') {
                try {
                    const corners = window.findOMRSheetCornersLive(video);
                    if (corners && corners.length === 4) {
                        // 1. Draw bounding green OMR sheet polygon
                        ctx.beginPath();
                        ctx.moveTo(corners[0].x, corners[0].y);
                        ctx.lineTo(corners[1].x, corners[1].y);
                        ctx.lineTo(corners[2].x, corners[2].y);
                        ctx.lineTo(corners[3].x, corners[3].y);
                        ctx.closePath();
                        
                        ctx.strokeStyle = '#10b981'; // glowing emerald green
                        ctx.lineWidth = 6;
                        ctx.lineJoin = 'round';
                        ctx.stroke();
                        
                        ctx.fillStyle = 'rgba(16, 185, 129, 0.15)';
                        ctx.fill();
                        
                        // 2. Draw 4 corner tracker dots
                        corners.forEach(pt => {
                            ctx.beginPath();
                            ctx.arc(pt.x, pt.y, 12, 0, 2 * Math.PI);
                            ctx.fillStyle = '#10b981';
                            ctx.fill();
                            ctx.strokeStyle = '#ffffff';
                            ctx.lineWidth = 3;
                            ctx.stroke();
                        });

                        // 3. Motion stability evaluation
                        let isMoving = false;
                        if (prevCorners && prevCorners.length === 4) {
                            let totalDistance = 0;
                            for (let i = 0; i < 4; i++) {
                                const dx = corners[i].x - prevCorners[i].x;
                                const dy = corners[i].y - prevCorners[i].y;
                                totalDistance += Math.sqrt(dx * dx + dy * dy);
                            }
                            const avgDistance = totalDistance / 4;
                            if (avgDistance > 6) {
                                isMoving = true;
                            }
                        }
                        prevCorners = corners;

                        if (isMoving) {
                            stableFrames = 0;
                            lockStartTime = null;
                            document.getElementById("scanner-status-text").innerText = "Hold steady... scanning";
                        } else {
                            if (lockStartTime === null) {
                                lockStartTime = Date.now();
                            }

                            const elapsed = Date.now() - lockStartTime;
                            const progress = Math.min(1.0, elapsed / 1000); // 1-second hold lock

                            // Render circular progress countdown inside viewport center
                            const cx = vW / 2;
                            const cy = vH / 2;
                            const r = 40;

                            ctx.beginPath();
                            ctx.arc(cx, cy, r, 0, 2 * Math.PI);
                            ctx.strokeStyle = 'rgba(255, 255, 255, 0.25)';
                            ctx.lineWidth = 8;
                            ctx.stroke();

                            ctx.beginPath();
                            ctx.arc(cx, cy, r, -Math.PI / 2, -Math.PI / 2 + progress * 2 * Math.PI);
                            ctx.strokeStyle = '#10b981';
                            ctx.lineWidth = 8;
                            ctx.lineCap = 'round';
                            ctx.stroke();

                            if (progress >= 1.0) {
                                // Auto-capture lock!
                                lockStartTime = null;
                                prevCorners = null;
                                simulateOmrScan(); // grade scanned frames
                                return;
                            }
                        }
                    } else {
                        lockStartTime = null;
                        document.getElementById("scanner-status-text").innerText = "Align the 4 corner marks in frame";
                    }
                } catch (err) {
                    console.warn("Live OMR tracker exception:", err);
                }
            }
        }
    }

    analysisFrameId = requestAnimationFrame(runCameraAnalysisLoop);
}

async function startCamera() {
    const video = document.getElementById("webcam-feed");
    if (!video) return;

    try {
        const stream = await navigator.mediaDevices.getUserMedia({
            video: { facingMode: "environment" },
            audio: false
        });
        state.videoStream = stream;
        video.srcObject = stream;
        video.setAttribute("playsinline", true);
        video.play();
        document.getElementById("scanner-status-text").innerText = "Webcam aligned. Position OMR sheet.";
        
        // Start live scanner analysis loop
        if (!isAnalysisRunning) {
            isAnalysisRunning = true;
            lockStartTime = null;
            prevCorners = null;
            analysisFrameId = requestAnimationFrame(runCameraAnalysisLoop);
        }
    } catch (err) {
        console.error("Camera access error:", err);
        // Fallback for developer simulation without camera hardware:
        document.getElementById("scanner-status-text").innerText = "Camera API blocked/not found. Running Simulator.";
    }
}

function stopCamera() {
    if (state.videoStream) {
        state.videoStream.getTracks().forEach(track => track.stop());
        state.videoStream = null;
    }
    const video = document.getElementById("webcam-feed");
    if (video) video.srcObject = null;
    
    // Stop corner tracking loops
    if (analysisFrameId) {
        cancelAnimationFrame(analysisFrameId);
        analysisFrameId = null;
    }
    isAnalysisRunning = false;
    
    // Clear overlay canvas
    const canvas = document.getElementById("scanner-canvas");
    if (canvas) {
        const ctx = canvas.getContext("2d");
        if (ctx) ctx.clearRect(0, 0, canvas.width, canvas.height);
    }
}

// Simulating the OMR Scanning progress bar
function simulateOmrScan() {
    const video = document.getElementById("webcam-feed");
    const scanBtn = document.getElementById("capture-scan-btn");
    const progressRow = document.getElementById("scan-progress-bar");
    const progressFill = document.getElementById("scan-progress-fill");
    const statusText = document.getElementById("scanner-status-text");
    const overlay = document.getElementById("scanner-overlay");

    // Failsafe: Run simulator if no active camera stream is detected
    if (!video || !state.videoStream || video.readyState < 2) {
        console.log("No active webcam stream found. Running simulation fallback...");
        runMockOmrScan();
        return;
    }

    // Failsafe: Run simulator if OpenCV is not loaded yet
    if (!window.cv || typeof window.cv !== 'object') {
        console.warn("OpenCV.js not loaded. Running simulation fallback...");
        runMockOmrScan();
        return;
    }

    // Begin real camera frame capture & computer vision analysis
    captureAndScanOMR(video, scanBtn, progressRow, progressFill, statusText, overlay);
}

async function captureAndScanOMR(video, scanBtn, progressRow, progressFill, statusText, overlay) {
    scanBtn.style.display = "none";
    progressRow.style.display = "block";
    overlay.classList.add("active");
    progressFill.style.width = "15%";
    statusText.innerText = "Snapping webcam frame...";

    // Briefly wait for camera buffer
    await new Promise(r => setTimeout(r, 150));

    try {
        const vW = video.videoWidth || 640;
        const vH = video.videoHeight || 480;
        
        const snapCanvas = document.createElement("canvas");
        snapCanvas.width = vW;
        snapCanvas.height = vH;
        const sCtx = snapCanvas.getContext("2d");
        sCtx.drawImage(video, 0, 0, vW, vH);

        progressFill.style.width = "50%";
        statusText.innerText = "Aligning corner anchors & perspective warp...";
        await new Promise(r => setTimeout(r, 100));

        const test = state.activeTest;
        const totalQ = test.totalQuestions;

        // Call the compiled OpenCV scanner!
        const result = await window.scanOMRSheet(snapCanvas, totalQ, 3, 1, []);
        
        // Draw correctness (green/red) and roll number (blue) overlays on the warped canvas
        try {
            window.drawOverlayOnWarpedCanvas(
                result.debugWarpedCanvas,
                totalQ,
                result.answers,
                test.answerKey,
                result.bestDy,
                [],
                result.questionOffsets,
                result.studentNum
            );
            state.scannedOmrUrl = result.debugWarpedCanvas.toDataURL("image/jpeg", 0.85);
        } catch (overlayErr) {
            console.error("Drawing overlays on warped canvas failed:", overlayErr);
            state.scannedOmrUrl = null;
        }

        progressFill.style.width = "85%";
        statusText.innerText = "Reading bubble density & grading paper...";
        await new Promise(r => setTimeout(r, 200));

        stopCamera();

        // Extract CV results
        const submission = {};
        for (let q = 1; q <= totalQ; q++) {
            submission[q] = result.answers[q] || null;
        }

        state.onlineAnswers = submission;
        progressFill.style.width = "100%";
        
        // Log attempts
        submitTest("OMR");
    } catch (err) {
        console.error("OpenCV OMR Scan failed:", err);
        statusText.innerText = "Alignment failed. Align the OMR sheet and try again.";
        scanBtn.style.display = "block";
        progressRow.style.display = "none";
        overlay.classList.remove("active");
        
        // Let the user know corner marks were not detected
        alert("OMR Sheet Scan Error: Alignment Failed. Please ensure the 4 black square corner markers are fully visible, clear of shadows, and centered in the viewport.");
    }
}

function runMockOmrScan() {
    const progressRow = document.getElementById("scan-progress-bar");
    const progressFill = document.getElementById("scan-progress-fill");
    const scanBtn = document.getElementById("capture-scan-btn");
    const statusText = document.getElementById("scanner-status-text");
    const overlay = document.getElementById("scanner-overlay");

    scanBtn.style.display = "none";
    progressRow.style.display = "block";
    overlay.classList.add("active");

    let progress = 0;
    const intervals = [
        { text: "Finding corner anchor points...", duration: 800 },
        { text: "Extracting perspective grid alignment...", duration: 1000 },
        { text: "Reading bubble optical densities...", duration: 1200 },
        { text: "Grading against answer key...", duration: 600 }
    ];

    let currentPhase = 0;
    
    function runPhase() {
        if (currentPhase >= intervals.length) {
            // Processing complete: simulate randomized answers submission
            stopCamera();
            const test = state.activeTest;
            const submission = {};
            const options = ["A", "B", "C", "D", null];
            
            for (let q = 1; q <= test.totalQuestions; q++) {
                const correctAns = test.answerKey[q];
                const roll = Math.floor(Math.random() * 100) + 1;
                
                if (roll <= 85) {
                    submission[q] = correctAns; // 85% correct
                } else if (roll <= 96) {
                    submission[q] = options.filter(o => o !== correctAns && o !== null)[Math.floor(Math.random() * 3)]; // Incorrect
                } else {
                    submission[q] = null; // Skipped
                }
            }
            
            // Generate a simulated mock OMR sheet canvas preview with annotations
            try {
                const mockCanvas = document.createElement("canvas");
                mockCanvas.width = 600;
                mockCanvas.height = 700;
                const mCtx = mockCanvas.getContext("2d");
                
                // Background
                mCtx.fillStyle = "#ffffff";
                mCtx.fillRect(0, 0, 600, 700);
                
                // Sheet Border Frame
                mCtx.strokeStyle = "#dc0045";
                mCtx.lineWidth = 3;
                mCtx.strokeRect(10, 10, 580, 680);
                
                // Alignment anchors (Black square marks at 4 corners)
                mCtx.fillStyle = "#000000";
                mCtx.fillRect(20, 20, 20, 20); // Top-Left
                mCtx.fillRect(560, 20, 20, 20); // Top-Right
                mCtx.fillRect(20, 660, 20, 20); // Bottom-Left
                mCtx.fillRect(560, 660, 20, 20); // Bottom-Right
                
                // Title and scanning rule banner
                mCtx.fillStyle = "#dc0045";
                mCtx.font = "bold 16px sans-serif";
                mCtx.fillText("NEET MOCK OMR ANSWER SHEET", 120, 36);
                mCtx.fillStyle = "#334155";
                mCtx.font = "10px sans-serif";
                mCtx.fillText("SIMULATED GRADING VERIFICATION PREVIEW • ORIGINAL RED DESIGN", 120, 52);
                
                // Draw 3-column Roll Number Bubble Grid
                mCtx.fillStyle = "#0f172a";
                mCtx.font = "bold 12px sans-serif";
                mCtx.fillText("ROLL NO: 123", 40, 95);
                const rxStart = 40, ryStart = 115, rxStep = 24, ryStep = 18;
                const mockRoll = [1, 2, 3];
                for (let col = 0; col < 3; col++) {
                    const activeDigit = mockRoll[col];
                    for (let row = 0; row < 10; row++) {
                        const cx = rxStart + col * rxStep;
                        const cy = ryStart + row * ryStep;
                        
                        mCtx.beginPath();
                        mCtx.arc(cx, cy, 6, 0, 2 * Math.PI);
                        mCtx.strokeStyle = "#dc0045";
                        mCtx.lineWidth = 1;
                        mCtx.stroke();
                        
                        mCtx.fillStyle = "#475569";
                        mCtx.font = "7px sans-serif";
                        mCtx.textAlign = "center";
                        mCtx.fillText(row.toString(), cx, cy + 2.5);
                        mCtx.textAlign = "left";
                        
                        if (row === activeDigit) {
                            // Scanned bubble indicator overlay: Translucent Blue
                            mCtx.beginPath();
                            mCtx.arc(cx, cy, 7.5, 0, 2 * Math.PI);
                            mCtx.fillStyle = "rgba(59, 130, 246, 0.45)";
                            mCtx.fill();
                            mCtx.strokeStyle = "#2563eb";
                            mCtx.lineWidth = 2.5;
                            mCtx.stroke();
                        }
                    }
                }
                
                // Draw Question Bubble Layout overlay (Physics columns)
                const qxStart = 200, qyStart = 100, qyStep = 20, qxStep = 26;
                mCtx.fillStyle = "#0f172a";
                mCtx.font = "bold 12px sans-serif";
                mCtx.fillText("SECTION A (PHYSICS)", qxStart, 80);
                
                // Render first 25 questions as sample bubbles
                for (let q = 1; q <= 25; q++) {
                    const qy = qyStart + (q - 1) * qyStep;
                    mCtx.fillStyle = "#0f172a";
                    mCtx.font = "10px sans-serif";
                    mCtx.fillText(q.toString().padStart(2, '0'), qxStart - 30, qy + 3.5);
                    
                    const correctAns = test.answerKey[q] || "A";
                    const studentAns = submission[q] || null;
                    const optionChars = ["A", "B", "C", "D"];
                    
                    for (let o = 0; o < 4; o++) {
                        const optChar = optionChars[o];
                        const qx = qxStart + o * qxStep;
                        
                        mCtx.beginPath();
                        mCtx.arc(qx, qy, 6.5, 0, 2 * Math.PI);
                        mCtx.strokeStyle = "#dc0045";
                        mCtx.lineWidth = 1;
                        mCtx.stroke();
                        
                        mCtx.fillStyle = "#475569";
                        mCtx.font = "7px sans-serif";
                        mCtx.textAlign = "center";
                        mCtx.fillText(optChar, qx, qy + 2.5);
                        mCtx.textAlign = "left";
                        
                        const isStudentPick = studentAns === optChar;
                        const isCorrectOption = correctAns === optChar;
                        
                        if (isStudentPick) {
                            if (studentAns === correctAns) {
                                // Correct selection: Translucent Green overlay
                                mCtx.beginPath();
                                mCtx.arc(qx, qy, 8, 0, 2 * Math.PI);
                                mCtx.fillStyle = "rgba(34, 197, 94, 0.45)";
                                mCtx.fill();
                                mCtx.strokeStyle = "#16a34a";
                                mCtx.lineWidth = 2.5;
                                mCtx.stroke();
                            } else {
                                // Incorrect selection: Translucent Red overlay
                                mCtx.beginPath();
                                mCtx.arc(qx, qy, 8, 0, 2 * Math.PI);
                                mCtx.fillStyle = "rgba(239, 68, 68, 0.45)";
                                mCtx.fill();
                                mCtx.strokeStyle = "#dc2626";
                                mCtx.lineWidth = 2.5;
                                mCtx.stroke();
                            }
                        } else if (isCorrectOption && studentAns !== null) {
                            // Correct answer guide outline: Green ring
                            mCtx.beginPath();
                            mCtx.arc(qx, qy, 8, 0, 2 * Math.PI);
                            mCtx.strokeStyle = "#16a34a";
                            mCtx.lineWidth = 2;
                            mCtx.stroke();
                        }
                    }
                }
                
                state.scannedOmrUrl = mockCanvas.toDataURL("image/jpeg", 0.85);
            } catch (mockErr) {
                console.error("Generating mock OMR canvas image failed:", mockErr);
                state.scannedOmrUrl = null;
            }

            // Score submission
            state.onlineAnswers = submission;
            submitTest("OMR");
            return;
        }

        statusText.innerText = intervals[currentPhase].text;
        
        let targetProgress = ((currentPhase + 1) / intervals.length) * 100;
        let step = (targetProgress - progress) / 20;

        let stepCount = 0;
        let progressInterval = setInterval(() => {
            progress += step;
            progressFill.style.width = `${progress}%`;
            stepCount++;
            if (stepCount >= 20) {
                clearInterval(progressInterval);
                currentPhase++;
                runPhase();
            }
        }, intervals[currentPhase].duration / 20);
    }

    runPhase();
}

// -------------------------------------------------------------
// Profile History Rendering
// -------------------------------------------------------------
function updateProfileStats() {
    const totalCount = state.history.length;
    let avg = 0;
    if (totalCount > 0) {
        const sum = state.history.reduce((acc, h) => acc + ((h.marksObtained / h.totalMarks) * 100), 0);
        avg = (sum / totalCount).toFixed(1);
    }
    
    // Updates dashboard statistics DOM elements
    const elements = {
        "stat-attempts-count": totalCount,
        "stat-avg-score": `${avg}%`,
        "profile-total-attempts": totalCount,
        "profile-avg-score": `${avg}%`
    };

    for (let id in elements) {
        const el = document.getElementById(id);
        if (el) el.innerText = elements[id];
    }

    // Render list
    const container = document.getElementById("profile-history-list");
    if (!container) return;
    container.innerHTML = "";

    if (state.history.length === 0) {
        container.innerHTML = `<p class="empty-text">No test attempts logged yet.</p>`;
        return;
    }

    state.history.forEach(record => {
        const card = document.createElement("div");
        const modeClass = record.attemptType.toLowerCase();
        card.className = "history-item-card";
        card.innerHTML = `
            <div class="history-info">
                <h4>${record.testTitle}</h4>
                <div class="history-metadata">
                    <span class="history-date">${record.dateString}</span>
                    <span class="mode-tag ${modeClass}">${record.attemptType}</span>
                </div>
            </div>
            <div class="history-score-box">
                <span class="history-score">${record.marksObtained}/${record.totalMarks}</span>
                <span>View scorecard</span>
            </div>
        `;
        card.addEventListener("click", () => {
            showScorecard(record);
        });
        container.appendChild(card);
    });

    // Draw SVG charts
    drawAnalyticsCharts();
}

function drawAnalyticsCharts() {
    const trendSvg = document.getElementById("trend-line-chart");
    const barSvg = document.getElementById("subject-bar-chart");
    if (!trendSvg || !barSvg) return;

    trendSvg.innerHTML = "";
    barSvg.innerHTML = "";

    const history = state.history || [];
    
    // --- 1. Draw Score Trend Line Chart ---
    const trendData = [...history].reverse().slice(-5);
    const points = trendData.length > 0 ? trendData.map(h => (h.marksObtained / h.totalMarks) * 100) : [60, 70, 65, 80, 75];
    const labels = trendData.length > 0 ? trendData.map((_, i) => `T-${i+1}`) : ["T-1", "T-2", "T-3", "T-4", "T-5"];

    const padding = 30;
    const chartW = 500;
    const chartH = 200;
    const usableW = chartW - 2 * padding;
    const usableH = chartH - 2 * padding;

    // Draw Y-axis grid lines
    const yTicks = [0, 25, 50, 75, 100];
    yTicks.forEach(tick => {
        const y = padding + usableH - (tick / 100) * usableH;
        const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line.setAttribute("x1", padding);
        line.setAttribute("y1", y);
        line.setAttribute("x2", chartW - padding);
        line.setAttribute("y2", y);
        line.setAttribute("class", "chart-grid-line");
        trendSvg.appendChild(line);

        const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttribute("x", padding - 8);
        text.setAttribute("y", y + 4);
        text.setAttribute("text-anchor", "end");
        text.setAttribute("class", "chart-label");
        text.textContent = `${tick}%`;
        trendSvg.appendChild(text);
    });

    // Calculate Coordinates
    const xStep = points.length > 1 ? usableW / (points.length - 1) : usableW;
    const coords = points.map((p, i) => {
        const x = padding + i * xStep;
        const y = padding + usableH - (p / 100) * usableH;
        return { x, y, val: p, label: labels[i] };
    });

    // Draw Path
    if (coords.length > 0) {
        let pathD = `M ${coords[0].x} ${coords[0].y}`;
        for (let i = 1; i < coords.length; i++) {
            pathD += ` L ${coords[i].x} ${coords[i].y}`;
        }

        const shadowPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
        shadowPath.setAttribute("d", pathD);
        shadowPath.setAttribute("class", "chart-line-shadow");
        trendSvg.appendChild(shadowPath);

        const linePath = document.createElementNS("http://www.w3.org/2000/svg", "path");
        linePath.setAttribute("d", pathD);
        linePath.setAttribute("class", "chart-line");
        trendSvg.appendChild(linePath);
    }

    // Draw Points & X Labels
    coords.forEach(pt => {
        const circ = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circ.setAttribute("cx", pt.x);
        circ.setAttribute("cy", pt.y);
        circ.setAttribute("r", 5);
        circ.setAttribute("class", "chart-point");
        
        const title = document.createElementNS("http://www.w3.org/2000/svg", "title");
        title.textContent = `${pt.val.toFixed(1)}%`;
        circ.appendChild(title);
        trendSvg.appendChild(circ);

        const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttribute("x", pt.x);
        text.setAttribute("y", chartH - padding + 16);
        text.setAttribute("text-anchor", "middle");
        text.setAttribute("class", "chart-label");
        text.textContent = pt.label;
        trendSvg.appendChild(text);
    });

    // --- 2. Draw Subject Accuracy Bar Chart ---
    const subjects = [
        { name: "Pedagogy", accuracy: 84 },
        { name: "Hindi", accuracy: 78 },
        { name: "English", accuracy: 68 },
        { name: "Maths", accuracy: 72 },
        { name: "Science", accuracy: 65 }
    ];

    const barPaddingLeft = 90;
    const barPaddingRight = 40;
    const barPaddingTop = 15;
    const barPaddingBottom = 15;
    const barUsableW = chartW - barPaddingLeft - barPaddingRight;
    const barUsableH = chartH - barPaddingTop - barPaddingBottom;
    const barStepY = barUsableH / subjects.length;

    subjects.forEach((subj, idx) => {
        const y = barPaddingTop + idx * barStepY + (barStepY / 2);
        
        const labelText = document.createElementNS("http://www.w3.org/2000/svg", "text");
        labelText.setAttribute("x", barPaddingLeft - 10);
        labelText.setAttribute("y", y + 4);
        labelText.setAttribute("text-anchor", "end");
        labelText.setAttribute("class", "chart-label");
        labelText.setAttribute("style", "font-weight: 600;");
        labelText.textContent = subj.name;
        barSvg.appendChild(labelText);

        const barBg = document.createElementNS("http://www.w3.org/2000/svg", "rect");
        barBg.setAttribute("x", barPaddingLeft);
        barBg.setAttribute("y", y - 6);
        barBg.setAttribute("width", barUsableW);
        barBg.setAttribute("height", 12);
        barBg.setAttribute("class", "chart-bar-bg");
        barSvg.appendChild(barBg);

        const bar = document.createElementNS("http://www.w3.org/2000/svg", "rect");
        bar.setAttribute("x", barPaddingLeft);
        bar.setAttribute("y", y - 6);
        bar.setAttribute("width", (subj.accuracy / 100) * barUsableW);
        bar.setAttribute("height", 12);
        bar.setAttribute("class", "chart-bar");
        bar.setAttribute("style", "fill: var(--primary);");
        barSvg.appendChild(bar);

        const valText = document.createElementNS("http://www.w3.org/2000/svg", "text");
        valText.setAttribute("x", barPaddingLeft + (subj.accuracy / 100) * barUsableW + 8);
        valText.setAttribute("y", y + 4);
        valText.setAttribute("class", "chart-label");
        valText.setAttribute("style", "font-weight: 700; fill: var(--primary);");
        valText.textContent = `${subj.accuracy}%`;
        barSvg.appendChild(valText);
    });
}

// -------------------------------------------------------------
// DOM Event Listeners & Bootstrapping
// -------------------------------------------------------------
document.addEventListener("DOMContentLoaded", () => {
    initAppState();

    document.getElementById("setup-declaration-checkbox").addEventListener("change", (e) => {
        document.getElementById("player-setup-start-btn").disabled = !e.target.checked;
    });

    // 1. Render initial content
    renderHomeExams();
    renderHomeUpdates();
    renderHomeSubjects();
    renderExamsGrid();
    renderPDFNotes();
    renderTestSeriesCatalog();
    updateProfileStats();

    // 2. Setup Sticky Footer click actions
    document.querySelectorAll(".footer-nav-item").forEach(btn => {
        btn.addEventListener("click", (e) => {
            const screen = btn.getAttribute("data-screen");
            navigateTo(screen);
        });
    });

    // 3. Setup Sidebar drawer click actions
    document.querySelectorAll(".nav-item").forEach(btn => {
        btn.addEventListener("click", (e) => {
            e.preventDefault();
            const screen = btn.getAttribute("data-screen");
            navigateTo(screen);
            // Close sidebar on mobile drawer selection
            document.getElementById("sidebar").classList.remove("active");
        });
    });

    // Hamburger drawer toggle
    document.getElementById("hamburger-btn").addEventListener("click", () => {
        document.getElementById("sidebar").classList.add("active");
    });
    
    document.getElementById("close-sidebar-btn").addEventListener("click", () => {
        document.getElementById("sidebar").classList.remove("active");
    });

    // Profile icon click
    document.getElementById("header-profile-btn").addEventListener("click", () => {
        navigateTo("profile");
    });

    // Home "View All" redirects
    document.querySelectorAll(".view-all-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            navigateTo(btn.getAttribute("data-target"));
        });
    });

    // Promo Card redirect
    document.getElementById("test-series-promo").addEventListener("click", () => {
        navigateTo("test-series-catalog");
    });

    // Search bar listener for Exams List
    document.getElementById("exam-search-input").addEventListener("input", (e) => {
        state.examSearchQuery = e.target.value;
        renderExamsGrid();
    });

    // Generic Back navigation buttons
    document.querySelectorAll(".back-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            navigateTo(btn.getAttribute("data-target"));
        });
    });

    // PDF Notes Modal navigation
    document.getElementById("close-pdf-reader-btn").addEventListener("click", () => {
        document.getElementById("pdf-reader-modal").classList.remove("active");
        state.activePdfNote = null;
    });

    document.getElementById("pdf-prev-btn").addEventListener("click", () => {
        if (state.pdfCurrentPage > 1) {
            state.pdfCurrentPage--;
            updatePdfPageContent();
        }
    });

    document.getElementById("pdf-next-btn").addEventListener("click", () => {
        if (state.pdfCurrentPage < state.pdfTotalPages) {
            state.pdfCurrentPage++;
            updatePdfPageContent();
        }
    });

    // Mock Downloads inside OMR Prep
    document.getElementById("btn-download-paper").addEventListener("click", function() {
        this.innerHTML = `<i class="fa-solid fa-circle-check text-success"></i>`;
    });

    document.getElementById("btn-download-omr").addEventListener("click", function() {
        this.innerHTML = `<i class="fa-solid fa-circle-check text-success"></i>`;
    });

    document.getElementById("btn-preview-paper").addEventListener("click", () => {
        const panel = document.getElementById("omr-pdf-viewer-container");
        panel.style.display = panel.style.display === "none" ? "block" : "none";
    });

    document.getElementById("close-pdf-viewer-btn").addEventListener("click", () => {
        document.getElementById("omr-pdf-viewer-container").style.display = "none";
    });

    document.getElementById("omr-prep-back-btn").addEventListener("click", () => {
        navigateTo("test-series-details");
    });

    // OMR Scanner navigation triggers
    document.getElementById("start-scan-btn").addEventListener("click", () => {
        // Reset scanner button and progress bar
        document.getElementById("capture-scan-btn").style.display = "block";
        document.getElementById("scan-progress-bar").style.display = "none";
        document.getElementById("scanner-overlay").classList.remove("active");
        
        navigateTo("omr-scanner");
        startCamera();
    });

    document.getElementById("scanner-back-btn").addEventListener("click", () => {
        navigateTo("omr-scan-prep");
    });

    document.getElementById("capture-scan-btn").addEventListener("click", () => {
        simulateOmrScan();
    });

    // OMR Upload scanner binding
    document.getElementById("upload-scan-btn").addEventListener("click", () => {
        document.getElementById("omr-file-input").click();
    });

    document.getElementById("omr-file-input").addEventListener("change", (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function(event) {
            const img = new Image();
            img.onload = async function() {
                const progressRow = document.getElementById("scan-progress-bar");
                const progressFill = document.getElementById("scan-progress-fill");
                const statusText = document.getElementById("scanner-status-text");
                const overlay = document.getElementById("scanner-overlay");
                const scanBtn = document.getElementById("capture-scan-btn");

                // Navigate to scanner screen so they see alignment progress
                navigateTo("omr-scanner");
                
                scanBtn.style.display = "none";
                progressRow.style.display = "block";
                overlay.classList.add("active");
                progressFill.style.width = "25%";
                statusText.innerText = "Processing uploaded image...";

                try {
                    // Draw image onto a canvas for OpenCV scanning
                    const canvas = document.createElement("canvas");
                    canvas.width = img.width;
                    canvas.height = img.height;
                    const ctx = canvas.getContext("2d");
                    ctx.drawImage(img, 0, 0);

                    progressFill.style.width = "50%";
                    statusText.innerText = "Aligning corner anchors & perspective warp...";
                    await new Promise(r => setTimeout(r, 200));

                    const test = state.activeTest;
                    const totalQ = test.totalQuestions;

                    // Execute OMR OpenCV detection!
                    const result = await window.scanOMRSheet(canvas, totalQ, 3, 1, []);

                    // Draw correctness (green/red) and roll number (blue) overlays on the warped canvas
                    try {
                        window.drawOverlayOnWarpedCanvas(
                            result.debugWarpedCanvas,
                            totalQ,
                            result.answers,
                            test.answerKey,
                            result.bestDy,
                            [],
                            result.questionOffsets,
                            result.studentNum
                        );
                        state.scannedOmrUrl = result.debugWarpedCanvas.toDataURL("image/jpeg", 0.85);
                    } catch (overlayErr) {
                        console.error("Drawing overlays on warped canvas failed:", overlayErr);
                        state.scannedOmrUrl = null;
                    }

                    progressFill.style.width = "85%";
                    statusText.innerText = "Grading bubble marks...";
                    await new Promise(r => setTimeout(r, 200));

                    const submission = {};
                    for (let q = 1; q <= totalQ; q++) {
                        submission[q] = result.answers[q] || null;
                    }

                    state.onlineAnswers = submission;
                    progressFill.style.width = "100%";
                    submitTest("OMR");
                } catch (err) {
                    console.error("OpenCV Upload OMR Scan failed:", err);
                    alert("OMR Sheet Scan Error: Alignment Failed. Please ensure the 4 black square corner markers are fully visible, clear of shadows, and centered in the image frame.");
                    navigateTo("omr-scan-prep");
                }
            };
            img.src = event.target.result;
        };
        reader.readAsDataURL(file);
    });

    // Scorecard completion trigger
    document.getElementById("result-home-btn").addEventListener("click", () => {
        navigateTo("home");
    });

    document.getElementById("result-review-btn").addEventListener("click", () => {
        renderTestReview();
        navigateTo("test-review");
    });

    document.getElementById("result-leaderboard-btn").addEventListener("click", () => {
        if (state.activeTest) {
            showTestDetails(state.activeTest);
        } else {
            navigateTo("test-series-catalog");
        }
    });

    document.getElementById("details-back-btn").addEventListener("click", () => {
        if (state.activeExam) {
            showExamDetails(state.activeExam);
        } else {
            navigateTo("home");
        }
    });

    document.getElementById("exam-details-back-btn").addEventListener("click", () => {
        navigateTo("home");
    });

    // Sub tab switching
    document.querySelectorAll(".exam-sub-tab").forEach(tab => {
        tab.addEventListener("click", () => {
            const activeTab = tab.getAttribute("data-tab");
            
            // Toggle active tabs buttons style
            document.querySelectorAll(".exam-sub-tab").forEach(btn => {
                const isSelected = btn.getAttribute("data-tab") === activeTab;
                btn.className = `exam-sub-tab ${isSelected ? 'active' : ''}`;
                btn.style.backgroundColor = isSelected ? "var(--surface)" : "transparent";
                btn.style.color = isSelected ? "var(--primary)" : "var(--on-surface-variant)";
            });

            // Toggle active lists
            document.getElementById("exam-tab-mocks").style.display = activeTab === "mocks" ? "block" : "none";
            document.getElementById("exam-tab-notes").style.display = activeTab === "notes" ? "block" : "none";
            document.getElementById("exam-tab-pyqs").style.display = activeTab === "pyqs" ? "block" : "none";
        });
    });

    document.getElementById("review-back-btn").addEventListener("click", () => {
        navigateTo("omr-result");
    });

    document.getElementById("btn-review-preview-paper").addEventListener("click", () => {
        const container = document.getElementById("review-pdf-viewer-container");
        container.style.display = container.style.display === "none" ? "block" : "none";
    });

    document.getElementById("close-review-pdf-btn").addEventListener("click", () => {
        document.getElementById("review-pdf-viewer-container").style.display = "none";
    });

    document.getElementById("player-exit-btn").addEventListener("click", () => {
        const exit = confirm("Exit Mock Test? Your progress on this attempt will be lost.");
        if (exit) {
            if (state.activeTest) {
                showTestDetails(state.activeTest);
            } else {
                navigateTo("home");
            }
        }
    });

    // Online Player Navigation controls
    document.getElementById("player-prev-btn").addEventListener("click", () => {
        if (state.currentQuestionIndex > 0) {
            state.currentQuestionIndex--;
            renderQuestion();
        }
    });

    document.getElementById("player-next-btn").addEventListener("click", () => {
        if (state.currentQuestionIndex < state.activeTest.totalQuestions - 1) {
            state.currentQuestionIndex++;
            renderQuestion();
        }
    });

    // Submit dialog triggers
    document.getElementById("player-submit-btn").addEventListener("click", () => {
        const answered = Object.keys(state.onlineAnswers).length;
        const total = state.activeTest.totalQuestions;
        const confirmSubmit = confirm(`You have answered ${answered} of ${total} questions. Do you want to submit the exam?`);
        if (confirmSubmit) {
            submitTest("ONLINE");
        }
    });

    // Palette modal triggers
    document.getElementById("player-palette-btn").addEventListener("click", () => {
        renderPaletteGrid();
        document.getElementById("palette-modal").classList.add("active");
    });

    document.getElementById("close-palette-btn").addEventListener("click", () => {
        document.getElementById("palette-modal").classList.remove("active");
    });

    function renderPaletteGrid() {
        const container = document.getElementById("palette-grid-container");
        if (!container || !state.activeTest) return;
        container.innerHTML = "";

        for (let q = 1; q <= state.activeTest.totalQuestions; q++) {
            const circle = document.createElement("div");
            const isAnswered = state.onlineAnswers[q] !== undefined;
            const isReview = state.onlineReview[q] === true;

            let bgColor = "var(--outline)";
            let textColor = "var(--on-surface-variant)";
            
            if (isReview && isAnswered) {
                bgColor = "#9C27B0"; // Purple
                textColor = "#fff";
            } else if (isReview) {
                bgColor = "#FF9800"; // Orange
                textColor = "#fff";
            } else if (isAnswered) {
                bgColor = "var(--success)"; // Green
                textColor = "#fff";
            }

            circle.className = `palette-circle`;
            circle.style.backgroundColor = bgColor;
            circle.style.color = textColor;
            circle.style.fontWeight = "700";
            circle.innerText = q;
            
            circle.addEventListener("click", () => {
                state.currentQuestionIndex = q - 1;
                renderQuestion();
                document.getElementById("palette-modal").classList.remove("active");
            });

            container.appendChild(circle);
        }
    }

    // Bookmark/Review checkbox change trigger
    document.getElementById("mark-review-checkbox").addEventListener("change", function() {
        const qNum = state.currentQuestionIndex + 1;
        state.onlineReview[qNum] = this.checked;
    });

    // -------------------------------------------------------------
    // Multi-Theme Controller
    // -------------------------------------------------------------
    const activeTheme = localStorage.getItem("app_theme") || "ocean";
    document.body.setAttribute("data-theme", activeTheme);
    document.querySelectorAll(".theme-btn").forEach(btn => {
        btn.classList.remove("active");
        if (btn.getAttribute("data-theme") === activeTheme) {
            btn.classList.add("active");
        }
    });

    document.querySelectorAll(".theme-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const theme = btn.getAttribute("data-theme");
            document.body.setAttribute("data-theme", theme);
            localStorage.setItem("app_theme", theme);
            document.querySelectorAll(".theme-btn").forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
        });
    });

    // -------------------------------------------------------------
    // Admin Security Gate & Login
    // -------------------------------------------------------------
    document.getElementById("admin-login-btn").addEventListener("click", () => {
        verifyAdminPasscode();
    });

    document.getElementById("admin-passcode-input").addEventListener("keydown", (e) => {
        if (e.key === "Enter") verifyAdminPasscode();
    });

    function verifyAdminPasscode() {
        const code = document.getElementById("admin-passcode-input").value;
        if (code === "12345") {
            document.getElementById("admin-passcode-input").value = "";
            navigateTo("admin");
            renderAdminConsole();
        } else {
            alert("Incorrect admin passcode. Try '12345'.");
        }
    }

    document.getElementById("admin-logout-btn").addEventListener("click", () => {
        navigateTo("home");
    });

    // -------------------------------------------------------------
    // Admin Panel Tab Switchers
    // -------------------------------------------------------------
    document.querySelectorAll(".admin-tab-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const tabName = btn.getAttribute("data-tab");
            document.querySelectorAll(".admin-tab-btn").forEach(b => b.classList.remove("active"));
            document.querySelectorAll(".admin-tab-body").forEach(b => b.classList.remove("active"));
            
            btn.classList.add("active");
            document.getElementById(`tab-${tabName}`).classList.add("active");
        });
    });

    // -------------------------------------------------------------
    // Admin Console Renderers
    // -------------------------------------------------------------
    function renderAdminConsole() {
        renderAdminExams();
        renderAdminNotes();
        renderAdminSeriesDropdowns();
        renderAdminNews();
        renderAdminLogs();
    }

    // A. Exams CRUD
    function renderAdminExams() {
        const tbody = document.getElementById("admin-exams-table-body");
        tbody.innerHTML = "";
        DB.exams.forEach((exam, idx) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><i class="fa-solid ${exam.iconName}"></i></td>
                <td><strong>${exam.shortName}</strong></td>
                <td>${exam.title}</td>
                <td><button class="admin-delete-btn" data-index="${idx}"><i class="fa-solid fa-trash-can"></i></button></td>
            `;
            tr.querySelector(".admin-delete-btn").addEventListener("click", () => {
                if (confirm(`Delete exam ${exam.shortName}?`)) {
                    DB.exams.splice(idx, 1);
                    localStorage.setItem("adm_exams", JSON.stringify(DB.exams));
                    renderAdminExams();
                    renderHomeExams();
                    renderExamsGrid();
                    renderAdminSeriesDropdowns();
                }
            });
            tbody.appendChild(tr);
        });
    }

    document.getElementById("admin-add-exam-form").addEventListener("submit", (e) => {
        e.preventDefault();
        const short = document.getElementById("adm-exam-short").value.trim().toUpperCase();
        const title = document.getElementById("adm-exam-title").value.trim();
        const icon = document.getElementById("adm-exam-icon").value;

        if (DB.exams.some(ex => ex.shortName === short)) {
            alert("An exam with this code already exists.");
            return;
        }

        DB.exams.push({
            id: short.toLowerCase().replace("/", "_"),
            title: title,
            shortName: short,
            iconName: icon
        });

        localStorage.setItem("adm_exams", JSON.stringify(DB.exams));
        document.getElementById("admin-add-exam-form").reset();
        
        renderAdminExams();
        renderHomeExams();
        renderExamsGrid();
        renderAdminSeriesDropdowns();
        alert("Exam listing created successfully!");
    });

    // B. Notes CRUD
    function renderAdminNotes() {
        const tbody = document.getElementById("admin-notes-table-body");
        tbody.innerHTML = "";
        DB.pdfNotes.forEach((note, idx) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><span class="note-tag">${note.subject}</span></td>
                <td><strong>${note.title}</strong></td>
                <td>${note.sizeMb} MB</td>
                <td><button class="admin-delete-btn" data-index="${idx}"><i class="fa-solid fa-trash-can"></i></button></td>
            `;
            tr.querySelector(".admin-delete-btn").addEventListener("click", () => {
                if (confirm(`Delete note ${note.title}?`)) {
                    DB.pdfNotes.splice(idx, 1);
                    localStorage.setItem("adm_pdf_notes", JSON.stringify(DB.pdfNotes));
                    renderAdminNotes();
                    renderPDFNotes();
                }
            });
            tbody.appendChild(tr);
        });
    }

    document.getElementById("admin-add-note-form").addEventListener("submit", (e) => {
        e.preventDefault();
        const title = document.getElementById("adm-note-title").value.trim();
        const subject = document.getElementById("adm-note-subject").value;
        const size = parseFloat(document.getElementById("adm-note-size").value);
        const url = document.getElementById("adm-note-url").value.trim();

        DB.pdfNotes.push({
            id: "note_" + Math.random().toString(36).substring(2, 8),
            title: title,
            subject: subject,
            sizeMb: size,
            pdfUrl: url
        });

        localStorage.setItem("adm_pdf_notes", JSON.stringify(DB.pdfNotes));
        document.getElementById("admin-add-note-form").reset();
        document.getElementById("adm-note-url").value = "https://example.com/mock.pdf";
        
        renderAdminNotes();
        renderPDFNotes();
        alert("Study note published!");
    });

    // C. Test Series & Keys Manager
    function renderAdminSeriesDropdowns() {
        const examSelect = document.getElementById("adm-series-exam");
        const seriesSelect = document.getElementById("adm-test-series-id");
        
        examSelect.innerHTML = "";
        DB.exams.forEach(ex => {
            examSelect.innerHTML += `<option value="${ex.id}">${ex.shortName}</option>`;
        });

        seriesSelect.innerHTML = "";
        DB.testSeries.forEach(ts => {
            const exName = DB.exams.find(e => e.id === ts.examId)?.shortName || "Exam";
            seriesSelect.innerHTML += `<option value="${ts.id}">[${exName}] ${ts.title}</option>`;
        });
    }

    document.getElementById("admin-add-series-form").addEventListener("submit", (e) => {
        e.preventDefault();
        const examId = document.getElementById("adm-series-exam").value;
        const title = document.getElementById("adm-series-title").value.trim();
        const desc = document.getElementById("adm-series-desc").value.trim();

        DB.testSeries.push({
            id: "ts_" + Math.random().toString(36).substring(2, 8),
            examId: examId,
            title: title,
            description: desc,
            numberOfTests: 0,
            tests: []
        });

        localStorage.setItem("adm_test_series", JSON.stringify(DB.testSeries));
        document.getElementById("admin-add-series-form").reset();
        
        renderAdminSeriesDropdowns();
        renderTestSeriesCatalog();
        alert("Test Series Group created!");
    });

    document.getElementById("admin-add-test-form").addEventListener("submit", (e) => {
        e.preventDefault();
        const seriesId = document.getElementById("adm-test-series-id").value;
        const title = document.getElementById("adm-test-title").value.trim();
        const duration = parseInt(document.getElementById("adm-test-duration").value);
        const qty = parseInt(document.getElementById("adm-test-qty").value);
        const keysText = document.getElementById("adm-test-key").value.trim().toUpperCase();

        const keysArray = keysText.split(",").map(k => k.trim());
        if (keysArray.length !== qty) {
            alert(`Error: The length of your answer key list (${keysArray.length}) must match the total questions count (${qty}).`);
            return;
        }

        const answerKey = {};
        keysArray.forEach((val, idx) => {
            answerKey[idx + 1] = val;
        });

        const series = DB.testSeries.find(ts => ts.id === seriesId);
        if (series) {
            series.tests.push({
                id: "test_" + Math.random().toString(36).substring(2, 8),
                title: title,
                durationMinutes: duration,
                totalQuestions: qty,
                answerKey: answerKey
            });
            series.numberOfTests = series.tests.length;

            localStorage.setItem("adm_test_series", JSON.stringify(DB.testSeries));
            document.getElementById("admin-add-test-form").reset();
            
            renderTestSeriesCatalog();
            alert("Mock Test & Answer Key added successfully!");
        }
    });

    // D. News CRUD
    function renderAdminNews() {
        const tbody = document.getElementById("admin-news-table-body");
        tbody.innerHTML = "";
        DB.updates.forEach((news, idx) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><span style="font-size: 11px;">${news.dateString}</span></td>
                <td><strong>${news.title}</strong></td>
                <td><button class="admin-delete-btn" data-index="${idx}"><i class="fa-solid fa-trash-can"></i></button></td>
            `;
            tr.querySelector(".admin-delete-btn").addEventListener("click", () => {
                if (confirm(`Delete update ${news.title}?`)) {
                    DB.updates.splice(idx, 1);
                    localStorage.setItem("adm_updates", JSON.stringify(DB.updates));
                    renderAdminNews();
                    renderHomeUpdates();
                }
            });
            tbody.appendChild(tr);
        });
    }

    document.getElementById("admin-add-update-form").addEventListener("submit", (e) => {
        e.preventDefault();
        const title = document.getElementById("adm-upd-title").value.trim();
        const content = document.getElementById("adm-upd-content").value.trim();
        
        const now = new Date();
        const dateStr = `${now.getFullYear()}-${(now.getMonth()+1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}`;

        DB.updates.unshift({
            id: "upd_" + Math.random().toString(36).substring(2, 8),
            title: title,
            content: content,
            dateString: dateStr
        });

        localStorage.setItem("adm_updates", JSON.stringify(DB.updates));
        document.getElementById("admin-add-update-form").reset();
        
        renderAdminNews();
        renderHomeUpdates();
        alert("News Update posted!");
    });

    // E. Submissions Log
    function renderAdminLogs() {
        const tbody = document.getElementById("admin-logs-table-body");
        tbody.innerHTML = "";
        state.history.forEach(log => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><span style="font-size: 10px; color: var(--secondary);">${log.dateString}</span></td>
                <td><span class="mode-tag ${log.attemptType.toLowerCase()}">${log.attemptType}</span></td>
                <td><strong>${log.testTitle}</strong></td>
                <td class="text-success"><strong>${log.marksObtained}/${log.totalMarks}</strong></td>
            `;
            tbody.appendChild(tr);
        });
    }

    document.getElementById("admin-clear-logs-btn").addEventListener("click", () => {
        if (confirm("Are you sure you want to clear all student attempt histories? This cannot be undone.")) {
            state.history = [];
            localStorage.setItem("omr_test_history", JSON.stringify([]));
            renderAdminLogs();
            updateProfileStats();
            alert("All attempt logs cleared.");
        }
    });
});

