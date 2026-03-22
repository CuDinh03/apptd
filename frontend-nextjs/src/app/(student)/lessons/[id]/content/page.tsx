"use client";

import { useEffect, useMemo, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  ExerciseDto,
  fetchLessons,
  fetchLessonContent,
  gradeExercise,
  submitProgress,
  type SubmitProgressResponse,
  VocabularyDto
} from "@/lib/api";
import { queryKeys } from "@/lib/query-keys";
import { celebrateXp } from "@/lib/xp-celebration";
import { useParams, useRouter } from "next/navigation";

import { useLanguage } from "@/contexts/LanguageContext";
import { useAuth } from "@/contexts/AuthContext";
import VocabularyHoverWord from "@/components/VocabularyHoverWord";
import LessonUsageSection from "@/components/LessonUsageSection";
import GermanListenButton from "@/components/GermanListenButton";
import StudentPageHint from "@/components/StudentPageHint";

function articleColor(article?: string) {
  const a = (article ?? "").toLowerCase();
  if (a === "der") return "bg-blue-100 text-blue-800";
  if (a === "die") return "bg-red-100 text-red-800";
  if (a === "das") return "bg-green-100 text-green-800";
  return "bg-gray-100 text-gray-700";
}

function ArticlePill({ article }: { article?: string }) {
  return (
    <span className={`inline-flex items-center rounded px-2 py-0.5 text-xs font-medium ${articleColor(article)}`}>
      {article ?? "-"}
    </span>
  );
}

