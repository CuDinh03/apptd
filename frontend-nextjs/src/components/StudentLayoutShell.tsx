"use client";

import Link from "next/link";
import { type ReactNode } from "react";

import SessionFooter from "@/components/SessionFooter";
import LanguageSwitcher from "@/components/LanguageSwitcher";
import { useAuth } from "@/contexts/AuthContext";
import { useLanguage } from "@/contexts/LanguageContext";

/** Giao diện học viên: header sáng, điều hướng bài học / tiến độ / AI. */
export default function StudentLayoutShell({ children }: { children: ReactNode }) {
  const { t } = useLanguage();
  const { user, signOut, ready } = useAuth();

  return (
    <div className="min-h-screen bg-white text-gray-900">
      <header className="border-b border-emerald-200/80 bg-gradient-to-r from-emerald-50 to-white">
        <div className="mx-auto flex max-w-5xl flex-wrap items-center justify-between gap-3 px-4 py-3">
          <Link href="/" className="text-lg font-semibold text-emerald-900 hover:underline">
            {t("nav.brand")}
          </Link>
          <div className="flex flex-wrap items-center gap-3 sm:gap-4">
            <nav className="flex flex-wrap items-center gap-3 text-sm sm:gap-4">
              <Link href="/lessons" className="font-medium text-gray-800 hover:text-emerald-800 hover:underline">
                {t("nav.lessons")}
              </Link>
              <Link href="/progress" className="font-medium text-gray-800 hover:text-emerald-800 hover:underline">
                {t("nav.progress")}
              </Link>
              <Link href="/ai-speech" className="font-medium text-gray-800 hover:text-emerald-800 hover:underline">
                {t("nav.aiSpeech")}
              </Link>
              {ready && user ? (
                <>
                  <span className="text-gray-400" aria-hidden>
                    ·
                  </span>
                  <span className="max-w-[10rem] truncate text-xs text-gray-600 sm:max-w-none" title={user.username}>
                    {user.username}
                  </span>
                  <button
                    type="button"
                    onClick={() => signOut()}
                    className="rounded-md border border-gray-300 bg-white px-2 py-1 text-sm font-medium hover:bg-gray-100"
                  >
                    {t("nav.logout")}
                  </button>
                </>
              ) : (
                <>
                  <Link href="/auth/login" className="font-medium hover:underline">
                    {t("nav.login")}
                  </Link>
                  <Link href="/auth/register" className="font-medium hover:underline">
                    {t("nav.register")}
                  </Link>
                </>
              )}
            </nav>
            <LanguageSwitcher />
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-5xl px-4 py-6">
        {children}
        <div className="mt-6">
          <SessionFooter />
        </div>
      </main>
    </div>
  );
}
