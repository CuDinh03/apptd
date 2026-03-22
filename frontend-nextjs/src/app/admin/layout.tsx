import type { Metadata } from "next";

import AdminLayoutShell from "@/components/AdminLayoutShell";

export const metadata: Metadata = {
  title: { default: "Admin · Curriculum", template: "%s · Admin" },
  description: "Quản trị lộ trình và nội dung bài học"
};

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  return <AdminLayoutShell>{children}</AdminLayoutShell>;
}
