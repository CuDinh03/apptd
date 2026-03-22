"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";

import { adminCreateLesson, adminDeleteLesson, adminListLessons } from "@/lib/api";
import { useAuth } from "@/contexts/AuthContext";
import { useLanguage } from "@/contexts/LanguageContext";

const LEVELS = ["A1", "A2", "B1", "B2"];
const CATEGORIES = ["GRAMMAR", "VOCABULARY", "COMMUNICATION"];

export default function AdminCurriculumListPage() {
  const { t } = useLanguage();
  const { token, user, ready } = useAuth();
  const [levelFilter, setLevelFilter] = useState("");
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState(null);

  const [title, setTitle] = useState("");
  const [newLevel, setNewLevel] = useState("A1");
  const [newCategory, setNewCategory] = useState("VOCABULARY");
  const [xpReward, setXpReward] = useState(20);
  const [menschenOrder, setMenschenOrder] = useState("");
  const [creating, setCreating] = useState(false);

  const load = useCallback(async () => {
    if (!token) return;
    setLoading(true);
    setErr(null);
    try {
      const data = await adminListLessons(token, levelFilter || undefined);
      setRows(data);
    } catch (e) {
      setErr(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }, [token, levelFilter]);

  useEffect(() => {
    if (!ready || !token || user?.role !== "ADMIN") return;
    load();
  }, [ready, token, user, load]);

  const onCreate = async (e) => {
    e.preventDefault();
    if (!token || !title.trim()) return;
    setCreating(true);
    setErr(null);
    try {
      const mo = menschenOrder.trim();
      const detail = await adminCreateLesson(token, {
        title: title.trim(),
        level: newLevel,
        category: newCategory,
        xpReward: Number(xpReward) || 0,
        menschenOrder: mo === "" ? null : Number(mo)
      });
      setTitle("");
      setMenschenOrder("");
      window.location.href = `/admin/curriculum/lessons/${detail.lesson.id}`;
    } catch (e2) {
      setErr(e2 instanceof Error ? e2.message : String(e2));
    } finally {
      setCreating(false);
    }
  };

  const onDelete = async (id) => {
    if (!token) return;
    if (!window.confirm(t("adminCurriculum.confirmDeleteLesson"))) return;
    setErr(null);
    try {
      await adminDeleteLesson(token, id);
      await load();
    } catch (e3) {
      setErr(e3 instanceof Error ? e3.message : String(e3));
    }
  };

  if (!ready) {
    return <p className="text-sm text-gray-600">{t("adminCurriculum.loading")}</p>;
  }
  if (!token) {
    return <p className="text-sm text-amber-800">{t("adminCurriculum.needLogin")}</p>;
  }
  if (user?.role !== "ADMIN") {
    return <p className="text-sm text-red-700">{t("adminCurriculum.forbidden")}</p>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">{t("adminCurriculum.title")}</h1>
        <p className="mt-1 text-sm text-gray-600">{t("adminCurriculum.subtitle")}</p>
      </div>

      {err && (
        <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">{err}</div>
      )}

      <form onSubmit={onCreate} className="rounded-md border border-gray-200 bg-gray-50 p-4">
        <h2 className="text-sm font-semibold text-gray-900">{t("adminCurriculum.newLesson")}</h2>
        <div className="mt-3 flex flex-wrap items-end gap-3">
          <label className="block min-w-[12rem] flex-1 text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colTitle")}</span>
            <input
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1.5"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </label>
          <label className="text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colLevel")}</span>
            <select
              className="mt-1 block w-full rounded border border-gray-300 px-2 py-1.5"
              value={newLevel}
              onChange={(e) => setNewLevel(e.target.value)}
            >
              {LEVELS.map((lv) => (
                <option key={lv} value={lv}>
                  {lv}
                </option>
              ))}
            </select>
          </label>
          <label className="text-sm">
            <span className="text-gray-600">Category</span>
            <select
              className="mt-1 block w-full rounded border border-gray-300 px-2 py-1.5"
              value={newCategory}
              onChange={(e) => setNewCategory(e.target.value)}
            >
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </label>
          <label className="text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colXp")}</span>
            <input
              type="number"
              min={0}
              className="mt-1 w-24 rounded border border-gray-300 px-2 py-1.5"
              value={xpReward}
              onChange={(e) => setXpReward(Number(e.target.value))}
            />
          </label>
          <label className="text-sm">
            <span className="text-gray-600">{t("adminCurriculum.colMenschen")}</span>
            <input
              type="number"
              className="mt-1 w-24 rounded border border-gray-300 px-2 py-1.5"
              value={menschenOrder}
              onChange={(e) => setMenschenOrder(e.target.value)}
              placeholder="—"
            />
          </label>
          <button
            type="submit"
            disabled={creating}
            className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
          >
            {creating ? "…" : t("adminCurriculum.createLesson")}
          </button>
        </div>
      </form>

      <div className="flex flex-wrap items-center gap-2">
        <span className="text-sm text-gray-600">{t("adminCurriculum.filterLevel")}</span>
        <select
          className="rounded border border-gray-300 px-2 py-1 text-sm"
          value={levelFilter}
          onChange={(e) => setLevelFilter(e.target.value)}
        >
          <option value="">{t("adminCurriculum.allLevels")}</option>
          {LEVELS.map((lv) => (
            <option key={lv} value={lv}>
              {lv}
            </option>
          ))}
        </select>
        <button
          type="button"
          onClick={() => load()}
          className="rounded border border-gray-300 bg-white px-3 py-1 text-sm hover:bg-gray-50"
        >
          Refresh
        </button>
      </div>

      {loading ? (
        <p className="text-sm text-gray-600">{t("adminCurriculum.loading")}</p>
      ) : (
        <div className="overflow-x-auto rounded-md border border-gray-200">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-gray-50 text-xs font-medium uppercase text-gray-600">
              <tr>
                <th className="px-3 py-2">{t("adminCurriculum.colTitle")}</th>
                <th className="px-3 py-2">{t("adminCurriculum.colLevel")}</th>
                <th className="px-3 py-2">Category</th>
                <th className="px-3 py-2">{t("adminCurriculum.colXp")}</th>
                <th className="px-3 py-2">{t("adminCurriculum.colMenschen")}</th>
                <th className="px-3 py-2">{t("adminCurriculum.colEx")}</th>
                <th className="px-3 py-2">{t("adminCurriculum.colVocab")}</th>
                <th className="px-3 py-2">{t("adminCurriculum.actions")}</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id} className="border-t border-gray-100">
                  <td className="px-3 py-2 font-medium">{r.title}</td>
                  <td className="px-3 py-2">{r.level}</td>
                  <td className="px-3 py-2">{r.category}</td>
                  <td className="px-3 py-2">{r.xpReward}</td>
                  <td className="px-3 py-2">{r.menschenOrder ?? "—"}</td>
                  <td className="px-3 py-2">{r.exerciseCount}</td>
                  <td className="px-3 py-2">{r.vocabularyCount}</td>
                  <td className="px-3 py-2">
                    <div className="flex flex-wrap gap-2">
                      <Link className="text-blue-700 underline" href={`/admin/curriculum/lessons/${r.id}`}>
                        {t("adminCurriculum.edit")}
                      </Link>
                      <button
                        type="button"
                        className="text-red-700 underline"
                        onClick={() => onDelete(r.id)}
                      >
                        {t("adminCurriculum.deleteLesson")}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
