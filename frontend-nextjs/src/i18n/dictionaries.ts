export type Locale = "en" | "de" | "vi";

export const LOCALE_STORAGE_KEY = "apptd_locale";

const en = {
  nav: {
    brand: "Learn German",
    lessons: "Lessons",
    progress: "Progress",
    aiSpeech: "AI Speaking",
    login: "Log in",
    register: "Register",
    logout: "Log out"
  },
  lang: {
    label: "Language",
    en: "English",
    de: "Deutsch",
    vi: "Tiếng Việt"
  },
  home: {
    title: "Welcome!",
    intro:
      "This is a sample frontend for the API at /api/v1: lessons, progress submission, and AI speech analysis.",
    noteTitle: "Note",
    noteBody:
      "Sign in to save lesson progress (XP is added when you complete exercises) and to use AI speech analysis. Your session stays in this browser tab until you log out or close the tab/browser."
  },
  lessons: {
    title: "Lessons",
    level: "Level:",
    loading: "Loading…",
    error: "Could not load lessons.",
    viewContent: "View content"
  },
  lessonContent: {
    loading: "Loading lesson content…",
    error: "Could not load lesson content.",
    vocab: "Vocabulary",
    plural: "Plural:",
    meaning: "Meaning:",
    exercises: "Exercises",
    noExercises: "No exercises for this lesson yet.",
    speaking: "Speaking practice topics",
    noTopics: "No topics for this level yet.",
    requirement: "Requirement:",
    check: "Check",
    correct: "Correct",
    incorrect: "Not correct",
    answerMcqPlaceholder: "Select an option…",
    answerShortPlaceholder: "Type your answer…",
    answerRequired: "Answer is required.",
    score: "Score",
    submitLesson: "Submit lesson",
    submittingLesson: "Submitting…",
    submitNeedAllChecked: "Check all exercises before submitting.",
    submitNeedAllAnswers: "Provide answers for all exercises before submitting.",
    vocabHoverHint: "Hover or focus the word for IPA and usage tips.",
    vocabPronunciation: "Pronunciation (IPA):",
    vocabUsage: "Usage:",
    usageHowTo: "How to use (notes)",
    sampleSentences: "Sample sentences (using lesson vocabulary)",
    examplesSectionIntro: "Before exercises — study these patterns and sentences that use this lesson’s words.",
    examplesSectionEmpty:
      "No examples returned from the server. Restart the backend (non-prod) or update the app so lesson JSON syncs.",
    listenGerman: "Listen (German pronunciation)",
    listenUnsupported:
      "No audio file and this browser does not support read-aloud. Try Chrome or Edge, or use a lesson with audio URLs."
  },
  progress: {
    titlePage: "Your progress",
    howXpWorks:
      "XP is added automatically when you complete a lesson’s exercises and submit from the lesson page. You don’t enter scores manually here — progress reflects lessons you have studied.",
    studiedLessons: "Lessons you have studied",
    noLessonsYet: "No lessons yet. Open a lesson and finish the exercises to earn XP.",
    colOrder: "#",
    colLesson: "Lesson",
    colLevel: "Level",
    colBest: "Best score",
    colXpLesson: "XP gained",
    colStatus: "Status",
    status_COMPLETED: "Completed",
    status_NOT_COMPLETED: "In progress",
    loginToView: "Sign in to see your XP and lesson history.",
    loadingMyProgress: "Loading your progress…",
    errorMyProgress: "Could not load your progress.",
    submitting: "Sending…",
    submit: "Submit",
    errorGeneric: "Error",
    xpAdded: "XP awarded:",
    bonus: "bonus",
    totalXp: "Total XP:",
    leaderboard: "Leaderboard",
    leaderboardLoading: "Loading…",
    leaderboardError: "Could not load leaderboard."
  },
  ai: {
    title: "Pronunciation analysis (AI speaking)",
    audioOptional: "Audio (optional)",
    levelOptional: "Level requirement (optional)",
    textOptional: "Text (optional)",
    textPlaceholder:
      "Paste transcript/text if you have no audio or want scoring from text.",
    topicIdOptional: "SpeakingTopicId (optional, preferred over level)",
    topicIdPlaceholder: "e.g. 1",
    analyzing: "Analyzing…",
    analyze: "Analyze",
    score: "Score",
    feedback: "Feedback",
    corrections: "Corrections"
  },
  errors: {
    noJwt: "You are not signed in. Open Log in and use your account.",
    needAudioOrText: "Send at least an audio file or text."
  },
  auth: {
    loginTitle: "Log in",
    registerTitle: "Register",
    seedHint:
      "Demo accounts (seeded by BE when not prod): learner student / Student123 · admin / Admin12345",
    username: "Username",
    password: "Password",
    loginFailed: "Login failed",
    registerFailed: "Registration failed",
    registerRules: "Username ≥ 3 characters, password ≥ 8 characters (per backend rules).",
    noAccount: "No account yet?",
    hasAccount: "Already have an account?"
  },
  session: {
    title: "Session",
    loggedIn: "Signed in as",
    role: "Role",
    hintCloseBrowser:
      "This session is stored for this tab only. Closing the tab or the browser (Chrome, Safari, …) ends the session unless you log in again.",
    notLoggedIn: "Sign in to save lesson XP and use AI speaking. Your token is saved automatically after login.",
    signInCta: "Sign in",
    adminPanelCta: "Admin: curriculum →"
  },
  adminCurriculum: {
    title: "Curriculum (admin)",
    subtitle: "Create and edit lessons, exercises, and vocabulary.",
    needLogin: "Sign in as an admin user.",
    forbidden: "Your account does not have admin rights.",
    loading: "Loading…",
    error: "Request failed.",
    filterLevel: "Level",
    allLevels: "All",
    newLesson: "New lesson",
    createLesson: "Create",
    deleteLesson: "Delete",
    confirmDeleteLesson: "Delete this lesson? Only allowed if no learner progress exists.",
    colTitle: "Title",
    colLevel: "Level",
    colXp: "XP",
    colMenschen: "Order",
    colEx: "Ex.",
    colVocab: "Vocab",
    actions: "Actions",
    edit: "Edit",
    backToList: "Back to list",
    saveLesson: "Save lesson content",
    saved: "Saved.",
    usageNotesLabel: "Usage notes (one line = one bullet)",
    examplesLabel: "Example phrases",
    addExampleRow: "Add phrase",
    exercisesSection: "Exercises",
    addExercise: "Add exercise",
    updateExercise: "Update",
    vocabSection: "Vocabulary",
    addVocab: "Add word",
    updateVocab: "Update word",
    question: "Question",
    choicesLines: "Choices (one per line, MCQ only)",
    correctAnswer: "Correct answer",
    sortOrder: "Sort order",
    exerciseType: "Type",
    word: "Word (DE)",
    article: "Article",
    meaningVi: "Meaning (VI)",
    plural: "Plural",
    ipa: "IPA",
    usageNote: "Usage note (VI)",
    audioUrl: "Audio URL",
    delete: "Delete",
    cancelEdit: "Cancel edit",
    editRow: "Edit",
    previewLearner: "Open as learner (new tab)"
  },
  adminLayout: {
    brand: "Admin · Curriculum",
    tagline: "Lesson & content management",
    navCurriculum: "Curriculum",
    backToStudent: "Student site",
    footerHint: "Admin area — learners use the separate student interface.",
    forbidden: "You need an administrator account to use this area.",
    needLogin: "Sign in as admin.",
    loading: "Loading…"
  }
};

