"use client";

import Link from "next/link";
import { type ReactNode } from "react";

import LanguageSwitcher from "@/components/LanguageSwitcher";
import { useAuth } from "@/contexts/AuthContext";
import { useLanguage } from "@/contexts/LanguageContext";

/** Giao diện quản trị: tông tối, điều hướng tách biệt với học viên. */
export default function AdminLayoutShell({ children }: { children: ReactNode }) {
  const { t } = useLanguage();
  const { user, signOut, ready } = useAuth();
  const canAccess = Boolean(ready && user?.role === "ADMIN");

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="border-b border-slate-700 bg-slate-900 text-slate-100 shadow-md">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-3 px-4 py-3">
          <div className="flex flex-wrap items-center gap-4">
            <Link href="/admin/curriculum" className="text-lg font-bold tracking-tight text-white hover:underline">
              {t("adminLayout.brand")}
            </Link>
            <span className="hidden text-xs text-slate-400 sm:inline" aria-hidden>
              |
            </span>
            <p className="hidden text-xs text-slate-400 sm:block">{t("adminLayout.tagline")}</p>
          </div>
          <div className="flex flex-wrap items-center gap-3 sm:gap-4">
            <nav className="flex flex-wrap items-center gap-3 text-sm">
              <Link
                href="/admin/curriculum"
                className="rounded-md px-2 py-1 font-medium text-slate-200 hover:bg-slate-800 hover:text-white"
              >
                {t("adminLayout.navCurriculum")}
              </Link>
              <Link
                href="/"
                className="rounded-md px-2 py-1 font-medium text-amber-200/90 hover:bg-slate-800 hover:text-amber-100"
              >
                {t("adminLayout.backToStudent")}
              </Link>
            </nav>
            <LanguageSwitcher variant="dark" />
            {ready && user ? (
              <div className="flex items-center gap-2 border-l border-slate-600 pl-3">
                <span className="max-w-[8rem] truncate text-xs text-slate-400" title={user.username}>
                  {user.username}
                </span>
                <button
                  type="button"
                  onClick={() => signOut()}
                  className="rounded-md border border-slate-500 bg-slate-800 px-2 py-1 text-xs font-medium text-white hover:bg-slate-700"
                >
                  {t("nav.logout")}
                </button>
              </div>
            ) : null}
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-6xl px-4 py-6">
        {!ready ? (
          <p className="text-sm text-slate-600">{t("adminLayout.loading")}</p>
        ) : !canAccess ? (
          <div className="rounded-lg border border-amber-400/60 bg-amber-50 p-6 text-sm text-amber-950 shadow-sm">
            <p className="font-medium">{user ? t("adminLayout.forbidden") : t("adminLayout.needLogin")}</p>
            <Link
              href="/auth/login"
              className="mt-4 inline-block rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
            >
              {t("nav.login")}
            </Link>
          </div>
        ) : (
          children
        )}
      </main>
      <footer className="border-t border-slate-200 bg-slate-50 py-4 text-center text-xs text-slate-500">
        {t("adminLayout.footerHint")}
      </footer>
    </div>
  );
}
