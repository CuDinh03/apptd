"use client";

import StudentPageHint from "@/components/StudentPageHint";
import { useLanguage } from "@/contexts/LanguageContext";

export default function HomePage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-3">
      <h1 className="text-2xl font-bold">{t("home.title")}</h1>
      <StudentPageHint>{t("home.hint")}</StudentPageHint>
    </div>
  );
}