const de = {
  nav: {
    brand: "Deutsch lernen",
    lessons: "Lektionen",
    progress: "Fortschritt",
    aiSpeech: "KI Sprechen",
    login: "Anmelden",
    register: "Registrieren",
    logout: "Abmelden"
  },
  lang: {
    label: "Sprache",
    en: "English",
    de: "Deutsch",
    vi: "Tiếng Việt"
  },
  home: {
    title: "Willkommen!",
    intro:
      "Dieses Frontend ruft die API unter /api/v1 auf: Lektionen, Fortschritt und KI-Sprachanalyse.",
    noteTitle: "Hinweis",
    noteBody:
      "Melden Sie sich an, um Lektionsfortschritt zu speichern (XP durch gelöste Übungen) und KI-Sprachanalyse zu nutzen. Die Sitzung bleibt in diesem Tab, bis Sie sich abmelden oder den Tab/Browser schließen."
  },
  lessons: {
    title: "Lektionen",
    level: "Niveau:",
    loading: "Laden…",
    error: "Lektionen konnten nicht geladen werden.",
    viewContent: "Inhalt ansehen"
  },
  lessonContent: {
    loading: "Inhalt wird geladen…",
    error: "Lektionsinhalt konnte nicht geladen werden.",
    vocab: "Wortschatz",
    plural: "Plural:",
    meaning: "Bedeutung:",
    exercises: "Übungen",
    noExercises: "Für diese Lektion gibt es noch keine Übungen.",
    speaking: "Sprechthemen",
    noTopics: "Für dieses Niveau gibt es noch keine Themen.",
    requirement: "Anforderung:",
    check: "Prüfen",
    correct: "Richtig",
    incorrect: "Falsch",
    answerMcqPlaceholder: "Antwort auswählen…",
    answerShortPlaceholder: "Gib deine Antwort ein…",
    answerRequired: "Antwort ist erforderlich.",
    score: "Punktzahl",
    submitLesson: "Lektion abgeben",
    submittingLesson: "Senden…",
    submitNeedAllChecked: "Alle Übungen prüfen, bevor Sie abgeben.",
    submitNeedAllAnswers: "Bitte geben Sie für alle Übungen eine Antwort ein, bevor Sie abgeben.",
    vocabHoverHint: "Mit der Maus über das Wort fahren (oder fokussieren) für IPA und Hinweise.",
    vocabPronunciation: "Aussprache (IPA):",
    vocabUsage: "Verwendung:",
    usageHowTo: "Verwendung (Hinweise)",
    sampleSentences: "Beispielsätze (Wortschatz der Lektion)",
    examplesSectionIntro: "Vor den Übungen — Muster und Sätze mit dem Wortschatz dieser Lektion.",
    examplesSectionEmpty:
      "Keine Beispiele vom Server. Backend neu starten (nicht prod) oder App aktualisieren, damit die Lektion synchronisiert.",
    listenGerman: "Anhören (deutsche Aussprache)",
    listenUnsupported:
      "Keine Audiodatei und keine Vorlesefunktion im Browser. Chrome/Edge probieren oder Lektion mit Audio-URLs nutzen."
  },
  progress: {
    titlePage: "Dein Fortschritt",
    howXpWorks:
      "XP wird automatisch gutgeschrieben, wenn du die Übungen einer Lektion abschließt und auf der Lektionsseite abgibst. Hier trägst du keine Punktzahl manuell ein — der Fortschritt basiert auf den Lektionen, die du bearbeitet hast.",
    studiedLessons: "Bearbeitete Lektionen",
    noLessonsYet: "Noch keine Lektion. Öffne eine Lektion und löse die Übungen, um XP zu sammeln.",
    colOrder: "#",
    colLesson: "Lektion",
    colLevel: "Niveau",
    colBest: "Beste Punktzahl",
    colXpLesson: "XP (Lektion)",
    colStatus: "Status",
    status_COMPLETED: "Abgeschlossen",
    status_NOT_COMPLETED: "Läuft",
    loginToView: "Melde dich an, um XP und Verlauf zu sehen.",
    loadingMyProgress: "Fortschritt wird geladen…",
    errorMyProgress: "Fortschritt konnte nicht geladen werden.",
    submitting: "Senden…",
    submit: "Absenden",
    errorGeneric: "Fehler",
    xpAdded: "XP gutgeschrieben:",
    bonus: "Bonus",
    totalXp: "Gesamt-XP:",
    leaderboard: "Bestenliste",
    leaderboardLoading: "Laden…",
    leaderboardError: "Bestenliste konnte nicht geladen werden."
  },
  ai: {
    title: "Ausspracheanalyse (KI-Sprechen)",
    audioOptional: "Audio (optional)",
    levelOptional: "Niveau (optional)",
    textOptional: "Text (optional)",
    textPlaceholder:
      "Transkript/Text einfügen, wenn kein Audio vorhanden oder Bewertung nur per Text.",
    topicIdOptional: "SpeakingTopicId (optional, hat Vorrang vor Niveau)",
    topicIdPlaceholder: "z. B. 1",
    analyzing: "Analyse…",
    analyze: "Analysieren",
    score: "Punktzahl",
    feedback: "Feedback",
    corrections: "Korrekturen"
  },
  errors: {
    noJwt: "Nicht angemeldet. Bitte über „Anmelden“ einloggen.",
    needAudioOrText: "Mindestens Audio oder Text senden."
  },
  auth: {
    loginTitle: "Anmelden",
    registerTitle: "Registrieren",
    seedHint:
      "Demo-Konten (BE-Seed, nicht prod): Lernende/r student / Student123 · Admin admin / Admin12345",
    username: "Benutzername",
    password: "Passwort",
    loginFailed: "Anmeldung fehlgeschlagen",
    registerFailed: "Registrierung fehlgeschlagen",
    registerRules: "Benutzername ≥ 3 Zeichen, Passwort ≥ 8 Zeichen (Backend-Regeln).",
    noAccount: "Noch kein Konto?",
    hasAccount: "Bereits ein Konto?"
  },
  session: {
    title: "Sitzung",
    loggedIn: "Angemeldet als",
    role: "Rolle",
    hintCloseBrowser:
      "Die Sitzung gilt nur für diesen Tab. Schließen Sie den Tab oder den Browser (Chrome, Safari, …), ist die Sitzung beendet.",
    notLoggedIn: "Melden Sie sich an, um Lektions-XP zu speichern und KI-Sprechen zu nutzen. Das Token wird nach dem Login gespeichert.",
    signInCta: "Anmelden",
    adminPanelCta: "Admin: Lehrplan →"
  },
  adminCurriculum: {
    title: "Lehrplan (Admin)",
    subtitle: "Lektionen, Übungen und Wortschatz bearbeiten.",
    needLogin: "Als Administrator anmelden.",
    forbidden: "Keine Admin-Rechte.",
    loading: "Laden…",
    error: "Anfrage fehlgeschlagen.",
    filterLevel: "Niveau",
    allLevels: "Alle",
    newLesson: "Neue Lektion",
    createLesson: "Anlegen",
    deleteLesson: "Löschen",
    confirmDeleteLesson: "Lektion löschen? Nur ohne Lernerfortschritt möglich.",
    colTitle: "Titel",
    colLevel: "Niveau",
    colXp: "XP",
    colMenschen: "Reihenfolge",
    colEx: "Üb.",
    colVocab: "Wortschatz",
    actions: "Aktionen",
    edit: "Bearbeiten",
    backToList: "Zur Liste",
    saveLesson: "Lektion speichern",
    saved: "Gespeichert.",
    usageNotesLabel: "Hinweise (eine Zeile = ein Punkt)",
    examplesLabel: "Beispielsätze",
    addExampleRow: "Satz hinzufügen",
    exercisesSection: "Übungen",
    addExercise: "Übung hinzufügen",
    updateExercise: "Aktualisieren",
    vocabSection: "Wortschatz",
    addVocab: "Wort hinzufügen",
    updateVocab: "Wort speichern",
    question: "Frage",
    choicesLines: "Optionen (je Zeile, nur MCQ)",
    correctAnswer: "Richtige Antwort",
    sortOrder: "Sortierung",
    exerciseType: "Typ",
    word: "Wort (DE)",
    article: "Artikel",
    meaningVi: "Bedeutung (VI)",
    plural: "Plural",
    ipa: "IPA",
    usageNote: "Hinweis (VI)",
    audioUrl: "Audio-URL",
    delete: "Löschen",
    cancelEdit: "Abbrechen",
    editRow: "Bearbeiten",
    previewLearner: "Als Lernende/r öffnen (neuer Tab)"
  },
  adminLayout: {
    brand: "Admin · Lehrplan",
    tagline: "Lektionen & Inhalte",
    navCurriculum: "Lehrplan",
    backToStudent: "Zur Lern-App",
    footerHint: "Admin-Bereich — Lernende nutzen die getrennte Oberfläche.",
    forbidden: "Administrator-Konto erforderlich.",
    needLogin: "Als Admin anmelden.",
    loading: "Laden…"
  }
};

