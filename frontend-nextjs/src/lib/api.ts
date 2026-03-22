export const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

/** Chuẩn response BE (theo .cursorrules). */
export type ApiResponse<T> = {
  success: boolean;
  message: string | null;
  errorCode: string | null;
  data: T | null;
  timestamp: string;
};

async function parseBody<T>(res: Response): Promise<ApiResponse<T>> {
  const text = await res.text();
  try {
    return JSON.parse(text) as ApiResponse<T>;
  } catch {
    throw new Error(`Invalid JSON: ${text.slice(0, 200)}`);
  }
}

function assertOk<T>(res: Response, body: ApiResponse<T>): asserts body is ApiResponse<T> & { success: true; data: T } {
  if (!res.ok || !body.success) {
    throw new Error(body.message ?? body.errorCode ?? `HTTP ${res.status}`);
  }
}

function assertSuccess(res: Response, body: ApiResponse<unknown>): asserts body is ApiResponse<unknown> & { success: true } {
  if (!res.ok || !body.success) {
    throw new Error(body.message ?? body.errorCode ?? `HTTP ${res.status}`);
  }
}

export type LessonDto = {
  id: number;
  title: string;
  level: string;
  category: string;
  menschenOrder?: number | null;
  xpReward: number;
};

export type VocabularyDto = {
  word: string;
  article?: string;
  pluralForm?: string;
  meaningVi: string;
  pronunciationIpa?: string | null;
  usageNoteVi?: string | null;
  audioUrl?: string;
};

export type SpeakingTopicDto = {
  id: number;
  contextPrompt: string;
  levelRequirement: string;
};

export type ExerciseDto = {
  id: number;
  type: string;
  questionText: string;
  choices: string[];
};

export type ExamplePhraseDto = {
  german: string;
  vietnamese: string;
  audioUrl?: string | null;
};

export type LessonContentResponse = {
  lesson: LessonDto;
  vocabularies: VocabularyDto[];
  usageNotes: string[];
  examplePhrases: ExamplePhraseDto[];
  speakingTopics: SpeakingTopicDto[];
  exercises: ExerciseDto[];
};

export type LeaderboardEntryDto = {
  userId: number;
  username: string;
  role: string;
  totalXp: number;
};

export type SubmitProgressRequest = {
  lessonId: number;
  scorePercent: number;
};

export type SubmitProgressResponse = {
  lessonId: number;
  scorePercent: number;
  xpAwarded: number;
  bonusXp: number;
  totalXp: number;
  lastScorePercent: number;
  bestScorePercent: number;
  attempts: number;
};

export type LessonProgressRowDto = {
  lessonId: number;
  menschenOrder: number | null;
  lessonTitle: string;
  level: string;
  status: string;
  bestScorePercent: number;
  lastScorePercent: number;
  attempts: number;
  xpEarnedFromLesson: number;
  completedAt: string | null;
};

export type UserProgressSummaryDto = {
  username: string;
  totalXp: number;
  lessons: LessonProgressRowDto[];
};

export type SpeechAnalysisResponse = {
  score: number;
  feedback: string;
  corrections: string;
};

export type GradeExerciseResponse = {
  exerciseId: number;
  correct: boolean;
};

export type AuthTokenResponse = {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
  userId: number;
  username: string;
  role: string;
};

function normalizeBearer(token: string) {
  const t = token.trim();
  if (!t) return "";
  return t.startsWith("Bearer ") ? t : `Bearer ${t}`;
}

export async function fetchLessons(level?: string): Promise<LessonDto[]> {
  const url = new URL(`${API_URL}/lessons`);
  if (level) url.searchParams.set("level", level);

  const res = await fetch(url.toString(), { method: "GET" });
  const body = await parseBody<LessonDto[]>(res);
  assertOk(res, body);
  return body.data ?? [];
}