export default function LessonContentPage() {
  const { t } = useLanguage();
  const { token, ready } = useAuth();
  const queryClient = useQueryClient();
  const router = useRouter();
  const params = useParams<{ id: string }>();
  const id = Number(params.id);

  const query = useQuery({
    queryKey: queryKeys.lessonContent(id),
    queryFn: () => fetchLessonContent(id),
    enabled: Number.isFinite(id)
  });

  const data = useMemo(() => (query.data ? query.data : null), [query.data]);

  const exercises = data?.exercises ?? [];
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [grading, setGrading] = useState<Record<number, { correct: boolean }>>({});
  const [checkingById, setCheckingById] = useState<Record<number, boolean>>({});

  const [checkError, setCheckError] = useState<string | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [submitResult, setSubmitResult] = useState<SubmitProgressResponse | null>(null);

  useEffect(() => {
    setAnswers({});
    setGrading({});
    setCheckingById({});
    setCheckError(null);
    setSubmitError(null);
    setSubmitLoading(false);
    setSubmitResult(null);
  }, [id]);

  const totalExercises = exercises.length;
  const correctCount = useMemo(
    () => exercises.reduce((acc, ex) => acc + (grading[ex.id]?.correct ? 1 : 0), 0),
    [exercises, grading]
  );
  const scorePercent = totalExercises > 0 ? Math.round((correctCount / totalExercises) * 100) : 0;
  const allAnswered = totalExercises === 0 || exercises.every((ex) => (answers[ex.id] ?? "").trim() !== "");

  const onCheck = async (ex: ExerciseDto) => {
    if (!ready || !token) {
      setCheckError(t("errors.noJwt"));
      return;
    }

    const raw = answers[ex.id] ?? "";
    const answer = raw.trim();
    if (!answer) {
      setCheckError(t("lessonContent.answerRequired"));
      return;
    }

    setCheckError(null);
    setCheckingById((prev) => ({ ...prev, [ex.id]: true }));
    try {
      const res = await gradeExercise(token, { exerciseId: ex.id, answer });
      setGrading((prev) => ({ ...prev, [ex.id]: { correct: res.correct } }));
    } catch (err) {
      setCheckError(err instanceof Error ? err.message : String(err));
    } finally {
      setCheckingById((prev) => ({ ...prev, [ex.id]: false }));
    }
  };

  const onSubmitLesson = async () => {
    if (!ready || !token) {
      setSubmitError(t("errors.noJwt"));
      return;
    }
    if (!allAnswered) {
      setSubmitError(t("lessonContent.submitNeedAllAnswers"));
      return;
    }

    setSubmitError(null);
    setCheckError(null);
    setSubmitLoading(true);
    try {
      // Auto-check tất cả trước khi submit (giống Duolingo).
      const nextGrading: Record<number, { correct: boolean }> = {};
      let nextCorrectCount = 0;

      for (const ex of exercises) {
        const raw = answers[ex.id] ?? "";
        const answer = raw.trim();
        if (!answer) {
          // Tránh race khi user sửa input trong lúc đang submit.
          throw new Error(t("lessonContent.submitNeedAllAnswers"));
        }

        setCheckingById((prev) => ({ ...prev, [ex.id]: true }));
        try {
          const res = await gradeExercise(token, { exerciseId: ex.id, answer });
          nextGrading[ex.id] = { correct: res.correct };
          if (res.correct) nextCorrectCount++;
        } finally {
          setCheckingById((prev) => ({ ...prev, [ex.id]: false }));
        }
      }

      setGrading(nextGrading);

      const nextScorePercent = totalExercises > 0 ? Math.round((nextCorrectCount / totalExercises) * 100) : 0;
      const submitRes = await submitProgress(token, { lessonId: id, scorePercent: nextScorePercent });
      setSubmitResult(submitRes);
      if (submitRes.xpAwarded > 0) {
        celebrateXp({ highImpact: submitRes.bonusXp > 0 });
      }
      await queryClient.invalidateQueries({ queryKey: ["myProgress"] });

      // Điều hướng sang lesson tiếp theo cùng level.
      if (data?.lesson?.level) {
        const list = await fetchLessons(data.lesson.level);
        const sorted = [...list].sort((a, b) => {
          const ao = a.menschenOrder ?? 9999;
          const bo = b.menschenOrder ?? 9999;
          if (ao !== bo) return ao - bo;
          return a.id - b.id;
        });

        const currentOrder = data.lesson.menschenOrder ?? null;
        const idx =
          currentOrder != null
            ? sorted.findIndex((l) => l.menschenOrder === currentOrder)
            : sorted.findIndex((l) => l.id === id);

        const next = idx >= 0 ? sorted[idx + 1] : undefined;
        if (next) {
          router.push(`/lessons/${next.id}/content`);
          return;
        }
      }

      // Không có lesson tiếp theo thì quay về progress.
      router.push("/progress");
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : String(err));
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <div className="space-y-4">
      {query.isLoading && <div className="text-sm text-gray-700">{t("lessonContent.loading")}</div>}
      {query.isError && (
        <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">
          {t("lessonContent.error")}
        </div>
      )}

      {data && (
        <>
          <div className="rounded-md border border-gray-200 bg-white p-4">
            <div className="text-sm text-gray-600">
              {data.lesson.level} · {data.lesson.category} · +{data.lesson.xpReward} XP
            </div>
            <div className="mt-1 text-lg font-semibold">{data.lesson.title}</div>
          </div>

          <StudentPageHint>{t("lessonContent.pageHint")}</StudentPageHint>

          <div className="rounded-md border border-gray-200 bg-white p-4">
            <h2 className="text-base font-semibold">{t("lessonContent.vocab")}</h2>
            <div className="mt-3 grid gap-3 md:grid-cols-2">
              {data.vocabularies.map((v: VocabularyDto, idx: number) => {
                const germanSpeak = [v.article, v.word].filter(Boolean).join(" ").trim();
                return (
                  <div key={`${v.word}-${idx}`} className="rounded-md border border-gray-100 p-3">
                    <div className="flex items-start justify-between gap-2">
                      <div className="min-w-0 flex-1">
                        <VocabularyHoverWord
                          articleSlot={<ArticlePill article={v.article} />}
                          word={v.word}
                          meaningVi={v.meaningVi}
                          pronunciationIpa={v.pronunciationIpa ?? undefined}
                          usageNoteVi={v.usageNoteVi ?? undefined}
                          labels={{
                            hoverHint: t("lessonContent.vocabHoverHint"),
                            meaning: t("lessonContent.meaning"),
                            pronunciation: t("lessonContent.vocabPronunciation"),
                            usage: t("lessonContent.vocabUsage")
                          }}
                        />
                      </div>
                      <GermanListenButton
                        text={germanSpeak}
                        audioUrl={v.audioUrl ?? undefined}
                        ariaLabel={t("lessonContent.listenGerman")}
                        titleUnsupported={t("lessonContent.listenUnsupported")}
                      />
                    </div>
                    {v.pluralForm && (
                      <div className="text-sm text-gray-700">
                        {t("lessonContent.plural")} {v.pluralForm}
                      </div>
                    )}
                    <div className="mt-1 text-sm text-gray-700">
                      {t("lessonContent.meaning")} {v.meaningVi}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <LessonUsageSection
            usageNotes={data.usageNotes ?? []}
            examplePhrases={data.examplePhrases ?? []}
            emptyHint={t("lessonContent.examplesSectionEmpty")}
            labels={{
              sectionIntro: t("lessonContent.examplesSectionIntro"),
              usageTitle: t("lessonContent.usageHowTo"),
              examplesTitle: t("lessonContent.sampleSentences"),
              listenGerman: t("lessonContent.listenGerman"),
              listenUnsupported: t("lessonContent.listenUnsupported")
            }}
          />

          <div className="rounded-md border border-gray-200 bg-white p-4">
            <h2 className="text-base font-semibold">{t("lessonContent.exercises")}</h2>
            {(data.exercises ?? []).length === 0 ? (
              <div className="mt-2 text-sm text-gray-600">{t("lessonContent.noExercises")}</div>
            ) : (
              <div className="mt-3 space-y-3">
                {exercises.map((ex: ExerciseDto) => {
                  const currentAnswer = answers[ex.id] ?? "";
                  const status = grading[ex.id];
                  const isMcq = ex.type === "MCQ";
                  const isChecking = checkingById[ex.id] ?? false;

                  return (
                    <div key={ex.id} className="rounded-md border border-gray-100 p-3">
                      <div className="text-xs font-medium text-gray-500">{ex.type}</div>
                      <div className="mt-1 text-sm font-medium text-gray-900">{ex.questionText}</div>

                      {status && (
                        <div
                          className={`mt-2 inline-flex items-center rounded px-2 py-0.5 text-xs font-medium ${
                            status.correct ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                          }`}
                        >
                          {status.correct ? t("lessonContent.correct") : t("lessonContent.incorrect")}
                        </div>
                      )}

                      {isMcq ? (
                        <label className="mt-3 block text-sm">
                          <span className="sr-only">Answer</span>
                          <select
                            className="mt-1 w-full rounded-md border border-gray-300 bg-white p-2 text-sm"
                            value={currentAnswer}
                            onChange={(e) => setAnswers((prev) => ({ ...prev, [ex.id]: e.target.value }))}
                            aria-label="MCQ answer"
                          >
                            <option value="">{t("lessonContent.answerMcqPlaceholder")}</option>
                            {(ex.choices ?? []).map((c) => (
                              <option key={c} value={c}>
                                {c}
                              </option>
                            ))}
                          </select>
                        </label>
                      ) : (
                        <label className="mt-3 block text-sm">
                          <span className="sr-only">Answer</span>
                          <textarea
                            className="mt-1 w-full resize-y rounded-md border border-gray-300 p-2 text-sm"
                            rows={3}
                            value={currentAnswer}
                            onChange={(e) => setAnswers((prev) => ({ ...prev, [ex.id]: e.target.value }))}
                            placeholder={t("lessonContent.answerShortPlaceholder")}
                          />
                        </label>
                      )}

                      <div className="mt-3 flex items-center gap-2">
                        <button
                          type="button"
                          disabled={!ready || !token || submitLoading || isChecking || !currentAnswer.trim()}
                          className="rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white disabled:opacity-60"
                          onClick={() => onCheck(ex)}
                        >
                          {isChecking ? "…" : t("lessonContent.check")}
                        </button>
                      </div>
                    </div>
                  );
                })}

                {checkError && (
                  <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">
                    {checkError}
                  </div>
                )}

                <div className="rounded-md border border-gray-200 bg-gray-50 p-3">
                  <div className="text-sm font-medium">
                    {t("lessonContent.score")}: {scorePercent}%
                  </div>
                  {submitResult == null && (
                    <button
                      type="button"
                      disabled={!ready || !token || submitLoading || !allAnswered}
                      className="mt-3 w-full rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
                      onClick={onSubmitLesson}
                    >
                      {submitLoading ? t("lessonContent.submittingLesson") : t("lessonContent.submitLesson")}
                    </button>
                  )}

                  {submitError && (
                    <div className="mt-2 rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">
                      {submitError}
                    </div>
                  )}

                  {submitResult && (
                    <div className="mt-3 rounded-md border border-gray-200 bg-white p-3 text-sm">
                      <div>
                        {t("progress.xpAdded")}{" "}
                        <span className="font-semibold">{submitResult.xpAwarded}</span> (+ {t("progress.bonus")}{" "}
                        <span className="font-semibold">{submitResult.bonusXp}</span>)
                      </div>
                      <div className="mt-1">
                        {t("progress.totalXp")} <span className="font-semibold">{submitResult.totalXp}</span>
                      </div>
                      <div className="mt-2 text-xs text-gray-600">
                        Attempts: {submitResult.attempts} · Best: {submitResult.bestScorePercent}%
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>

          <div className="rounded-md border border-gray-200 bg-white p-4">
            <h2 className="text-base font-semibold">{t("lessonContent.speaking")}</h2>
            {data.speakingTopics.length === 0 ? (
              <div className="mt-2 text-sm text-gray-600">{t("lessonContent.noTopics")}</div>
            ) : (
              <div className="mt-3 space-y-3">
                {data.speakingTopics.map((topic) => (
                  <div key={topic.id} className="rounded-md border border-gray-100 p-3">
                    <div className="text-sm font-medium">
                      {t("lessonContent.requirement")} {topic.levelRequirement}
                    </div>
                    <div className="mt-1 whitespace-pre-wrap text-sm text-gray-700">{topic.contextPrompt}</div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}

