"use client";

import { type ReactNode } from "react";

import { AuthProvider } from "@/contexts/AuthContext";
import { LanguageProvider } from "@/contexts/LanguageContext";
import ReactQueryProvider from "@/components/ReactQueryProvider";

export default function AppProviders({ children }: { children: ReactNode }) {
  return (
    <ReactQueryProvider>
      <LanguageProvider>
        <AuthProvider>{children}</AuthProvider>
      </LanguageProvider>
    </ReactQueryProvider>
  );
}
