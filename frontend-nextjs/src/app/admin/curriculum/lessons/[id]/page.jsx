"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useCallback, useEffect, useState } from "react";

import {
  adminCreateExercise,
  adminCreateVocabulary,
  adminDeleteExercise,
  adminDeleteVocabulary,
  adminGetLesson,
  adminReplaceExercise,
  adminReplaceLesson,
  adminReplaceVocabulary
} from "@/lib/api";
import { useAuth } from "@/contexts/AuthContext";
import { useLanguage } from "@/contexts/LanguageContext";

const LEVELS = ["A1", "A2", "B1", "B2"];
const CATEGORIES = ["GRAMMAR", "VOCABULARY", "COMMUNICATION"];
const EX_TYPES = ["MCQ", "SHORT_TEXT"];

function emptyExample() {
  return { german: "", vietnamese: "", audioUrl: "" };
}

export default function AdminLessonEditorPage() {
  const { t } = useLanguage();
  const { token, user, ready } = useAuth();
  const params = useParams();
  const lessonId = Number(params.id);

  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState(null);
  const [msg, setMsg] = useState(null);

  const [title, setTitle] = useState("");
  const [level, setLevel] = useState("A1");
  const [category, setCategory] = useState("VOCABULARY");
  const [xpReward, setXpReward] = useState(20);
  const [menschenOrder, setMenschenOrder] = useState("");
  const [usageNotesText, setUsageNotesText] = useState("");
  const [examples, setExamples] = useState([emptyExample()]);

  const [exType, setExType] = useState("MCQ");
  const [exQuestion, setExQuestion] = useState("");
  const [exChoices, setExChoices] = useState("");
  const [exCorrect, setExCorrect] = useState("");
  const [exSort, setExSort] = useState(1);
  const [editingExId, setEditingExId] = useState(null);

  const [vWord, setVWord] = useState("");
  const [vArticle, setVArticle] = useState("");
  const [vPlural, setVPlural] = useState("");
  const [vMeaning, setVMeaning] = useState("");
  const [vIpa, setVIpa] = useState("");
  const [vUsage, setVUsage] = useState("");
  const [vAudio, setVAudio] = useState("");
  const [editingVId, setEditingVId] = useState(null);

  const applyDetail = useCallback((d) => {
    setDetail(d);
    const l = d.lesson;
    setTitle(l.title);
    setLevel(l.level);
    setCategory(l.category);
    setXpReward(l.xpReward);
    setMenschenOrder(l.menschenOrder != null ? String(l.menschenOrder) : "");
    setUsageNotesText((d.usageNotes ?? []).join("\n"));
    const ph = d.examplePhrases?.length ? d.examplePhrases : [emptyExample()];
    setExamples(
      ph.map((p) => ({
        german: p.german ?? "",
        vietnamese: p.vietnamese ?? "",
        audioUrl: p.audioUrl ?? ""
      }))
    );
  }, []);

  const load = useCallback(async () => {
    if (!token || !Number.isFinite(lessonId)) return;
    setLoading(true);
    setErr(null);
    try {
      const d = await adminGetLesson(token, lessonId);
      applyDetail(d);
    } catch (e) {
      setErr(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }, [token, lessonId, applyDetail]);

  useEffect(() => {
    if (!ready || !token || user?.role !== "ADMIN") return;
    load();
  }, [ready, token, user, load]);

  useEffect(() => {
    if (!detail || editingExId != null) return;
    setExSort(detail.exercises.length + 1);
  }, [detail, editingExId]);

  const flashSaved = () => {
    setMsg(t("adminCurriculum.saved"));
    setTimeout(() => setMsg(null), 2500);
  };

  const saveLesson = async (e) => {
    e.preventDefault();
    if (!token) return;
    setErr(null);
    try {
      const mo = menschenOrder.trim();
      const usageNotes = usageNotesText
        .split("\n")
        .map((s) => s.trim())
        .filter(Boolean);
      const examplePhrases = examples
        .filter((row) => row.german.trim())
        .map((row) => ({
          german: row.german.trim(),
          vietnamese: (row.vietnamese ?? "").trim(),
          audioUrl: (row.audioUrl ?? "").trim() || null
        }));
      const d = await adminReplaceLesson(token, lessonId, {
        title: title.trim(),
        level,
        category,
        xpReward: Number(xpReward) || 0,
        menschenOrder: mo === "" ? null : Number(mo),
        usageNotes,
        examplePhrases
      });
      applyDetail(d);
      flashSaved();
    } catch (e2) {
      setErr(e2 instanceof Error ? e2.message : String(e2));
    }
  };

  const resetExerciseForm = () => {
    setEditingExId(null);
    setExType("MCQ");
    setExQuestion("");
    setExChoices("");
    setExCorrect("");
  };

  const startEditExercise = (ex) => {
    setEditingExId(ex.id);
    setExType(ex.exerciseType);
    setExQuestion(ex.questionText);
    setExChoices((ex.choices ?? []).join("\n"));
    setExCorrect(ex.correctAnswer);
    setExSort(ex.sortOrder);
  };

  const submitExercise = async (e) => {
    e.preventDefault();
    if (!token) return;
    setErr(null);
    try {
      const choicesLines = exChoices
        .split("\n")
        .map((s) => s.trim())
        .filter(Boolean);
      const payload = {
        exerciseType: exType,
        questionText: exQuestion.trim(),
        correctAnswer: exCorrect.trim(),
        choices: exType === "MCQ" ? choicesLines : undefined,
        sortOrder: editingExId ? exSort : exSort || undefined
      };
      let d;
      if (editingExId) {
        d = await adminReplaceExercise(token, editingExId, {
          ...payload,
          sortOrder: exSort
        });
      } else {
        d = await adminCreateExercise(token, lessonId, payload);
      }
      applyDetail(d);
      resetExerciseForm();
      flashSaved();
    } catch (e3) {
      setErr(e3 instanceof Error ? e3.message : String(e3));
    }
  };

  const delExercise = async (id) => {
    if (!token) return;
    if (!window.confirm("OK?")) return;
    setErr(null);
    try {
      const d = await adminDeleteExercise(token, id);
      applyDetail(d);
      resetExerciseForm();
    } catch (e4) {
      setErr(e4 instanceof Error ? e4.message : String(e4));
    }
  };

  const resetVocabForm = () => {
    setEditingVId(null);
    setVWord("");
    setVArticle("");
    setVPlural("");
    setVMeaning("");
    setVIpa("");
    setVUsage("");
    setVAudio("");
  };

  const startEditVocab = (v) => {
    setEditingVId(v.id);
    setVWord(v.word);
    setVArticle(v.article ?? "");
    setVPlural(v.pluralForm ?? "");
    setVMeaning(v.meaningVi);
    setVIpa(v.pronunciationIpa ?? "");
    setVUsage(v.usageNoteVi ?? "");
    setVAudio(v.audioUrl ?? "");
  };

  const submitVocab = async (e) => {
    e.preventDefault();
    if (!token) return;
    setErr(null);
    try {
      const payload = {
        word: vWord.trim(),
        article: vArticle.trim() || null,
        pluralForm: vPlural.trim() || null,
        meaningVi: vMeaning.trim(),
        pronunciationIpa: vIpa.trim() || null,
        usageNoteVi: vUsage.trim() || null,
        audioUrl: vAudio.trim() || null
      };
      let d;
      if (editingVId) {
        d = await adminReplaceVocabulary(token, editingVId, payload);
      } else {
        d = await adminCreateVocabulary(token, lessonId, payload);
      }
      applyDetail(d);
      resetVocabForm();
      flashSaved();
    } catch (e5) {
      setErr(e5 instanceof Error ? e5.message : String(e5));
    }
  };

  const delVocab = async (id) => {
    if (!token) return;
    if (!window.confirm("OK?")) return;
    setErr(null);
    try {
      const d = await adminDeleteVocabulary(token, id);
      applyDetail(d);
      resetVocabForm();
    } catch (e6) {
      setErr(e6 instanceof Error ? e6.message : String(e6));
    }
  };

  if (!ready) {
    return <p className="text-sm text-gray-600">{t("adminCurriculum.loading")}</p>;
  }
  if (!token || user?.role !== "ADMIN") {
    return <p className="text-sm text-red-700">{t("adminCurriculum.forbidden")}</p>;
  }
  if (!Number.isFinite(lessonId)) {
    return <p className="text-sm text-red-700">Invalid id</p>;
  }
  if (loading && !detail) {
    return <p className="text-sm text-gray-600">{t("adminCurriculum.loading")}</p>;
  }
  if (!detail) {
    return <p className="text-sm text-red-700">{t("adminCurriculum.error")}</p>;
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <Link href="/admin/curriculum" className="text-sm text-blue-700 underline">
          ← {t("adminCurriculum.backToList")}
        </Link>
        <Link
          href={`/lessons/${lessonId}/content`}
          className="text-sm text-gray-700 underline"
          target="_blank"
          rel="noreferrer"
        >
          {t("adminCurriculum.previewLearner")}
        </Link>
      </div>

      {msg && <div className="rounded-md border border-green-200 bg-green-50 p-2 text-sm text-green-900">{msg}</div>}
      {err && <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">{err}</div>}

      <form onSubmit={saveLesson} className="space-y-4 rounded-md border border-gray-200 p-4">
        <h1 className="text-lg font-semibold">{detail.lesson.title}</h1>
        <div className="grid gap-3 md:grid-cols-2">
          <label className="block text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colTitle")}</span>
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1.5"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </label>
          <label className="block text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colLevel")}</span>
            <select
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1.5"
              value={level}
              onChange={(e) => setLevel(e.target.value)}
            >
              {LEVELS.map((lv) => (
                <option key={lv} value={lv}>
                  {lv}
                </option>
              ))}
            </select>
          </label>
          <label className="block text-sm">
            <span className="text-gray-600">Category</span>
            <select
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1.5"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
            >
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </label>
          <label className="block text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colXp")}</span>
            <input
              type="number"
              min={0}
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1.5"
              value={xpReward}
              onChange={(e) => setXpReward(Number(e.target.value))}
            />
          </label>
          <label className="block text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colMenschen")}</span>
            <input
              type="number"
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1.5"
              value={menschenOrder}
              onChange={(e) => setMenschenOrder(e.target.value)}
              placeholder="—"
            />
          </label>
        </div>
        <label className="block text-sm">
          <span className="text-gray-600">{t("adminCurriculum.usageNotesLabel")}</span>
          <textarea
            className="mt-1 w-full rounded border border-gray-300 px-2 py-1.5 font-mono text-xs"
            rows={5}
            value={usageNotesText}
            onChange={(e) => setUsageNotesText(e.target.value)}
          />
        </label>

        <div>
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-gray-800">{t("adminCurriculum.examplesLabel")}</span>
            <button
              type="button"
              className="text-sm text-blue-700 underline"
              onClick={() => setExamples((prev) => [...prev, emptyExample()])}
            >
              {t("adminCurriculum.addExampleRow")}
            </button>
          </div>
          <div className="mt-2 space-y-2">
            {examples.map((row, i) => (
              <div key={i} className="grid gap-2 rounded border border-gray-100 p-2 md:grid-cols-3">
                <input
                  className="rounded border border-gray-300 px-2 py-1 text-sm"
                  placeholder="Deutsch"
                  value={row.german}
                  onChange={(e) => {
                    const next = [...examples];
                    next[i] = { ...next[i], german: e.target.value };
                    setExamples(next);
                  }}
                />
                <input
                  className="rounded border border-gray-300 px-2 py-1 text-sm"
                  placeholder="Tiếng Việt"
                  value={row.vietnamese}
                  onChange={(e) => {
                    const next = [...examples];
                    next[i] = { ...next[i], vietnamese: e.target.value };
                    setExamples(next);
                  }}
                />
                <input
                  className="rounded border border-gray-300 px-2 py-1 text-sm"
                  placeholder={t("adminCurriculum.audioUrl")}
                  value={row.audioUrl}
                  onChange={(e) => {
                    const next = [...examples];
                    next[i] = { ...next[i], audioUrl: e.target.value };
                    setExamples(next);
                  }}
                />
              </div>
            ))}
          </div>
        </div>

        <button type="submit" className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white">
          {t("adminCurriculum.saveLesson")}
        </button>
      </form>

      <section className="rounded-md border border-gray-200 p-4">
        <h2 className="text-base font-semibold">{t("adminCurriculum.exercisesSection")}</h2>
        <ul className="mt-2 space-y-2 text-sm">
          {detail.exercises.map((ex) => (
            <li key={ex.id} className="flex flex-wrap items-start justify-between gap-2 border-b border-gray-100 py-2">
              <div>
                <span className="font-mono text-xs text-gray-500">{ex.exerciseType}</span> #{ex.sortOrder}
                <div className="mt-0.5">{ex.questionText}</div>
              </div>
              <div className="flex gap-2">
                <button type="button" className="text-blue-700 underline" onClick={() => startEditExercise(ex)}>
                  {t("adminCurriculum.editRow")}
                </button>
                <button type="button" className="text-red-700 underline" onClick={() => delExercise(ex.id)}>
                  {t("adminCurriculum.delete")}
                </button>
              </div>
            </li>
          ))}
        </ul>

        <form onSubmit={submitExercise} className="mt-4 space-y-2 rounded-md bg-gray-50 p-3">
          <div className="text-sm font-medium text-gray-800">
            {editingExId ? `${t("adminCurriculum.updateExercise")} #${editingExId}` : t("adminCurriculum.addExercise")}
          </div>
          <div className="flex flex-wrap gap-2">
            <label className="text-sm">
              {t("adminCurriculum.exerciseType")}
              <select
                className="ml-1 rounded border border-gray-300 px-2 py-1"
                value={exType}
                onChange={(e) => setExType(e.target.value)}
              >
                {EX_TYPES.map((x) => (
                  <option key={x} value={x}>
                    {x}
                  </option>
                ))}
              </select>
            </label>
            <label className="text-sm">
              {t("adminCurriculum.sortOrder")}
              <input
                type="number"
                className="ml-1 w-20 rounded border border-gray-300 px-2 py-1"
                value={exSort}
                onChange={(e) => setExSort(Number(e.target.value))}
                required
              />
            </label>
          </div>
          <label className="block text-sm">
            {t("adminCurriculum.question")}
            <textarea
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              rows={2}
              value={exQuestion}
              onChange={(e) => setExQuestion(e.target.value)}
              required
            />
          </label>
          {exType === "MCQ" && (
            <label className="block text-sm">
              {t("adminCurriculum.choicesLines")}
              <textarea
                className="mt-1 w-full rounded border border-gray-300 px-2 py-1 font-mono text-xs"
                rows={4}
                value={exChoices}
                onChange={(e) => setExChoices(e.target.value)}
              />
            </label>
          )}
          <label className="block text-sm">
            {t("adminCurriculum.correctAnswer")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={exCorrect}
              onChange={(e) => setExCorrect(e.target.value)}
              required
            />
          </label>
          <div className="flex gap-2">
            <button type="submit" className="rounded-md bg-gray-900 px-3 py-1.5 text-sm text-white">
              {editingExId ? t("adminCurriculum.updateExercise") : t("adminCurriculum.addExercise")}
            </button>
            {editingExId ? (
              <button type="button" className="text-sm underline" onClick={resetExerciseForm}>
                {t("adminCurriculum.cancelEdit")}
              </button>
            ) : null}
          </div>
        </form>
      </section>

      <section className="rounded-md border border-gray-200 p-4">
        <h2 className="text-base font-semibold">{t("adminCurriculum.vocabSection")}</h2>
        <ul className="mt-2 space-y-2 text-sm">
          {detail.vocabularies.map((v) => (
            <li key={v.id} className="flex flex-wrap justify-between gap-2 border-b border-gray-100 py-2">
              <div>
                <span className="font-medium">{v.article ? `${v.article} ` : ""}{v.word}</span>
                <span className="text-gray-600"> — {v.meaningVi}</span>
              </div>
              <div className="flex gap-2">
                <button type="button" className="text-blue-700 underline" onClick={() => startEditVocab(v)}>
                  {t("adminCurriculum.editRow")}
                </button>
                <button type="button" className="text-red-700 underline" onClick={() => delVocab(v.id)}>
                  {t("adminCurriculum.delete")}
                </button>
              </div>
            </li>
          ))}
        </ul>

        <form onSubmit={submitVocab} className="mt-4 grid gap-2 rounded-md bg-gray-50 p-3 md:grid-cols-2">
          <div className="md:col-span-2 text-sm font-medium">
            {editingVId ? `${t("adminCurriculum.updateVocab")} #${editingVId}` : t("adminCurriculum.addVocab")}
          </div>
          <label className="text-sm">
            {t("adminCurriculum.word")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={vWord}
              onChange={(e) => setVWord(e.target.value)}
              required
            />
          </label>
          <label className="text-sm">
            {t("adminCurriculum.article")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={vArticle}
              onChange={(e) => setVArticle(e.target.value)}
            />
          </label>
          <label className="text-sm">
            {t("adminCurriculum.plural")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={vPlural}
              onChange={(e) => setVPlural(e.target.value)}
            />
          </label>
          <label className="text-sm">
            {t("adminCurriculum.meaningVi")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={vMeaning}
              onChange={(e) => setVMeaning(e.target.value)}
              required
            />
          </label>
          <label className="text-sm">
            {t("adminCurriculum.ipa")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={vIpa}
              onChange={(e) => setVIpa(e.target.value)}
            />
          </label>
          <label className="text-sm md:col-span-2">
            {t("adminCurriculum.usageNote")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={vUsage}
              onChange={(e) => setVUsage(e.target.value)}
            />
          </label>
          <label className="text-sm md:col-span-2">
            {t("adminCurriculum.audioUrl")}
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1"
              value={vAudio}
              onChange={(e) => setVAudio(e.target.value)}
            />
          </label>
          <div className="flex gap-2 md:col-span-2">
            <button type="submit" className="rounded-md bg-gray-900 px-3 py-1.5 text-sm text-white">
              {editingVId ? t("adminCurriculum.updateVocab") : t("adminCurriculum.addVocab")}
            </button>
            {editingVId ? (
              <button type="button" className="text-sm underline" onClick={resetVocabForm}>
                {t("adminCurriculum.cancelEdit")}
              </button>
            ) : null}
          </div>
        </form>
      </section>
    </div>
  );
}
