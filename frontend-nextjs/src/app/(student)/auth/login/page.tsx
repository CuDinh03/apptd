"use client";

import { type FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { LogIn } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { useLoginMutation } from "@/hooks/useAuthMutations";
import StudentPageHint from "@/components/StudentPageHint";
import { useLanguage } from "@/contexts/LanguageContext";

export default function LoginPage() {
  const { signIn } = useAuth();
  const { t } = useLanguage();
  const router = useRouter();
  const [username, setUsername] = useState("student");
  const [password, setPassword] = useState("Student123");
  const [error, setError] = useState<string | null>(null);
  const loginMutation = useLoginMutation();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      const auth = await loginMutation.mutateAsync({ username, password });
      signIn(auth);
      router.push("/progress");
    } catch (err) {
      setError(err instanceof Error ? err.message : t("auth.loginFailed"));
    }
  };

  return (
    <div className="mx-auto max-w-md space-y-4">
      <div className="flex items-center gap-2 text-xl font-semibold">
        <LogIn className="h-6 w-6" aria-hidden />
        {t("auth.loginTitle")}
      </div>
      <StudentPageHint>
        <span className="block">{t("auth.loginPageHint")}</span>
        <span className="mt-1 block text-xs opacity-90">{t("auth.seedHint")}</span>
      </StudentPageHint>
      <form onSubmit={onSubmit} className="space-y-3 rounded-md border border-gray-200 bg-white p-4">
        <label className="block text-sm font-medium">
          {t("auth.username")}
          <input
            className="mt-1 w-full rounded-md border border-gray-300 p-2 text-sm"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
          />
        </label>
        <label className="block text-sm font-medium">
          {t("auth.password")}
          <input
            type="password"
            className="mt-1 w-full rounded-md border border-gray-300 p-2 text-sm"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </label>
        {error && <div className="rounded-md bg-red-50 p-2 text-sm text-red-800">{error}</div>}
        <button
          type="submit"
          disabled={loginMutation.isPending}
          className="w-full rounded-md bg-gray-900 py-2 text-sm font-medium text-white disabled:opacity-60"
        >
          {loginMutation.isPending ? "…" : t("nav.login")}
        </button>
      </form>
      <p className="text-sm text-gray-600">
        {t("auth.noAccount")}{" "}
        <Link href="/auth/register" className="font-medium underline">
          {t("nav.register")}
        </Link>
      </p>
    </div>
  );
}