export async function fetchLessonContent(id: number): Promise<LessonContentResponse> {
  const res = await fetch(`${API_URL}/lessons/${id}/content`, { method: "GET" });
  const body = await parseBody<LessonContentResponse>(res);
  assertOk(res, body);
  const d = body.data as LessonContentResponse;
  return {
    ...d,
    usageNotes: Array.isArray(d.usageNotes) ? d.usageNotes : [],
    examplePhrases: Array.isArray(d.examplePhrases) ? d.examplePhrases : [],
    vocabularies: Array.isArray(d.vocabularies) ? d.vocabularies : [],
    exercises: Array.isArray(d.exercises) ? d.exercises : [],
    speakingTopics: Array.isArray(d.speakingTopics) ? d.speakingTopics : []
  };
}

export async function fetchLeaderboard(limit = 10): Promise<LeaderboardEntryDto[]> {
  const url = new URL(`${API_URL}/users/leaderboard`);
  url.searchParams.set("limit", String(limit));

  const res = await fetch(url.toString(), { method: "GET" });
  const body = await parseBody<LeaderboardEntryDto[]>(res);
  assertOk(res, body);
  return body.data ?? [];
}

export async function fetchMyProgress(token: string): Promise<UserProgressSummaryDto> {
  const res = await fetch(`${API_URL}/progress/me`, {
    method: "GET",
    headers: { Authorization: normalizeBearer(token) }
  });
  const body = await parseBody<UserProgressSummaryDto>(res);
  assertOk(res, body);
  return body.data as UserProgressSummaryDto;
}

export async function submitProgress(token: string, payload: SubmitProgressRequest): Promise<SubmitProgressResponse> {
  const res = await fetch(`${API_URL}/progress/submit`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: normalizeBearer(token)
    },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<SubmitProgressResponse>(res);
  assertOk(res, body);
  return body.data as SubmitProgressResponse;
}

export async function analyzeSpeech(
  token: string,
  args: {
    audio?: File;
    text?: string;
    speakingTopicId?: number;
    level?: string;
  }
): Promise<SpeechAnalysisResponse> {
  const form = new FormData();
  if (args.audio) form.append("audio", args.audio);
  if (args.text) form.append("text", args.text);
  if (args.speakingTopicId != null) form.append("speakingTopicId", String(args.speakingTopicId));
  if (args.level) form.append("level", args.level);

  const res = await fetch(`${API_URL}/ai/analyze-speech`, {
    method: "POST",
    headers: {
      Authorization: normalizeBearer(token)
    },
    body: form
  });

  const body = await parseBody<SpeechAnalysisResponse>(res);
  assertOk(res, body);
  return body.data as SpeechAnalysisResponse;
}

export async function loginRequest(username: string, password: string): Promise<AuthTokenResponse> {
  const res = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });
  const body = await parseBody<AuthTokenResponse>(res);
  assertOk(res, body);
  return body.data as AuthTokenResponse;
}

export async function registerRequest(username: string, password: string): Promise<AuthTokenResponse> {
  const res = await fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });
  const body = await parseBody<AuthTokenResponse>(res);
  assertOk(res, body);
  return body.data as AuthTokenResponse;
}

export async function gradeExercise(
  token: string,
  payload: { exerciseId: number; answer: string }
): Promise<GradeExerciseResponse> {
  const res = await fetch(`${API_URL}/exercises/grade`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: normalizeBearer(token)
    },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<GradeExerciseResponse>(res);
  assertOk(res, body);
  return body.data as GradeExerciseResponse;
}

// --- Admin curriculum (ROLE_ADMIN) ---

export type AdminLessonSummaryDto = {
  id: number;
  title: string;
  level: string;
  category: string;
  xpReward: number;
  menschenOrder: number | null;
  exerciseCount: number;
  vocabularyCount: number;
};

export type ExamplePhraseAdminDto = {
  german: string;
  vietnamese?: string | null;
  audioUrl?: string | null;
};

export type AdminLessonMetaDto = {
  id: number;
  title: string;
  level: string;
  category: string;
  xpReward: number;
  menschenOrder: number | null;
};

export type AdminExerciseDto = {
  id: number;
  lessonId: number;
  exerciseType: string;
  questionText: string;
  choices: string[];
  correctAnswer: string;
  sortOrder: number;
};