const vi = {
  nav: {
    brand: "Học tiếng Đức",
    lessons: "Bài học",
    progress: "Tiến độ",
    aiSpeech: "AI nói",
    login: "Đăng nhập",
    register: "Đăng ký",
    logout: "Đăng xuất"
  },
  lang: {
    label: "Ngôn ngữ",
    en: "English",
    de: "Deutsch",
    vi: "Tiếng Việt"
  },
  home: {
    title: "Chào mừng!",
    intro:
      "Đây là FE mẫu gọi BE tại /api/v1: bài học, gửi tiến độ và phân tích giọng nói bằng AI.",
    noteTitle: "Lưu ý",
    noteBody:
      "Đăng nhập để lưu tiến độ bài học (XP cộng khi làm xong bài tập) và dùng phân tích giọng nói AI. Phiên được giữ trong tab trình duyệt cho đến khi đăng xuất hoặc đóng tab/trình duyệt."
  },
  lessons: {
    title: "Bài học",
    level: "Trình độ:",
    loading: "Đang tải…",
    error: "Không tải được bài học.",
    viewContent: "Xem nội dung"
  },
  lessonContent: {
    loading: "Đang tải nội dung…",
    error: "Không tải được nội dung bài học.",
    vocab: "Từ vựng",
    plural: "Số nhiều:",
    meaning: "Nghĩa:",
    exercises: "Bài tập",
    noExercises: "Chưa có bài tập cho bài này.",
    speaking: "Chủ đề luyện nói",
    noTopics: "Chưa có chủ đề cho trình độ này.",
    requirement: "Yêu cầu:",
    check: "Kiểm tra",
    correct: "Đúng",
    incorrect: "Sai",
    answerMcqPlaceholder: "Chọn một đáp án…",
    answerShortPlaceholder: "Nhập câu trả lời…",
    answerRequired: "Cần có đáp án.",
    score: "Điểm",
    submitLesson: "Nộp bài",
    submittingLesson: "Đang nộp…",
    submitNeedAllChecked: "Hãy kiểm tra hết các bài trước khi nộp.",
    submitNeedAllAnswers: "Vui lòng trả lời đầy đủ tất cả bài trước khi nộp.",
    vocabHoverHint: "Đưa chuột lên từ (hoặc Tab để focus) để xem phiên âm và cách dùng.",
    vocabPronunciation: "Phiên âm (IPA):",
    vocabUsage: "Cách dùng:",
    usageHowTo: "Cách dùng từ / cấu trúc",
    sampleSentences: "Câu mẫu (dùng từ vựng bài học)",
    examplesSectionIntro: "Trước khi làm bài tập — đọc gợi ý cách dùng và các câu mẫu có từ vựng của bài.",
    examplesSectionEmpty:
      "Chưa có câu mẫu từ máy chủ. Hãy khởi động lại backend (không dùng profile prod) hoặc cập nhật bản app để đồng bộ dữ liệu bài học.",
    listenGerman: "Nghe phát âm (tiếng Đức)",
    listenUnsupported:
      "Chưa có file âm thanh và trình duyệt không hỗ trợ đọc tiếng Đức. Thử Chrome/Edge hoặc bài có URL audio."
  },
  progress: {
    titlePage: "Tiến độ của bạn",
    howXpWorks:
      "Điểm XP được cộng tự động khi bạn làm xong bài tập trong một bài học và bấm nộp bài trên trang bài học. Trang này không nhập điểm tay — tiến độ dựa trên các bài bạn đã học.",
    studiedLessons: "Các bài đã học",
    noLessonsYet: "Chưa có bài nào. Vào danh sách bài học, làm hết bài tập để tích XP.",
    colOrder: "#",
    colLesson: "Bài học",
    colLevel: "Trình độ",
    colBest: "Điểm tốt nhất",
    colXpLesson: "XP từ bài",
    colStatus: "Trạng thái",
    status_COMPLETED: "Hoàn thành",
    status_NOT_COMPLETED: "Đang học",
    loginToView: "Đăng nhập để xem XP và lịch sử bài học.",
    loadingMyProgress: "Đang tải tiến độ…",
    errorMyProgress: "Không tải được tiến độ.",
    submitting: "Đang gửi…",
    submit: "Gửi",
    errorGeneric: "Lỗi",
    xpAdded: "XP được cộng:",
    bonus: "thưởng",
    totalXp: "Tổng XP:",
    leaderboard: "Bảng xếp hạng",
    leaderboardLoading: "Đang tải…",
    leaderboardError: "Không tải được bảng xếp hạng."
  },
  ai: {
    title: "Phân tích phát âm (AI nói)",
    audioOptional: "File âm thanh (tùy chọn)",
    levelOptional: "Trình độ (tùy chọn)",
    textOptional: "Văn bản (tùy chọn)",
    textPlaceholder:
      "Dán transcript/text nếu không có audio hoặc muốn chấm theo text.",
    topicIdOptional: "SpeakingTopicId (tùy chọn, ưu tiên hơn trình độ)",
    topicIdPlaceholder: "Ví dụ: 1",
    analyzing: "Đang phân tích…",
    analyze: "Phân tích",
    score: "Điểm",
    feedback: "Nhận xét",
    corrections: "Sửa lỗi"
  },
  errors: {
    noJwt: "Bạn chưa đăng nhập. Vào mục Đăng nhập và dùng tài khoản.",
    needAudioOrText: "Cần gửi ít nhất audio hoặc text."
  },
  auth: {
    loginTitle: "Đăng nhập",
    registerTitle: "Đăng ký",
    seedHint:
      "Tài khoản mẫu (BE seed khi không prod): học viên student / Student123 · quản trị admin / Admin12345",
    username: "Tên đăng nhập",
    password: "Mật khẩu",
    loginFailed: "Đăng nhập thất bại",
    registerFailed: "Đăng ký thất bại",
    registerRules: "Username ≥ 3 ký tự, mật khẩu ≥ 8 ký tự (theo rule BE).",
    noAccount: "Chưa có tài khoản?",
    hasAccount: "Đã có tài khoản?"
  },
  session: {
    title: "Phiên làm việc",
    loggedIn: "Đã đăng nhập",
    role: "Vai trò",
    hintCloseBrowser:
      "Phiên chỉ lưu trong tab này. Đóng tab hoặc tắt trình duyệt (Chrome, Safari, …) sẽ kết thúc phiên; đăng nhập lại nếu cần.",
    notLoggedIn: "Đăng nhập để lưu XP bài học và dùng AI nói. Token được lưu tự động sau khi đăng nhập.",
    signInCta: "Đăng nhập",
    adminPanelCta: "Khu quản trị lộ trình →"
  },
  adminCurriculum: {
    title: "Quản trị lộ trình",
    subtitle: "Tạo và sửa bài học, bài tập, từ vựng.",
    needLogin: "Đăng nhập bằng tài khoản admin.",
    forbidden: "Tài khoản của bạn không có quyền admin.",
    loading: "Đang tải…",
    error: "Gọi API thất bại.",
    filterLevel: "Trình độ",
    allLevels: "Tất cả",
    newLesson: "Tạo bài mới",
    createLesson: "Tạo",
    deleteLesson: "Xóa bài",
    confirmDeleteLesson: "Xóa bài này? Chỉ được phép khi chưa có học viên ghi tiến độ.",
    colTitle: "Tiêu đề",
    colLevel: "Cấp",
    colXp: "XP",
    colMenschen: "Thứ tự",
    colEx: "B.tập",
    colVocab: "Từ",
    actions: "Thao tác",
    edit: "Sửa",
    backToList: "Về danh sách",
    saveLesson: "Lưu nội dung bài",
    saved: "Đã lưu.",
    usageNotesLabel: "Ghi chú cách dùng (mỗi dòng = một ý)",
    examplesLabel: "Câu mẫu",
    addExampleRow: "Thêm câu",
    exercisesSection: "Bài tập",
    addExercise: "Thêm bài tập",
    updateExercise: "Cập nhật",
    vocabSection: "Từ vựng",
    addVocab: "Thêm từ",
    updateVocab: "Lưu từ",
    question: "Câu hỏi",
    choicesLines: "Lựa chọn (mỗi dòng một đáp án, chỉ MCQ)",
    correctAnswer: "Đáp án đúng",
    sortOrder: "Thứ tự hiển thị",
    exerciseType: "Loại",
    word: "Từ (DE)",
    article: "Mạo từ",
    meaningVi: "Nghĩa (VI)",
    plural: "Số nhiều",
    ipa: "IPA",
    usageNote: "Ghi chú dùng (VI)",
    audioUrl: "URL âm thanh",
    delete: "Xóa",
    cancelEdit: "Hủy sửa",
    editRow: "Sửa",
    previewLearner: "Xem như học viên (tab mới)"
  },
  adminLayout: {
    brand: "Quản trị · Lộ trình",
    tagline: "Quản lý bài học & nội dung",
    navCurriculum: "Lộ trình học",
    backToStudent: "Về trang học viên",
    footerHint: "Đang ở khu quản trị — học viên dùng giao diện riêng.",
    forbidden: "Cần tài khoản quản trị viên.",
    needLogin: "Đăng nhập bằng admin.",
    loading: "Đang tải…"
  }
};

export type Messages = typeof en;

export const dictionaries: Record<Locale, Messages> = {
  en,
  de,
  vi
};

export const defaultLocale: Locale = "vi";

export function isLocale(value: string | null | undefined): value is Locale {
  return value === "en" || value === "de" || value === "vi";
}

export function getMessage(dict: Messages, path: string): string {
  const parts = path.split(".");
  let cur: unknown = dict;
  for (const p of parts) {
    if (cur && typeof cur === "object" && p in (cur as object)) {
      cur = (cur as Record<string, unknown>)[p];
    } else {
      return path;
    }
  }
  return typeof cur === "string" ? cur : path;
}
