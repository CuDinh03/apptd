import type { Metadata } from "next";

/**
 * Nhóm route học viên: chỉ metadata — shell nằm ở RootChrome (một lần, tránh 2 header).
 */
export const metadata: Metadata = {
  title: { default: "German Learning", template: "%s · German Learning" },
  description: "Học tiếng Đức A1–B2"
};

export default function StudentGroupLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}