export type AdminVocabularyDto = {
  id: number;
  lessonId: number;
  word: string;
  article?: string | null;
  pluralForm?: string | null;
  meaningVi: string;
  pronunciationIpa?: string | null;
  usageNoteVi?: string | null;
  audioUrl?: string | null;
};

export type AdminLessonDetailDto = {
  lesson: AdminLessonMetaDto;
  usageNotes: string[];
  examplePhrases: ExamplePhraseAdminDto[];
  exercises: AdminExerciseDto[];
  vocabularies: AdminVocabularyDto[];
};

export async function adminListLessons(token: string, level?: string): Promise<AdminLessonSummaryDto[]> {
  const url = new URL(`${API_URL}/admin/curriculum/lessons`);
  if (level) url.searchParams.set("level", level);
  const res = await fetch(url.toString(), { headers: { Authorization: normalizeBearer(token) } });
  const body = await parseBody<AdminLessonSummaryDto[]>(res);
  assertOk(res, body);
  return body.data ?? [];
}

export async function adminGetLesson(token: string, lessonId: number): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/lessons/${lessonId}`, {
    headers: { Authorization: normalizeBearer(token) }
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminCreateLesson(
  token: string,
  payload: { title: string; level: string; category: string; xpReward: number; menschenOrder?: number | null }
): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/lessons`, {
    method: "POST",
    headers: { "Content-Type": "application/json", Authorization: normalizeBearer(token) },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminReplaceLesson(
  token: string,
  lessonId: number,
  payload: {
    title: string;
    level: string;
    category: string;
    xpReward: number;
    menschenOrder?: number | null;
    usageNotes: string[];
    examplePhrases: ExamplePhraseAdminDto[];
  }
): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/lessons/${lessonId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", Authorization: normalizeBearer(token) },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminDeleteLesson(token: string, lessonId: number): Promise<void> {
  const res = await fetch(`${API_URL}/admin/curriculum/lessons/${lessonId}`, {
    method: "DELETE",
    headers: { Authorization: normalizeBearer(token) }
  });
  const body = await parseBody<unknown>(res);
  assertSuccess(res, body);
}

export async function adminCreateExercise(
  token: string,
  lessonId: number,
  payload: { exerciseType: string; questionText: string; choices?: string[]; correctAnswer: string; sortOrder?: number | null }
): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/lessons/${lessonId}/exercises`, {
    method: "POST",
    headers: { "Content-Type": "application/json", Authorization: normalizeBearer(token) },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminReplaceExercise(
  token: string,
  exerciseId: number,
  payload: { exerciseType: string; questionText: string; choices?: string[]; correctAnswer: string; sortOrder: number }
): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/exercises/${exerciseId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", Authorization: normalizeBearer(token) },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminDeleteExercise(token: string, exerciseId: number): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/exercises/${exerciseId}`, {
    method: "DELETE",
    headers: { Authorization: normalizeBearer(token) }
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminCreateVocabulary(
  token: string,
  lessonId: number,
  payload: {
    word: string;
    article?: string | null;
    pluralForm?: string | null;
    meaningVi: string;
    pronunciationIpa?: string | null;
    usageNoteVi?: string | null;
    audioUrl?: string | null;
  }
): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/lessons/${lessonId}/vocabularies`, {
    method: "POST",
    headers: { "Content-Type": "application/json", Authorization: normalizeBearer(token) },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminReplaceVocabulary(
  token: string,
  vocabularyId: number,
  payload: {
    word: string;
    article?: string | null;
    pluralForm?: string | null;
    meaningVi: string;
    pronunciationIpa?: string | null;
    usageNoteVi?: string | null;
    audioUrl?: string | null;
  }
): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/vocabularies/${vocabularyId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", Authorization: normalizeBearer(token) },
    body: JSON.stringify(payload)
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}

export async function adminDeleteVocabulary(token: string, vocabularyId: number): Promise<AdminLessonDetailDto> {
  const res = await fetch(`${API_URL}/admin/curriculum/vocabularies/${vocabularyId}`, {
    method: "DELETE",
    headers: { Authorization: normalizeBearer(token) }
  });
  const body = await parseBody<AdminLessonDetailDto>(res);
  assertOk(res, body);
  return body.data as AdminLessonDetailDto;
}
