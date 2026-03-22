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
    hint:
      "Top menu: Lessons — choose a level, open a card, View content. Progress — your XP and lesson list. AI Speaking — upload audio or paste text (sign in first). Session is per browser tab."
  },
  lessons: {
    title: "Lessons",
    pageHint: "Pick a level, then open a lesson with View content. XP is counted when you submit from the lesson page.",
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
    pageHint:
      "Flow: skim vocabulary and examples → answer every exercise → Submit lesson (you must be signed in for XP). Use Check on each item or submit to validate all.",
    examplesSectionIntro: "Patterns and sample sentences using this lesson’s words.",
    examplesSectionEmpty:
      "No examples from the server. If you run your own backend, check lesson data or non-prod seed.",
    listenGerman: "Listen (German pronunciation)",
    listenUnsupported:
      "No audio file and this browser does not support read-aloud. Try Chrome or Edge, or use a lesson with audio URLs."
  },
  progress: {
    titlePage: "Your progress",
    pageHint:
      "XP updates when you submit a lesson after doing the exercises. This page is read-only: see totals, per-lesson XP, and status.",
    leaderboardHint: "Ranking by total XP (all learners).",
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
    pageHint:
      "Sign in. Add an audio file and/or text, pick level (or topic ID if you have one), then Analyze. At least one of audio or text is required.",
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
    loginPageHint:
      "Enter your username and password, then Log in. After login you can save lesson XP and use AI speaking.",
    seedHint:
      "Demo (when backend seeds non-prod): student / Student123 · admin / Admin12345",
    registerPageHint: "Choose a username and password that meet the rules below, then register — you will be signed in automatically.",
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
    hintCloseBrowser: "Session is for this tab only — close tab/browser and you’ll need to sign in again.",
    notLoggedIn: "Sign in to save XP and use AI speaking.",
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
    hint:
      "Menü oben: Lektionen — Niveau wählen, Karte öffnen, Inhalt ansehen. Fortschritt — XP und Liste. KI Sprechen — Audio oder Text (zuerst anmelden). Sitzung gilt nur für diesen Tab."
  },
  lessons: {
    title: "Lektionen",
    pageHint:
      "Niveau wählen, dann „Inhalt ansehen“. XP gibt es, wenn du auf der Lektionsseite abgibst.",
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
    pageHint:
      "Ablauf: Wortschatz und Beispiele lesen → alle Übungen beantworten → Lektion abgeben (für XP anmelden). Pro Übung „Prüfen“ oder beim Abgeben alles prüfen.",
    examplesSectionIntro: "Muster und Beispielsätze mit dem Wortschatz dieser Lektion.",
    examplesSectionEmpty:
      "Keine Beispiele vom Server. Eigenes Backend prüfen oder Lektionsdaten/Seed (nicht prod).",
    listenGerman: "Anhören (deutsche Aussprache)",
    listenUnsupported:
      "Keine Audiodatei und keine Vorlesefunktion im Browser. Chrome/Edge probieren oder Lektion mit Audio-URLs nutzen."
  },
  progress: {
    titlePage: "Dein Fortschritt",
    pageHint:
      "XP kommt, wenn du nach den Übungen auf der Lektionsseite abgibst. Diese Seite zeigt nur Übersicht und Status.",
    leaderboardHint: "Rangliste nach Gesamt-XP.",
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
    pageHint:
      "Anmelden. Audio und/oder Text hinzufügen, Niveau (oder Themen-ID) wählen, dann Analysieren. Mindestens Audio oder Text nötig.",
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
    loginPageHint:
      "Benutzername und Passwort eingeben, dann anmelden. Danach XP speichern und KI-Sprechen nutzen.",
    seedHint:
      "Demo (BE-Seed, nicht prod): student / Student123 · admin / Admin12345",
    registerPageHint:
      "Benutzername und Passwort nach den Regeln wählen — nach der Registrierung bist du automatisch angemeldet.",
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
    hintCloseBrowser: "Nur dieser Tab — Tab/Browser schließen, dann neu anmelden.",
    notLoggedIn: "Anmelden für XP und KI-Sprechen.",
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
    hint:
      "Menu trên: Bài học — chọn cấp độ → mở thẻ bài → Xem nội dung. Tiến độ: XP và danh sách bài. AI nói: tải audio hoặc dán chữ (đăng nhập trước). Phiên theo từng tab trình duyệt."
  },
  lessons: {
    title: "Bài học",
    pageHint: "Chọn cấp độ, bấm Xem nội dung vào bài. XP chỉ tính khi bạn nộp bài trên trang bài học.",
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
    pageHint:
      "Thứ tự: xem từ vựng & câu mẫu → trả lời hết bài tập → Nộp bài (cần đăng nhập để cộng XP). Có thể bấm Kiểm tra từng câu hoặc nộp để hệ thống chấm hết.",
    examplesSectionIntro: "Gợi ý cách dùng và câu mẫu có từ vựng của bài.",
    examplesSectionEmpty:
      "Chưa có câu mẫu từ server. Kiểm tra backend hoặc dữ liệu bài / seed (môi trường không prod).",
    listenGerman: "Nghe phát âm (tiếng Đức)",
    listenUnsupported:
      "Chưa có file âm thanh và trình duyệt không hỗ trợ đọc tiếng Đức. Thử Chrome/Edge hoặc bài có URL audio."
  },
  progress: {
    titlePage: "Tiến độ của bạn",
    pageHint:
      "XP cập nhật khi bạn nộp bài sau khi làm bài tập. Trang này chỉ xem: tổng XP, XP từng bài, trạng thái.",
    leaderboardHint: "Xếp hạng theo tổng XP (mọi học viên).",
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
    pageHint:
      "Đăng nhập. Thêm file âm thanh và/hoặc văn bản, chọn cấp độ (hoặc SpeakingTopicId nếu có), rồi Phân tích. Bắt buộc có ít nhất audio hoặc text.",
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
    loginPageHint:
      "Nhập tên đăng nhập và mật khẩu, bấm Đăng nhập. Sau đó mới lưu XP bài học và dùng AI nói.",
    seedHint:
      "Demo (BE seed khi không prod): student / Student123 · admin / Admin12345",
    registerPageHint:
      "Chọn username và mật khẩu đúng quy tắc bên dưới — sau đăng ký bạn được đăng nhập luôn.",
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
    hintCloseBrowser: "Phiên theo tab này — đóng tab/trình duyệt là hết, cần đăng nhập lại.",
    notLoggedIn: "Đăng nhập để lưu XP và dùng AI nói.",
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
