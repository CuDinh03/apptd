"use client";

import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchLessons, LessonDto } from "@/lib/api";
import { queryKeys } from "@/lib/query-keys";
import Link from "next/link";

import { useLanguage } from "@/contexts/LanguageContext";

const LEVELS = ["A1", "A2", "B1", "B2"] as const;

export default function LessonsPage() {
  const { t } = useLanguage();
  const [level, setLevel] = useState<(typeof LEVELS)[number]>("A1");

  const query = useQuery({
    queryKey: queryKeys.lessons(level),
    queryFn: () => fetchLessons(level),
    staleTime: 30_000
  });

  const items = useMemo(() => {
    const list = query.data ? [...query.data] : [];
    list.sort((a, b) => {
      const ao = a.menschenOrder ?? 9999;
      const bo = b.menschenOrder ?? 9999;
      if (ao !== bo) return ao - bo;
      return a.id - b.id;
    });
    return list;
  }, [query.data]);

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-xl font-bold">{t("lessons.title")}</h1>
        <div className="flex items-center gap-2">
          <label className="text-sm font-medium">{t("lessons.level")}</label>
          <select
            className="rounded-md border border-gray-300 p-2 text-sm"
            value={level}
            onChange={(e) => setLevel(e.target.value as (typeof LEVELS)[number])}
          >
            {LEVELS.map((l) => (
              <option key={l} value={l}>
                {l}
              </option>
            ))}
          </select>
        </div>
      </div>

      {query.isLoading && <div className="text-sm text-gray-700">{t("lessons.loading")}</div>}
      {query.isError && (
        <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">
          {t("lessons.error")}
        </div>
      )}

      <div className="grid gap-3 md:grid-cols-2">
        {items.map((l: LessonDto) => (
          <div key={l.id} className="rounded-md border border-gray-200 bg-white p-4">
            <div className="flex items-start justify-between gap-3">
              <div>
                <div className="text-sm text-gray-600">{l.level} · {l.category}</div>
                <div className="mt-1 text-base font-semibold">{l.title}</div>
              </div>
              <div className="rounded-md bg-gray-900 px-2 py-1 text-xs font-medium text-white">
                +{l.xpReward} XP
              </div>
            </div>
            <div className="mt-3">
              <Link href={`/lessons/${l.id}/content`} className="text-sm font-medium underline">
                {t("lessons.viewContent")}
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

