"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode
} from "react";

import {
  defaultLocale,
  dictionaries,
  getMessage,
  isLocale,
  LOCALE_STORAGE_KEY,
  type Locale,
  type Messages
} from "@/i18n/dictionaries";

type LanguageContextValue = {
  locale: Locale;
  setLocale: (locale: Locale) => void;
  t: (path: string) => string;
  messages: Messages;
};

const LanguageContext = createContext<LanguageContextValue | null>(null);

export function LanguageProvider({ children }: { children: ReactNode }) {
  const [locale, setLocaleState] = useState<Locale>(defaultLocale);
  const [hydrated, setHydrated] = useState(false);

  useEffect(() => {
    try {
      const raw = window.localStorage.getItem(LOCALE_STORAGE_KEY);
      if (isLocale(raw)) {
        setLocaleState(raw);
      }
    } catch {
      // ignore
    }
    setHydrated(true);
  }, []);

  const setLocale = useCallback((next: Locale) => {
    setLocaleState(next);
    try {
      window.localStorage.setItem(LOCALE_STORAGE_KEY, next);
    } catch {
      // ignore
    }
    if (typeof document !== "undefined") {
      document.documentElement.lang = next === "vi" ? "vi" : next === "de" ? "de" : "en";
    }
  }, []);

  useEffect(() => {
    if (!hydrated) return;
    if (typeof document !== "undefined") {
      document.documentElement.lang = locale === "vi" ? "vi" : locale === "de" ? "de" : "en";
    }
  }, [locale, hydrated]);

  const messages = dictionaries[locale];

  const t = useCallback((path: string) => getMessage(messages, path), [messages]);

  const value = useMemo(
    () => ({ locale, setLocale, t, messages }),
    [locale, setLocale, t, messages]
  );

  return <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>;
}

export function useLanguage(): LanguageContextValue {
  const ctx = useContext(LanguageContext);
  if (!ctx) {
    throw new Error("useLanguage must be used within LanguageProvider");
  }
  return ctx;
}
