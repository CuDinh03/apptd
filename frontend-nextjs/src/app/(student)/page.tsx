"use client";

import { useLanguage } from "@/contexts/LanguageContext";

export default function HomePage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-3">
      <h1 className="text-2xl font-bold">{t("home.title")}</h1>
      <p className="text-sm text-gray-700">{t("home.intro")}</p>
      <div className="rounded-md border border-gray-200 bg-white p-4">
        <div className="text-sm font-semibold">{t("home.noteTitle")}</div>
        <p className="mt-1 text-sm text-gray-700">{t("home.noteBody")}</p>
      </div>
    </div>
  );
}

