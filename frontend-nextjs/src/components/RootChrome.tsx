"use client";

import { usePathname } from "next/navigation";
import { type ReactNode } from "react";

import StudentLayoutShell from "@/components/StudentLayoutShell";

/**
 * Chỉ bọc shell học viên một lần; route /admin để layout admin tự quản (tránh 2 header).
 */
export default function RootChrome({ children }: { children: ReactNode }) {
  const pathname = usePathname() ?? "";
  if (pathname.startsWith("/admin")) {
    return <>{children}</>;
  }
  return <StudentLayoutShell>{children}</StudentLayoutShell>;
}
