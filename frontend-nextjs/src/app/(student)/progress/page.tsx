"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { fetchLeaderboard, fetchMyProgress } from "@/lib/api";
import { queryKeys } from "@/lib/query-keys";

import { useAuth } from "@/contexts/AuthContext";
import StudentPageHint from "@/components/StudentPageHint";
import { useLanguage } from "@/contexts/LanguageContext";

function statusLabel(t: (k: string) => string, status: string) {
  const key = `progress.status_${status}`;
  const label = t(key);
  return label === key ? status : label;
}

export default function ProgressPage() {
  const { t } = useLanguage();
  const { token, ready, user } = useAuth();

  const myProgressQuery = useQuery({
    queryKey: queryKeys.myProgress(user?.userId ?? null),
    queryFn: () => fetchMyProgress(token!),
    enabled: ready && !!token,
    staleTime: 15_000
  });

  const leaderboardQuery = useQuery({
    queryKey: queryKeys.leaderboard(10),
    queryFn: () => fetchLeaderboard(10),
    staleTime: 30_000
  });

  return (
    <div className="space-y-4">
      <div className="rounded-md border border-gray-200 bg-white p-4">
        <h1 className="text-lg font-semibold">{t("progress.titlePage")}</h1>
        <div className="mt-2">
          <StudentPageHint>{t("progress.pageHint")}</StudentPageHint>
        </div>

        {!ready && <div className="mt-3 text-sm text-gray-600">{t("progress.loadingMyProgress")}</div>}

        {ready && !token && (
          <div className="mt-3 rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-900">
            {t("progress.loginToView")}{" "}
            <Link href="/auth/login" className="font-medium underline">
              {t("nav.login")}
            </Link>
          </div>
        )}

        {ready && token && myProgressQuery.isLoading && (
          <div className="mt-3 text-sm text-gray-700">{t("progress.loadingMyProgress")}</div>
        )}

        {ready && token && myProgressQuery.isError && (
          <div className="mt-3 rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">
            {t("progress.errorMyProgress")}
          </div>
        )}

        {ready && token && myProgressQuery.data && (
          <div className="mt-4 space-y-3">
            <div className="text-sm text-gray-700">
              <span className="font-medium">{myProgressQuery.data.username}</span>
              {" · "}
              <span className="font-semibold text-gray-900">
                {t("progress.totalXp")} {myProgressQuery.data.totalXp} XP
              </span>
            </div>

            <h2 className="text-base font-semibold">{t("progress.studiedLessons")}</h2>

            {myProgressQuery.data.lessons.length === 0 ? (
              <p className="text-sm text-gray-600">
                {t("progress.noLessonsYet")}{" "}
                <Link href="/lessons" className="font-medium underline">
                  {t("nav.lessons")}
                </Link>
              </p>
            ) : (
              <div className="overflow-x-auto rounded-md border border-gray-100">
                <table className="min-w-full text-left text-sm">
                  <thead className="bg-gray-50 text-xs font-medium uppercase text-gray-500">
                    <tr>
                      <th className="px-3 py-2">{t("progress.colOrder")}</th>
                      <th className="px-3 py-2">{t("progress.colLesson")}</th>
                      <th className="px-3 py-2">{t("progress.colLevel")}</th>
                      <th className="px-3 py-2">{t("progress.colBest")}</th>
                      <th className="px-3 py-2">{t("progress.colXpLesson")}</th>
                      <th className="px-3 py-2">{t("progress.colStatus")}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {myProgressQuery.data.lessons.map((row) => (
                      <tr key={row.lessonId} className="border-t border-gray-100">
                        <td className="px-3 py-2 text-gray-600">{row.menschenOrder ?? "—"}</td>
                        <td className="px-3 py-2">
                          <Link href={`/lessons/${row.lessonId}/content`} className="text-blue-700 hover:underline">
                            {row.lessonTitle}
                          </Link>
                        </td>
                        <td className="px-3 py-2 text-gray-700">{row.level}</td>
                        <td className="px-3 py-2 text-gray-800">{row.bestScorePercent}%</td>
                        <td className="px-3 py-2 font-medium text-gray-900">{row.xpEarnedFromLesson}</td>
                        <td className="px-3 py-2 text-gray-700">{statusLabel(t, row.status)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>

      <div className="rounded-md border border-gray-200 bg-white p-4">
        <h2 className="text-base font-semibold">{t("progress.leaderboard")}</h2>
        <p className="mt-1 text-xs text-gray-600">{t("progress.leaderboardHint")}</p>
        {leaderboardQuery.isLoading && (
          <div className="mt-2 text-sm text-gray-700">{t("progress.leaderboardLoading")}</div>
        )}
        {leaderboardQuery.isError && (
          <div className="mt-2 text-sm text-red-700">{t("progress.leaderboardError")}</div>
        )}
        {leaderboardQuery.data && (
          <div className="mt-3 space-y-2">
            {leaderboardQuery.data.map((u, idx) => (
              <div key={u.userId} className="flex items-center justify-between text-sm">
                <div className="text-gray-700">
                  {idx + 1}. {u.username}
                </div>
                <div className="font-semibold">{u.totalXp} XP</div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
