"use client";

import Link from "next/link";

import { useAuth } from "@/contexts/AuthContext";
import { useLanguage } from "@/contexts/LanguageContext";

export default function SessionFooter() {
  const { t } = useLanguage();
  const { user, signOut, ready } = useAuth();

  if (!ready) {
    return null;
  }

  if (user) {
    return (
      <div className="w-full max-w-xl rounded-md border border-gray-200 bg-gray-50 p-4">
        <div className="text-sm font-semibold">{t("session.title")}</div>
        <p className="mt-2 text-sm text-gray-800">
          <span className="font-medium">{t("session.loggedIn")}</span> {user.username}
          <span className="text-gray-600">
            {" "}
            ({t("session.role")}: {user.role})
          </span>
        </p>
        <p className="mt-2 text-xs text-gray-600">{t("session.hintCloseBrowser")}</p>
        {user.role === "ADMIN" ? (
          <p className="mt-3">
            <Link
              href="/admin/curriculum"
              className="text-sm font-semibold text-emerald-800 underline decoration-emerald-600 hover:text-emerald-950"
            >
              {t("session.adminPanelCta")}
            </Link>
          </p>
        ) : null}
        <button
          type="button"
          className="mt-3 rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white"
          onClick={() => signOut()}
        >
          {t("nav.logout")}
        </button>
      </div>
    );
  }

  return (
    <div className="w-full max-w-xl rounded-md border border-amber-200 bg-amber-50 p-4">
      <div className="text-sm font-semibold">{t("session.title")}</div>
      <p className="mt-2 text-sm text-gray-800">{t("session.notLoggedIn")}</p>
      <Link
        href="/auth/login"
        className="mt-3 inline-block rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white"
      >
        {t("session.signInCta")}
      </Link>
    </div>
  );
}
