"use client";

import { type FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { UserPlus } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { useRegisterMutation } from "@/hooks/useAuthMutations";
import { useLanguage } from "@/contexts/LanguageContext";

export default function RegisterPage() {
  const { signIn } = useAuth();
  const { t } = useLanguage();
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const registerMutation = useRegisterMutation();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      const auth = await registerMutation.mutateAsync({ username, password });
      signIn(auth);
      router.push("/progress");
    } catch (err) {
      setError(err instanceof Error ? err.message : t("auth.registerFailed"));
    }
  };

  return (
    <div className="mx-auto max-w-md space-y-4">
      <div className="flex items-center gap-2 text-xl font-semibold">
        <UserPlus className="h-6 w-6" aria-hidden />
        {t("auth.registerTitle")}
      </div>
      <p className="text-sm text-gray-600">{t("auth.registerRules")}</p>
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
            autoComplete="new-password"
          />
        </label>
        {error && <div className="rounded-md bg-red-50 p-2 text-sm text-red-800">{error}</div>}
        <button
          type="submit"
          disabled={registerMutation.isPending}
          className="w-full rounded-md bg-gray-900 py-2 text-sm font-medium text-white disabled:opacity-60"
        >
          {registerMutation.isPending ? "…" : t("nav.register")}
        </button>
      </form>
      <p className="text-sm text-gray-600">
        {t("auth.hasAccount")}{" "}
        <Link href="/auth/login" className="font-medium underline">
          {t("nav.login")}
        </Link>
      </p>
    </div>
  );
}
