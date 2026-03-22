"use client";

import { useLanguage } from "@/contexts/LanguageContext";
import type { Locale } from "@/i18n/dictionaries";

const OPTIONS: { value: Locale; labelKey: string }[] = [
  { value: "en", labelKey: "lang.en" },
  { value: "de", labelKey: "lang.de" },
  { value: "vi", labelKey: "lang.vi" }
];

type LanguageSwitcherProps = { variant?: "default" | "dark" };

export default function LanguageSwitcher({ variant = "default" }: LanguageSwitcherProps) {
  const { locale, setLocale, t } = useLanguage();
  const isDark = variant === "dark";

  return (
    <label className="flex items-center gap-2 text-sm">
      <span
        className={`hidden font-medium sm:inline ${isDark ? "text-slate-300" : "text-gray-600"}`}
      >
        {t("lang.label")}
      </span>
      <select
        className={
          isDark
            ? "rounded-md border border-slate-600 bg-slate-800 px-2 py-1 text-sm text-slate-100"
            : "rounded-md border border-gray-300 bg-white px-2 py-1 text-sm"
        }
        value={locale}
        onChange={(e) => setLocale(e.target.value as Locale)}
        aria-label={t("lang.label")}
      >
        {OPTIONS.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {t(opt.labelKey)}
          </option>
        ))}
      </select>
    </label>
  );
}
